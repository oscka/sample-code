package com.commerce.common.redis;

import java.util.List;
import java.util.Set;

import org.springframework.data.redis.core.ZSetOperations;

public interface RedisZSetOperation<T> {

	Long zCard(ZSetOperations<Object,Object> operations);

	void removeRange(ZSetOperations<Object,Object> operations,Long count);

	Boolean add(ZSetOperations<Object,Object> operations,T t);

	void execute(ZSetOperations<Object,Object> operations, List<T> t);

	Set<Object> reverseRange(ZSetOperations<Object,Object> operations,int start,int stop);

}
