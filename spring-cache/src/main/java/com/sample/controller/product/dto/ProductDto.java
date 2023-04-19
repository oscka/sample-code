package com.sample.controller.product.dto;

import com.sample.controller.category.dto.CategoryDto;
import com.sample.domain.product.Product;
import com.sample.domain.product.common.OrderStandard;

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

}
