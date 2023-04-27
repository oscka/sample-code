#!/bin/sh

# 레디스 클러스터에 TTL 이 설정되지 않은채 저장되어 있는 값들을 삭제

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

  count=0

 for key in ${keys// / } ; do
    ttl=`redis-cli -h $host TTL $key`
    act=""

    if [ $ttl -eq -1 ]
    then
      result=`redis-cli -h $host EXPIRE $key $expire`
      act=" -> $expire"
      ((count++))
    fi

    echo "$key: $ttl$act"
  done
  if [ $count -gt 0 ]
  then
    echo 'sleep'
    sleep 1s
  fi
done