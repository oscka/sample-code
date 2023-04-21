package com.sample.common.redis;

import java.util.List;
import java.util.Set;

import org.springframework.data.redis.core.ZSetOperations;

public interface RedisZSetOperation<T> {

	Long zCard(ZSetOperations<String,Object> operations);

	void removeRange(ZSetOperations<String,Object> operations,Long count);

	Boolean add(ZSetOperations<String,Object> operations,T t);

	void execute(ZSetOperations<String,Object> operations, List<T> t);

	Set<Object> reverseRange(ZSetOperations<String,Object> operations,int start,int stop);

}
