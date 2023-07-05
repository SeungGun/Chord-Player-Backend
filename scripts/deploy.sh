#!/usr/bin/env bash

REPOSITORY=/home/ubuntu/app
cd $REPOSITORY

JAR_NAME= $(ls $REPOSITORY/build/libs/ | grep 'SNAPSHOT.jar' | tail -n 1)
echo "> $JAR_NAME"

JAR_PATH=$REPOSITORY/build/libs/$JAR_NAME
echo "> $JAR_PATH"

APP_LOG=$REPOSITORY/application.log

echo "> 현재 구동 중인 애플리케이션 PID 확인"

CURRENT_PID=$(pgrep -fl java | grep $REPOSITORY/build/libs/ | awk '{print $1}')

if [ -z "$CURRENT_PID" ] 
then
  echo "현재 구동 중인 애플리케이션이 없으므로 종료하지 않습니다."
else
  echo "> kill -15 $CURRENT_PID"
  kill -15 $CURRENT_PID
  sleep 5
fi

echo "> $JAR_PATH 실행"

nohup java -jar $JAR_PATH > $APP_LOG 2>&1 &

echo "> 애플리케이션 배포 완료"
