package com.sample.common.constant;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum CacheType {


	CATEGORY("category");

	private final String name;
	private final int expireAfterWrite;

	private final int maximumSize;

	CacheType(String name){
		this.name = name;
		this.expireAfterWrite = ConstConfig.DEFAULT_TTL_SEC;
		this.maximumSize = ConstConfig.DEFAULT_MAX_SIZE;
	}

	public static class ConstConfig {
		public static final String KEY_CATEGORY = "category";
		static final int DEFAULT_TTL_SEC = 3000;
		static final int DEFAULT_MAX_SIZE = 10000;

	}


}