package com.commerce.domain.category;

import java.util.List;

import com.commerce.controller.category.dto.CategoryDto;
import com.commerce.controller.category.dto.CategoryDto.CategoryInfo;
import com.commerce.domain.BaseTimeEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Entity
public class Category extends BaseTimeEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "category_id")
	private Long id;

	@Enumerated(EnumType.STRING)
	@Column(length = 30, nullable = false)
	private CategoryStatus status;

	private String catName;
	private Long upperCatCode;
	private Integer catLevel;

	// ------------ 비지니스 로직 --------------- //
	public CategoryInfo toCategoryDto(){
		return CategoryInfo.builder()
			.id(this.id)
			.status(this.status)
			.catName(this.catName)
			.upperCatCode(this.upperCatCode)
			.catLevel(this.catLevel)
			.build();
	}


	public void update(CategoryDto.Save update){
		this.catLevel = update.getCatLevel();
		this.catName = update.getCatName();
		this.status = update.getStatus();
		this.upperCatCode = update.getUpperCatCode();
	}

}
