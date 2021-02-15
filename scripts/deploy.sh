#!/bin/bash

REPOSITORY=/home/ec2-user/app/step3
PROJECT_NAME=myintroduce-backend

echo "> Build 파일 복사"

cp $REPOSITORY/back-end-zip/*.jar $REPOSITORY/back-end

echo "> 현재 구동중인 애플리케이션 pid 확인"

IDLE_PID=$(lsof -ti tcp:8081)

if [ -z ${IDLE_PID} ]
then
  echo "> 현재 구동중인 애플리케이션이 없으므로 종료하지 않습니다."
else
  echo "> kill -15 $IDLE_PID"
  kill -15 ${IDLE_PID}
  sleep 5
fi

echo "> 새 어플리케이션 배포"

JAR_NAME=$(ls -tr $REPOSITORY/back-end/*.jar | tail -n 1)

echo "> JAR Name: $JAR_NAME"

echo "> $JAR_NAME 에 실행권한 추가"

chmod +x $JAR_NAME

echo "> $JAR_NAME 실행"

java

nohup java -javaagent:/home/ec2-user/scouter/agent.java/scouter.agent.jar -Dobj_name=MY-INTRODUCE-WAS-8081 -DScouter.config=/home/ec2-user/scouter/agent.java/conf/myintroduce8081.conf -jar \
    -Dspring.config.location=classpath:/application.yml,/home/ec2-user/app/application-real-db.yml,/home/ec2-user/app/application-ops.yml \
    -Dspring.profiles.active=real1 \
    $JAR_NAME > $REPOSITORY/nohup.out 2>&1 &

sleep 10

for RETRY_COUNT in {1..10}
do
  RESPONSE=$(curl -s http://localhost:8081/profile)
  UP_COUNT=$(echo ${RESPONSE} | grep 'real' | wc -l)

  if [ ${UP_COUNT} -ge 1 ]
  then # $up_count >= 1 ("real" 문자열이 있는지 검증)
      echo "> Health check 성공"

      IDLE_PID2=$(lsof -ti tcp:8082)

      if [ -z ${IDLE_PID2} ]
      then
        echo "> 현재 구동중인 애플리케이션이 없으므로 종료하지 않습니다."
      else
        echo "> kill -15 $IDLE_PID2"
        kill -15 ${IDLE_PID2}
        sleep 5
      fi

      echo "> 새 어플리케이션 배포"

      JAR_NAME=$(ls -tr $REPOSITORY/back-end/*.jar | tail -n 1)

      echo "> JAR Name: $JAR_NAME"

      echo "> $JAR_NAME 에 실행권한 추가"

      chmod +x $JAR_NAME

      echo "> $JAR_NAME 실행"

      nohup java -javaagent:/home/ec2-user/scouter/agent.java/scouter.agent.jar -Dobj_name=MY-INTRODUCE-WAS-8082 -DScouter.config=/home/ec2-user/scouter/agent.java/conf/myintroduce8082.conf -jar \
          -Dspring.config.location=classpath:/application.yml,/home/ec2-user/app/application-real-db.yml,/home/ec2-user/app/application-ops.yml \
          -Dspring.profiles.active=real2 \
          $JAR_NAME > $REPOSITORY/nohup.out 2>&1 &
      break
  else
      echo "> Health check의 응답을 알 수 없거나 혹은 실행 상태가 아닙니다."
      echo "> Health check: ${RESPONSE}"
  fi

  if [ ${RETRY_COUNT} -eq 10 ]
  then
    echo "> Health check 실패. "
    exit 1
  fi

  echo "> Health check 연결 실패. 재시도..."
  sleep 10
done