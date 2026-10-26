"""Send one SSM deployment and wait for its actual result; requires AWS CLI."""
import hashlib
import json
import os
from pathlib import Path
import shlex
import subprocess
import time


def aws(*args):
    return subprocess.run(
        ["aws", *args, "--output", "json"],
        text=True, capture_output=True, check=True,
    ).stdout


def main():
    bucket = os.environ["AWS_ADMIN_ARTIFACT_BUCKET"]
    release = os.environ["RELEASE_ID"]
    instance = os.environ["AWS_ADMIN_BACKEND_INSTANCE_ID"]
    region = os.environ["AWS_REGION"]
    port = os.environ["AWS_ADMIN_BACKEND_PORT"]
    jar_hash = hashlib.sha256(Path("noticeboard-admin/backend/build/libs/noticeboard-admin.jar").read_bytes()).hexdigest()
    script_hash = hashlib.sha256(Path("noticeboard-admin/deploy/deploy.sh").read_bytes()).hexdigest()
    download = shlex.join([
        "aws", "s3api", "get-object", "--region", region, "--bucket", bucket,
        "--key", f"admin-backend/{release}/deploy.sh",
    ])
    arguments = shlex.join([bucket, release, jar_hash, region, port])
    # Only artifact coordinates and hashes go into SSM, never runtime secrets.
    command = "\n".join([
        "set -eu",
        'work=$(mktemp -d)',
        "trap 'rm -f -- \"$work/deploy.sh\"; rmdir -- \"$work\"' EXIT",
        f'{download} "$work/deploy.sh" >/dev/null',
        f'printf "%s  %s\\n" {shlex.quote(script_hash)} "$work/deploy.sh" | sha256sum --check --status',
        f'bash "$work/deploy.sh" {arguments}',
    ])
    result = json.loads(aws(
        "ssm", "send-command", "--instance-ids", instance,
        "--document-name", "AWS-RunShellScript", "--timeout-seconds", "180",
        "--comment", f"admin backend {release}",
        "--parameters", json.dumps({"commands": [command], "executionTimeout": ["900"]}),
    ))
    command_id = result["Command"]["CommandId"]
    print(f"SSM command: {command_id}", flush=True)
    deadline = time.monotonic() + 1200
    while time.monotonic() < deadline:
        try:
            invocation = json.loads(aws(
                "ssm", "get-command-invocation", "--command-id", command_id,
                "--instance-id", instance,
            ))
        except subprocess.CalledProcessError as error:
            # SSM has eventual consistency immediately after SendCommand.
            if "InvocationDoesNotExist" not in error.stderr:
                raise
        else:
            status = invocation["Status"]
            if status not in {"Pending", "InProgress", "Delayed", "Cancelling"}:
                print(invocation.get("StandardOutputContent", ""))
                print(invocation.get("StandardErrorContent", ""))
                if status != "Success" or invocation.get("ResponseCode") != 0:
                    raise SystemExit(f"Deployment failed: {status} (command {command_id})")
                return
        time.sleep(5)
    raise SystemExit(f"SSM result timed out. Inspect command {command_id} before retrying.")


if __name__ == "__main__":
    main()
