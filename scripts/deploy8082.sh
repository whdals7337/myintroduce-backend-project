#!/bin/bash

REPOSITORY=/home/ec2-user/app/step3
PROJECT_NAME=myintroduce-backend

echo "> 현재 8082포트에 구동중인 애플리케이션 pid 확인"

IDLE_PID=$(lsof -ti tcp:8082)

if [ -z ${IDLE_PID} ]
then
  echo "> 현재 8082포트에 구동중인 애플리케이션이 없으므로 종료하지 않습니다."
else
  echo "> kill -15 $IDLE_PID"
  kill -15 ${IDLE_PID}
  sleep 5
fi

echo "> 새 어플리케이션을 8082포트에 배포"

JAR_NAME=$(ls -tr $REPOSITORY/back-end/*.jar | tail -n 1)

echo "> $JAR_NAME 실행"

nohup java -javaagent:/home/ec2-user/scouter/agent.java/scouter.agent.jar \
    -Dscouter.config=/home/ec2-user/scouter/agent.java/conf/was02.conf \
    -jar \
    -Dspring.config.location=classpath:/application.yml,/home/ec2-user/app/application-real-db.yml,/home/ec2-user/app/application-ops.yml,/home/ec2-user/app/application-redis.yml,/home/ec2-user/app/application-s3.yml \
    -Dspring.profiles.active=real2 \
    $JAR_NAME > $REPOSITORY/nohup2.out 2>&1 &