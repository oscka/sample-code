package com.commerce.domain.product;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.commerce.controller.product.dto.ProductCount;
import com.commerce.domain.product.common.LikeCountInterface;
import com.commerce.domain.product.common.ViewCountInterface;

@Repository
public interface ProductRepository extends JpaRepository<Product,Long> ,SearchProductRepository{

	ProductCount findViewCountAndLikeCountById(Long id);

	ViewCountInterface findViewCountById(Long id);

	LikeCountInterface findLikeCountById(Long id);
}
