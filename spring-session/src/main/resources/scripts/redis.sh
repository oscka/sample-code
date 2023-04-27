#!/bin/sh

# TTL 이 설정되어 있지 않은 세션 정보 SCAN

cursor=-1
host=${redis-host}
pattern=${redis_key_pattern}

while [ $cursor -ne 0 ]; do
  if [ $cursor -eq -1 ]
  then
    cursor=0
  fi

  reply=`redis-cli -h $host SCAN $cursor MATCH $pattern COUNT 10000`
  cursor=`expr "$reply" : '\([0-9]*[0-9]\)'`
  keys=`echo $reply | cut -d' ' -f2-`

  for key in ${keys// / } ; do
    ttl=`redis-cli -h $host TTL $key`

    # TTL 이 설정되지 않은 경우, TTL 명령어에 대한 응답값은 -1로 반환됩니다.
    if [ $ttl -eq -1 ]
    then
      echo "$key"
    fi
  done
done