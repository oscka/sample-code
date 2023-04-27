package com.commerce.domain.product;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.commerce.controller.product.dto.ProductDto;

public interface SearchProductRepository {

	Page<ProductDto.Response> searchProduct(Pageable pageable,ProductDto.SearchCondition condition);

}
