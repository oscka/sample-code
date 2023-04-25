package com.commerce.common.util;

import java.lang.reflect.Method;
import java.util.UUID;

import org.springframework.cache.interceptor.KeyGenerator;

public class CacheKeyGenerator implements KeyGenerator {

	private static final String PREFIX = "category:";

	@Override
	public Object generate(Object target, Method method, Object... params) {
		return PREFIX + UUID.randomUUID().toString();
	}
}
