package com.commerce.service.product;


import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.commerce.controller.product.dto.ProductDto;
import com.commerce.domain.product.Product;
import com.commerce.domain.product.ProductRepository;

import lombok.RequiredArgsConstructor;

@CacheConfig(cacheManager = "redisCacheManager")
@RequiredArgsConstructor
@Service
public class ProductService {

	private final ProductRepository productRepository;

	private final BestProductRedisOperation bestProductRedisOperation;

	private final ZSetOperations<Object,Object> zSetOperation;

	@Transactional
	public void save(ProductDto.Request save){
		productRepository.save(save.toEntity());
	}

	@Transactional(readOnly = true)
	public Page<ProductDto.Response> findAll(Pageable pageable, ProductDto.SearchCondition searchCondition){

		return productRepository.searchProduct(pageable,searchCondition);
	}

	@Transactional(readOnly = true)
	@Cacheable(value = "product", key="#id")
	public ProductDto.Response findById(Long id){

		return productRepository.findById(id).orElseThrow(()
			-> new RuntimeException("존재하지 않는 상품입니다.")).toProductDto();
	}


	@Transactional
	@CacheEvict(value = "product", key = "#id")
	public void update(Long id, ProductDto.Request update){
		Product product = productRepository.findById(id).orElseThrow(()->new RuntimeException("존재하지 않는 상품입니다."));
		product.update(update);
	}

	@Transactional
	@CacheEvict(value = "product",key="#id")
	public void delete(Long id){
		Product product = productRepository.findById(id).orElseThrow(()->new RuntimeException("존재하지 않는 상품입니다."));
		productRepository.delete(product);
	}

	/**
	 * 상위 인기 상품 조회
	 * @return
	 */
	public List<ProductDto.ProductRankingDto> getBestProductList(){
		Set<Object> object = bestProductRedisOperation.reverseRange(zSetOperation,0,9);
		return object.stream().map(el -> (ProductDto.ProductRankingDto)el).collect(Collectors.toList());
	}

	@Transactional(readOnly = true)
	@Cacheable(value = "product-cnt" , key = "#id")
	public Integer getProductViewCount(Long id){
		return productRepository.findViewCountById(id).getViewCount();
	}

	@Transactional(readOnly = true)
	@Cacheable(value = "product:count:like", key = "#id", unless = "#reslut == null")
	public Integer getProductLikeCount(Long id){
		return productRepository.findById(id).get().getLikeCount();
	}

}