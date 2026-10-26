#!/usr/bin/env bash
set -Eeuo pipefail

APP_DIR=/opt/noticeboard-admin
STATE_DIR=/var/lib/noticeboard-admin
SERVICE=noticeboard-admin.service
READY_ATTEMPTS=30
previous=''
switched=false

wait_ready() {
  local attempt body
  for ((attempt = 0; attempt < READY_ATTEMPTS; attempt++)); do
    if systemctl is-active --quiet "$SERVICE" &&
       body=$(curl --noproxy '*' --silent --show-error --fail --connect-timeout 2 --max-time 3 \
         "http://127.0.0.1:${port}/api/actuator/health/readiness" 2>/dev/null) &&
       [[ "$body" =~ \"status\"[[:space:]]*:[[:space:]]*\"UP\" ]]; then
      return 0
    fi
    sleep 2
  done
  return 1
}

point_current() {
  ln -sfn "$1" "$APP_DIR/current.jar.next"
  mv -Tf "$APP_DIR/current.jar.next" "$APP_DIR/current.jar"
}

finish() {
  local result=$?
  trap - EXIT
  if [[ $result -ne 0 && "$switched" == true ]]; then
    echo 'Deployment failed; restoring the previous release.' >&2
    if [[ -n "$previous" ]]; then
      if point_current "$previous" && systemctl restart "$SERVICE" && wait_ready; then
        echo 'Previous release is healthy again.' >&2
      else
        echo 'Rollback failed. Inspect noticeboard-admin.service through SSM.' >&2
      fi
    else
      # First deployment: there is no healthy previous version to restore.
      systemctl stop "$SERVICE" || true
      rm -f -- "$APP_DIR/current.jar"
      echo 'First deployment failed; the service has been stopped.' >&2
    fi
  fi
  exit "$result"
}

deploy_release() {
  local release="$APP_DIR/releases/$release_id.jar"
  local staging="$APP_DIR/releases/$release_id.part"
  # Download and verify completely before touching the running application.
  aws s3api get-object --region "$region" --bucket "$bucket" \
    --key "admin-backend/$release_id/noticeboard-admin.jar" "$staging" >/dev/null
  printf '%s  %s\n' "$jar_sha256" "$staging" | sha256sum --check --status
  chmod 0644 "$staging"
  mv -f -- "$staging" "$release"

  if [[ -e "$APP_DIR/current.jar" || -L "$APP_DIR/current.jar" ]]; then
    [[ -L "$APP_DIR/current.jar" ]] || { echo 'current.jar must be a release symlink.' >&2; return 1; }
    previous=$(readlink -f "$APP_DIR/current.jar")
    [[ -f "$previous" && "$previous" == "$APP_DIR/releases/"* && "$previous" != "$release" ]] || return 1
  fi

  trap finish EXIT
  trap 'exit 130' INT
  trap 'exit 143' TERM
  switched=true
  point_current "$release"
  systemctl restart "$SERVICE"
  if ! wait_ready; then
    echo 'Application/database readiness did not become UP.' >&2
    return 1
  fi
  switched=false
  echo "Deployed $release_id successfully."

  # Keep the current and previous release; the instance has a small root volume.
  local candidate
  for candidate in "$APP_DIR"/releases/*.jar; do
    if [[ "$candidate" != "$release" && "$candidate" != "$previous" ]]; then
      rm -f -- "$candidate"
    fi
  done
}

main() {
  [[ $# -eq 5 ]] || { echo 'Usage: deploy.sh BUCKET RELEASE_ID JAR_SHA256 REGION PORT' >&2; exit 2; }
  bucket=$1 release_id=$2 jar_sha256=$3 region=$4 port=$5
  [[ "$bucket" =~ ^[a-z0-9][a-z0-9.-]{1,61}[a-z0-9]$ ]]
  [[ "$release_id" =~ ^[0-9a-f]{40}-[0-9]+-[0-9]+$ ]]
  [[ "$jar_sha256" =~ ^[0-9a-f]{64}$ ]]
  [[ "$region" =~ ^[a-z]{2}-[a-z]+-[0-9]+$ ]]
  [[ "$port" =~ ^[0-9]{1,5}$ ]] && ((10#$port > 0 && 10#$port < 65536))
  [[ $EUID -eq 0 ]] || { echo 'Run through SSM as root.' >&2; exit 1; }

  for tool in aws java systemctl cloud-init curl sha256sum flock timeout runuser; do
    command -v "$tool" >/dev/null
  done
  timeout 120 cloud-init status --wait >/dev/null
  [[ -f "$APP_DIR/bootstrap-complete" ]]
  [[ -d "$APP_DIR/releases" ]]
  systemctl cat "$SERVICE" >/dev/null
  exec 9>"$APP_DIR/deploy.lock"
  flock -n 9 || { echo 'Another deployment is running.' >&2; exit 1; }

  for file in secrets.properties firebase-service-account.json rds-ca-bundle.pem; do
    runuser -u noticeboard -- test -s "$STATE_DIR/config/$file"
    runuser -u noticeboard -- test -r "$STATE_DIR/config/$file"
  done
  install -d -m 0750 -o noticeboard -g noticeboard "$STATE_DIR/files" "$STATE_DIR/images/category"
  deploy_release
}

if [[ "${BASH_SOURCE[0]}" == "$0" ]]; then
  main "$@"
fi
