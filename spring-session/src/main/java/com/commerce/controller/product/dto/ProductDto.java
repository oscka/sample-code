package com.commerce.controller.product.dto;


import com.commerce.controller.category.dto.CategoryDto;
import com.commerce.domain.product.Product;
import com.commerce.domain.product.common.OrderStandard;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class ProductDto {

	@Getter
	@NoArgsConstructor(access = AccessLevel.PROTECTED)
	@AllArgsConstructor
	public static class Request {

		private String name;
		private Long price;
		private Integer stockQuantity;

		private CategoryDto.CategoryInfo categoryInfo;

		public Product toEntity(){
			return Product.builder()
				.name(this.name)
				.price(this.price)
				.stockQuantity(this.stockQuantity)
				.category(categoryInfo.toEntity())
				.build();
		}

	}

	@Getter
	@AllArgsConstructor
	@Builder
	@NoArgsConstructor(access = AccessLevel.PROTECTED)
	public static class Response {

		private Long id;
		private String name;
		private Long price;
		private Integer stockQuantity;
		private CategoryDto.CategoryInfo category;
		private Integer purchaseCount;

	}

	@Getter
	@NoArgsConstructor
	public static class update {
		private Long id;

		private String name;
		private Long price;
		private Integer stockQuantity;

		private CategoryDto.CategoryInfo category;

	}


	@Builder
	@Getter
	@NoArgsConstructor
	@AllArgsConstructor
	public static class SearchCondition {

		private String keyword;
		private Long categoryId;

		private OrderStandard orderStandard;
	}

	@Builder
	@Getter
	@NoArgsConstructor
	@AllArgsConstructor
	public static class ProductRankingDto {

		private Long id;
		private String name;
		private Long price;
		private Integer stockQuantity;
		private Integer purchaseCount;
	}

}
