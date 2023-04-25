package com.sample.service;



import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Component;

import com.sample.common.constant.CacheType;
import com.sample.common.redis.RedisZSetOperation;
import com.sample.controller.product.dto.ProductDto;

import lombok.extern.slf4j.Slf4j;

/**
 * best product ranking operation
 */
@Slf4j
@Component
public class BestProductRedisOperation implements RedisZSetOperation<ProductDto.ProductRankingDto> {

	@Override
	public void execute(ZSetOperations<String, Object> operations, List<ProductDto.ProductRankingDto> productRankingDtoList) {

		Long count = this.zCard(operations);

		// count 가 null 이거나 0 일경우 range 를 삭제한다.
		if (Optional.ofNullable(count).orElse(count) == 0){
			this.removeRange(operations, count);
		}

		for (ProductDto.ProductRankingDto productRankingDto : productRankingDtoList){
			this.add(operations,productRankingDto);
		}
	}

	/**
	 * ZCARD key
	 * key 의 element 의 개수 return
	 * @param operations
	 * @return
	 */
	@Override
	public Long zCard(ZSetOperations<String, Object> operations) {
		return operations.zCard(CacheType.BEST10PRODUCT.getName());
	}

	/**
	 * ZREMRANGEBYRANK key start stop
	 * key 사이의 순위에 저장된 정렬된 집합의 모든 요소 제거
	 * @param operations
	 * @param count
	 */
	@Override
	public void removeRange(ZSetOperations<String, Object> operations, Long count) {
		operations.removeRange(CacheType.BEST10PRODUCT.getName(), 0, count);
	}

	/**
	 * ZADD KEY SCORE VALUE
	 * @param operations
	 * @param productRankingDto
	 * @return
	 */
	@Override
	public Boolean add(ZSetOperations<String, Object> operations, ProductDto.ProductRankingDto productRankingDto) {
		return operations.add(CacheType.BEST10PRODUCT.getName(), productRankingDto,Double.valueOf(productRankingDto.getPurchaseCount()));
	}

	@Override
	public Set<Object> reverseRange(ZSetOperations<String, Object> operations, int start, int stop) {
		return operations.reverseRange(CacheType.BEST10PRODUCT.getName(),start,stop);
	}
}
