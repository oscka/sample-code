package com.sample.service;


import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sample.controller.category.dto.CategoryDto;
import com.sample.domain.category.Category;
import com.sample.domain.category.CategoryRepository;
import com.sample.domain.category.CategoryStatus;
import com.sample.error.exception.CategoryNotFoundException;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class CategoryService {

	private final CategoryRepository categoryRepository;

	@Transactional
	public void saveParentCategory(CategoryDto.SaveParentCategory requestDto){
		categoryRepository.save(requestDto.toEntity());
	}

	@Transactional
	public void saveChildCategory(CategoryDto.SaveChildCategory requestDto){

		// 상위 Category ID 가 존재하지 않을 경우 CategoryNotFoundException
		Category category = categoryRepository.findById(requestDto.getUpperCatCode()).orElseThrow(()
			-> new CategoryNotFoundException("존재하지 않는 상위카테고리 입니다."));

		// 하위 카테고리의 Level 이 상위카테고리의 Level 보다 같거나 작을경우 Exception
		if(requestDto.getCatLevel() <= category.getCatLevel()){
			throw new RuntimeException("상위카테고리 Level 설정에 문제가 있습니다.");
		}

		categoryRepository.save(requestDto.toEntity());
	}

	@Transactional(readOnly = true)
	public List<CategoryDto.CategoryInfo> findAllCategory(){

		return categoryRepository.findAllByStatus(CategoryStatus.ACTIVE)
			.stream()
			.map(Category::toCategoryDto)
			.collect(Collectors.toList());
	}

	@Transactional(readOnly = true)
	public List<CategoryDto.CategoryInfo> findChildCategory(Long upperCatCode) {

		Category category = categoryRepository.findById(upperCatCode).orElseThrow(()
			-> new CategoryNotFoundException("존재하지 않는 상위카테고리 입니다."));

		List<Category> secondCategoryList = categoryRepository.findAllByUpperCatCodeAndStatus(upperCatCode,CategoryStatus.ACTIVE);

		return secondCategoryList.stream()
			.map(Category::toCategoryDto)
			.collect(Collectors.toList());
	}



	@Transactional
	public void update(Long id ,CategoryDto.Save update){

		Category category = categoryRepository.findById(id).orElseThrow(()->new CategoryNotFoundException("존재하지 않은 카테고리 입니다."));

		if (Optional.ofNullable(update.getUpperCatCode()).orElse(0L) != 0 &&
			update.getCatLevel() <= update.getUpperCatCode()){

			// 하위 카테고리의 Level 이 상위카테고리의 Level 보다 같거나 작을경우 Exception
			throw new RuntimeException("상위카테고리 Level 설정에 문제가 있습니다.");
		}

		category.update(update);
	}

	@Transactional
	public void deleteCategory(Long id){

		Category category = categoryRepository.findById(id).orElseThrow(()->new CategoryNotFoundException("존재하지 않은 카테고리 입니다."));

		categoryRepository.delete(category);
	}

}