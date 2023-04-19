package com.sample.service;

import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sample.controller.product.dto.ProductDto;
import com.sample.domain.product.Product;
import com.sample.domain.product.ProductRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class ProductService {

	private final ProductRepository productRepository;

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

}