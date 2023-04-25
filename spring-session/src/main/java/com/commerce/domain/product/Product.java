package com.commerce.domain.product;

import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicInsert;

import com.commerce.controller.product.dto.ProductDto;
import com.commerce.domain.BaseTimeEntity;
import com.commerce.domain.category.Category;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedEntityGraph;
import jakarta.persistence.PostRemove;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NamedEntityGraph()
@DynamicInsert
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Table(name = "products")
@Entity
public class Product extends BaseTimeEntity {

	@Column(name = "products_id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Id
	private Long id;

	@Column(nullable = false)
	private String name;

	@Column(nullable = false)
	private Long price;

	@Column(nullable = false)
	private Integer stockQuantity;

	@Builder.Default
	@ColumnDefault("0")
	private Integer viewCount = 0;

	@ColumnDefault("0")
	private Integer likeCount;

	@ManyToOne(optional = false)
	@JoinColumn(name = "category_id")
	private Category category;

	// ------------ 비지니스 로직 --------------- //



	public ProductDto.Response toProductDto(){

		return ProductDto.Response.builder()
			.id(this.id)
			.name(this.name)
			.price(this.price)
			.category(this.category.toCategoryDto())
			.stockQuantity(this.stockQuantity)
			.build();
	}

	public void update(ProductDto.Request update){
		this.price = update.getPrice();
		this.name = update.getName();
		this.category = update.getCategoryInfo().toEntity();
		this.stockQuantity = update.getStockQuantity();
	}


}
