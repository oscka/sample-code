package com.sample.common.util;

import java.lang.reflect.Method;
import java.util.UUID;

import org.springframework.cache.interceptor.KeyGenerator;

public class CacheKeyGenerator implements KeyGenerator {

	private static final String PREFIX = "spring-cache:";

	@Override
	public Object generate(Object target, Method method, Object... params) {
		return PREFIX + UUID.randomUUID().toString();
	}
}
