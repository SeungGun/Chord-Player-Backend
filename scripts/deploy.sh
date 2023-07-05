#!/bin/bash

REPOSITORY=/home/ubuntu/app

echo "> 현재 구동중인 애플리케이션 PID 확인"

CURRENT_PID=$(pgrep -f chord-player)

echo "$CURRENT_PID"

if [ -z $CURRENT_PID ]; then
  echo "> 현재 구동중인 애플리케이션이 없으므로 종료하지 않습니다."
else
  echo "> kill -15 $CURRENT_PID"
  kill -15 $CURRENT_PID
  sleep 5
fi

echo "> 새 애플리케이션 배포"
echo "> Build 파일 복사"

cp $REPOSITORY/build/libs/*.jar $REPOSITORY/

JAR_NAME=$(ls $REPOSITORY/ | grep 'chord-player' | tail -n 1)

echo "> JAR Name: $JAR_NAME"

nohup java -jar $REPOSITORY/$JAR_NAME --spring.profiles.active=$profile --database.url=$database_url --database.username=$database_username --database.password=$database_password &
