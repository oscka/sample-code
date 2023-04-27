package com.commerce.common.constant;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum CacheType {


	CATEGORY("category"),
	BEST10PRODUCT("product::best"),

	NEW_PRODUCT("product::new"),
	;


	private final String name;
	private final int expireAfterWrite;

	private final int maximumSize;

	CacheType(String name){
		this.name = name;
		this.expireAfterWrite = ConstConfig.DEFAULT_TTL_SEC;
		this.maximumSize = ConstConfig.DEFAULT_MAX_SIZE;
	}

	public static class ConstConfig {
		static final int DEFAULT_TTL_SEC = 3000; // 300
		static final int DEFAULT_MAX_SIZE = 10000;

	}


}