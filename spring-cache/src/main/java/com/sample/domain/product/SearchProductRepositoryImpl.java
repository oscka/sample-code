package com.sample.domain.product;

import static com.sample.domain.product.QProduct.*;
import static org.springframework.util.StringUtils.*;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import com.sample.controller.product.dto.ProductDto;
import com.sample.domain.product.common.OrderStandard;
import com.querydsl.core.QueryResults;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class SearchProductRepositoryImpl implements SearchProductRepository{

	private final JPAQueryFactory jpaQueryFactory;

	@Override
	public Page<ProductDto.Response> searchProduct(Pageable pageable,ProductDto.SearchCondition condition) {

		QueryResults<ProductDto.Response> results = jpaQueryFactory
			.select(Projections.fields(ProductDto.Response.class,
				product.id,
				product.category,
				product.createdAt,
				product.updatedAt,
				product.price,
				product.name,
				product.stockQuantity
				))
			.from(product)
			.leftJoin(product.category)
			.where(
				eqCategoryId(condition.getCategoryId()),
				containsKeyword(condition.getKeyword())
				).orderBy(
					getOrderSpecifier(condition.getOrderStandard())
				)
				.offset(pageable.getOffset())
				.limit(pageable.getPageSize())
				.fetchResults();


		List<ProductDto.Response> products = results.getResults();
		long total = results.getTotal();

		return new PageImpl<>(products,pageable,total);
	}

	private BooleanExpression eqCategoryId(Long categoryId) {
		return (categoryId != null) ? product.category.id.eq(categoryId) : null;
	}

	private BooleanExpression containsKeyword(String keyword) {
		return (hasText(keyword)) ? product.name.containsIgnoreCase(keyword) : null;
			//.or(product.price.containsIgnoreCase(keyword))
	}

	private OrderSpecifier getOrderSpecifier(OrderStandard orderStandard) {
		OrderSpecifier orderSpecifier = null;

		if (orderStandard == null) {
			orderSpecifier = new OrderSpecifier(Order.DESC, product.createdAt);
		} else {
			switch (orderStandard) {
				case LOW_PRICE:
					orderSpecifier = new OrderSpecifier(Order.ASC, product.price);
					break;
				case HIGH_PRICE:
					orderSpecifier = new OrderSpecifier(Order.DESC, product.price);
					break;
				case NEW_PRODUCT:
					orderSpecifier = new OrderSpecifier(Order.DESC, product.updatedAt);
					break;
				default:
					orderSpecifier = new OrderSpecifier(Order.DESC, product.createdAt);
			}
		}

		return orderSpecifier;
	}




}
