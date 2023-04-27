package com.sample.domain.product;

import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicInsert;

import com.sample.controller.product.dto.ProductDto;
import com.sample.domain.BaseTimeEntity;
import com.sample.domain.category.Category;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedEntityGraph;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


@NamedEntityGraph()
@DynamicInsert
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "products")
@Entity
public class Product extends BaseTimeEntity {

	@Column(name = "products_id")
	@GeneratedValue
	@Id
	private Long id;

	private String name;
	private Long price;
	private Integer stockQuantity;

	@ColumnDefault("0")
	private Integer viewCount;

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
