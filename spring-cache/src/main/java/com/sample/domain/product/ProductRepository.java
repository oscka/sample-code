package com.sample.domain.product;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sample.controller.product.dto.ProductCount;
import com.sample.domain.product.common.LikeCountInterface;
import com.sample.domain.product.common.ViewCountInterface;

@Repository
public interface ProductRepository extends JpaRepository<Product,Long> ,SearchProductRepository{

	ProductCount findViewCountAndLikeCountById(Long id);

	ViewCountInterface findViewCountById(Long id);

	LikeCountInterface findLikeCountById(Long id);
}
