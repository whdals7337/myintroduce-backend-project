#!/bin/bash

for RETRY_COUNT in {1..10}
do
  RESPONSE=$(curl -s http://localhost:8081/profile)
  UP_COUNT=$(echo ${RESPONSE} | grep 'real' | wc -l)

  if [ ${UP_COUNT} -ge 1 ]
  then # $up_count >= 1 ("real" 문자열이 있는지 검증)
      echo "> 8081포트에 애플리케이션 배포 완료"
      echo "> 8082포트에 애플리케이션 배포 시작"
      chmod +x deploy8082.sh
      ./deploy8082.sh
      break
  else
      echo "> 8081포트에 애플리케이션의 응답을 알 수 없거나 혹은 실행 상태가 아닙니다."
  fi

  if [ ${RETRY_COUNT} -eq 10 ]
  then
    echo "> 8081포트에 애플리케이션 배포 실패. "
    exit 1
  fi

  echo "> 8081포트 애플리케이션 연결 실패. 재시도..."
  sleep 5
done