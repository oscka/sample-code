package com.sample.controller.category.dto;

import com.sample.domain.category.Category;
import com.sample.domain.category.CategoryStatus;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class CategoryDto {

	@Getter
	@Builder
	@NoArgsConstructor(access = AccessLevel.PROTECTED)
	@AllArgsConstructor
	public static class Save {
		@NotBlank(message = "카테고리명을 입력해주세요.")
		@Size(max = 50, message = "올바른 카테고리명 값을 입력했는지 확인해주세요.")
		private String catName;

		@NotNull(message = "카테고리 상태를 입력해주세요")
		private CategoryStatus status;

		@NotNull(message = "카테고리 레벨 값을 입력해주세요")
		private Integer catLevel;

		private Long upperCatCode;

		public Category toEntity(){
			return Category.builder()
				.catName(this.catName)
				.catLevel(this.catLevel)
				.status(this.status)
				.upperCatCode(this.upperCatCode)
				.build();
		}
	}

	@Getter
	@Builder
	@NoArgsConstructor(access = AccessLevel.PROTECTED)
	@AllArgsConstructor
	public static class SaveParentCategory {
		@NotBlank(message = "카테고리명을 입력해주세요.")
		@Size(max = 50, message = "올바른 카테고리명 값을 입력했는지 확인해주세요.")
		private String catName;

		@NotNull(message = "카테고리 상태를 입력해주세요")
		private CategoryStatus status;

		@NotNull(message = "카테고리 레벨 값을 입력해주세요")
		private Integer catLevel;

		public Category toEntity(){
			return Category.builder()
				.catName(this.catName)
				.catLevel(this.catLevel)
				.status(this.status)
				.build();
		}
	}

	@Getter
	@Builder
	@NoArgsConstructor(access = AccessLevel.PROTECTED)
	@AllArgsConstructor
	public static class SaveChildCategory {
		@NotBlank(message = "카테고리명을 입력해주세요.")
		@Size(max = 50, message = "올바른 카테고리명 값을 입력했는지 확인해주세요.")
		private String catName;

		@NotNull(message = "카테고리 상태를 입력해주세요")
		private CategoryStatus status;

		@NotNull(message = "카테고리 레벨 값을 입력해주세요")
		private Integer catLevel;

		@NotNull(message = "상위 카테고리 값을 입력해주세요")
		private Long upperCatCode;

		public Category toEntity(){
			return Category.builder()
				.catName(this.catName)
				.catLevel(this.catLevel)
				.upperCatCode(this.upperCatCode)
				.status(this.status)
				.build();
		}
	}

	@Getter
	@NoArgsConstructor(access = AccessLevel.PROTECTED)
	public static class CategoryInfo {

		private Long id;
		private String catName;

		private CategoryStatus status;

		private Integer catLevel;

		private Long upperCatCode;

		@Builder
		public CategoryInfo (Long id,String catName,CategoryStatus status,Integer catLevel,Long upperCatCode){
			this.id = id;
			this.catName = catName;
			this.status = status;
			this.catLevel = catLevel;
			this.upperCatCode = upperCatCode;
		}

		public Category toEntity(){
			return Category.builder()
				.id(this.id)
				.catName(this.catName)
				.catLevel(this.catLevel)
				.upperCatCode(this.upperCatCode)
				.status(this.status)
				.build();
		}

	}


}
