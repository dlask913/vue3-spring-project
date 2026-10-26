"""Exercise real release-switch/rollback code with fake AWS/systemd/readiness."""
import hashlib
import os
from pathlib import Path
import shlex
import subprocess
import tempfile
import unittest


SCRIPT = Path(__file__).resolve().parents[1] / "deploy.sh"


class ReleaseTests(unittest.TestCase):
    def run_deployment(self, *, previous=True, healthy=True, checksum=True, restart=True):
        with tempfile.TemporaryDirectory() as temporary:
            root = Path(temporary)
            app = root / "app"
            (app / "releases").mkdir(parents=True)
            old = app / "releases" / "old.jar"
            old.write_text("old release")
            if previous:
                (app / "current.jar").symlink_to(old)
            payload = root / "payload.jar"
            payload.write_bytes(b"new release")
            digest = hashlib.sha256(payload.read_bytes()).hexdigest() if checksum else "0" * 64
            harness = f"""
source {shlex.quote(SCRIPT.as_posix())}
APP_DIR={shlex.quote(app.as_posix())}
payload={shlex.quote(payload.as_posix())}
bucket=example-bucket region=ap-northeast-2 port=8081
release_id=new
jar_sha256={digest}
aws() {{ cp "$payload" "${{@: -1}}"; }}
systemctl() {{
  echo "$*" >> "$APP_DIR/systemctl.log"
  if [[ "$1" == restart && "$(readlink "$APP_DIR/current.jar")" == */new.jar ]]; then
    return {0 if restart else 1}
  fi
}}
wait_ready() {{
  [[ "$(readlink "$APP_DIR/current.jar")" == */old.jar ]] || {'true' if healthy else 'false'}
}}
deploy_release
"""
            result = subprocess.run(
                [os.environ.get("TEST_BASH", "bash"), "-c", harness],
                text=True, capture_output=True,
            )
            current = app / "current.jar"
            current_name = current.resolve().name if current.exists() else None
            log_path = app / "systemctl.log"
            log = log_path.read_text() if log_path.exists() else ""
            return result, current_name, log

    def test_success_keeps_new_release(self):
        result, current, _ = self.run_deployment()
        self.assertEqual(result.returncode, 0, result.stderr)
        self.assertEqual(current, "new.jar")

    def test_unhealthy_release_rolls_back_and_reports_failure(self):
        result, current, log = self.run_deployment(healthy=False)
        self.assertNotEqual(result.returncode, 0)
        self.assertEqual(current, "old.jar")
        self.assertEqual(log.count("restart"), 2)
        self.assertIn("Previous release is healthy again", result.stderr)

    def test_restart_failure_rolls_back(self):
        result, current, _ = self.run_deployment(restart=False)
        self.assertNotEqual(result.returncode, 0)
        self.assertEqual(current, "old.jar")

    def test_bad_checksum_does_not_restart_or_replace_current(self):
        result, current, log = self.run_deployment(checksum=False)
        self.assertNotEqual(result.returncode, 0)
        self.assertEqual(current, "old.jar")
        self.assertEqual(log, "")

    def test_first_deployment_failure_stops_service(self):
        result, current, log = self.run_deployment(previous=False, healthy=False)
        self.assertNotEqual(result.returncode, 0)
        self.assertIsNone(current)
        self.assertIn("stop noticeboard-admin.service", log)


if __name__ == "__main__":
    unittest.main()
