#!/bin/bash

REPOSITORY=/home/ec2-user/app/step3
PROJECT_NAME=myintroduce-backend

echo "> Build 파일 복사"

cp $REPOSITORY/back-end-zip/*.jar $REPOSITORY/back-end

JAR_NAME=$(ls -tr $REPOSITORY/back-end/*.jar | tail -n 1)

echo "> $JAR_NAME 에 실행권한 추가"

chmod +x $JAR_NAME

cd $REPOSITORY/back-end-zip/

echo "> deploy8081.sh 에 실행권한 추가"

chmod +x deploy8081.sh

echo "> 8081포트에 애플리케이션 배포 시작"

./deploy8081.sh

sleep 20

echo "> health.sh 에 실행권한 추가"

chmod +x health.sh

echo "> 8081포트에 애플리케이션 배포 상태 확인"

./health.sh