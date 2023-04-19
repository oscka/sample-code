package com.sample.common.constant;


import lombok.Getter;

@Getter
public enum CacheType {


	CATEGORY("category",24 * 60 * 60,500), // 1day
	PRODUCT("product"),
	LIKE_COUNT("like_count");

	private final String name;
	private final int expireAfterWrite;

	private final int maximumSize;

	CacheType(String name){
		this.name = name;
		this.expireAfterWrite = ConstConfig.DEFAULT_TTL_SEC;
		this.maximumSize = ConstConfig.DEFAULT_MAX_SIZE;
	}

	CacheType(String name,int expireAfterWrite){
		this.name = name;
		this.expireAfterWrite = expireAfterWrite;
		this.maximumSize = ConstConfig.DEFAULT_MAX_SIZE;
	}

	CacheType(String name,int expireAfterWrite,int maximumSize){
		this.name = name;
		this.expireAfterWrite = expireAfterWrite;
		this.maximumSize = maximumSize;
	}

	public static class ConstConfig {
		static final int DEFAULT_TTL_SEC = 3000; //50 * 60 (50분)
		static final int DEFAULT_MAX_SIZE = 10000;

	}


}