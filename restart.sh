#!/usr/bin/env bash
# 重启 daily-discover-server（宿主机直跑模式）
# 用法: ./restart.sh [--build]

set -euo pipefail

PROJECT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
JAR="${PROJECT_DIR}/target/daily-discover-server-1.0.0-SNAPSHOT.jar"
LOG_DIR="/home/sshuser/logs/daily-discover"
OUT_LOG="${LOG_DIR}/server.out"

mkdir -p "${LOG_DIR}"

# 可选：重新打包
if [[ "${1:-}" == "--build" ]]; then
  echo "[$(date)] Building..."
  "${PROJECT_DIR}/mvnw" -q -o package -DskipTests
fi

echo "[$(date)] Stopping old process..."
pkill -f "daily-discover-server.*\.jar" 2>/dev/null || true
sleep 2

# 确保进程彻底停了
if pgrep -f "daily-discover-server.*\.jar" >/dev/null; then
  echo "[$(date)] Force killing..."
  pkill -9 -f "daily-discover-server.*\.jar" 2>/dev/null || true
  sleep 1
fi

echo "[$(date)] Starting..."
cd "${PROJECT_DIR}"
nohup java -jar "${JAR}" > "${OUT_LOG}" 2>&1 &
NEW_PID=$!
echo "[$(date)] Started PID: ${NEW_PID}"

# 等健康检查
for i in {1..30}; do
  if curl -sf http://localhost:8080/api/actuator/health >/dev/null; then
    echo "[$(date)] Health check OK"
    exit 0
  fi
  sleep 1
done

echo "[$(date)] Health check FAILED" >&2
tail -30 "${OUT_LOG}" >&2
exit 1