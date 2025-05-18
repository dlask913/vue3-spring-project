# 현재 날짜 형식: YYYY-MM-DD
TODAY=$(date +%F)

# 로그 백업 디렉토리 생성
mkdir -p /backup_logs/spring-app-1
mkdir -p /backup_logs/spring-app-2

# spring-app-1 로그 백업
if [ -f logs/spring-app-1/nohup.log ]; then
  grep -E 'INFO|WARN|ERROR' /logs/spring-app-1/nohup.log > /backup_logs/spring-app-1/nohup-$TODAY.log
fi

# spring-app-2 로그 백업
if [ -f logs/spring-app-2/nohup.log ]; then
  grep -E 'INFO|WARN|ERROR' /logs/spring-app-2/nohup.log > /backup_logs/spring-app-2/nohup-$TODAY.log
fi