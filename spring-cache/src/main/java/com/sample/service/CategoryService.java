package com.sample.service;

import static com.sample.common.constant.CacheType.ConstConfig.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
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

	//@CacheEvict(value = KEY_CATEGORY,allEntries = true)
	//@CachePut(value = KEY_CATEGORY,keyGenerator = "cacheKeyGenerator")
	//@Cacheable(value = KEY_CATEGORY,keyGenerator = "cacheKeyGenerator")

	@CacheEvict(value = KEY_CATEGORY,allEntries = true)
	@Transactional
	public void saveParentCategory(CategoryDto.SaveParentCategory requestDto){
		categoryRepository.save(requestDto.toEntity());
	}

	//@CacheEvict(value = KEY_CATEGORY,allEntries = true)
	//@CachePut(value = KEY_CATEGORY,keyGenerator = "cacheKeyGenerator")
	//@Cacheable(value = KEY_CATEGORY,keyGenerator = "cacheKeyGenerator")

	@CacheEvict(value = KEY_CATEGORY,allEntries = true)
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

	// @CacheEvict(value = KEY_CATEGORY, allEntries = true)
	// @Transactional
	// public void save(CategoryDto.Save save){
	//
	// 	// 카테고리의 upperCatCode 가 존재하며, 해당 상위 Category ID 가 존재하지 않을 경우 CategoryNotFoundException
	// 	if (Optional.ofNullable(save.getUpperCatCode()).orElse(0L) != 0){
	// 		Category category = categoryRepository.findById(save.getUpperCatCode()).orElseThrow(()
	// 			-> new CategoryNotFoundException("존재하지 않는 상위카테고리 입니다."));
	// 	}
	//
	// 	if (Optional.ofNullable(save.getUpperCatCode()).orElse(0L) < 0 && save.getCatLevel() != 1){
	// 		throw new RuntimeException("하위 카테고리 등록시 상위카테고리Code 를 입력해주세요");
	// 	}
	//
	// 	categoryRepository.save(save.toEntity());
	// }


	//@Cacheable(value = KEY_CATEGORY ,keyGenerator = "cacheKeyGenerator" , unless="#result == null")
	@Cacheable(value = KEY_CATEGORY , unless="#result == null")
	@Transactional(readOnly = true)
	public List<CategoryDto.CategoryInfo> findAllCategory(){

		return categoryRepository.findAllByStatus(CategoryStatus.ACTIVE)
			.stream()
			.map(Category::toCategoryDto)
			.collect(Collectors.toList());
	}

	//@Cacheable(cacheNames = KEY_CATEGORY,keyGenerator = "cacheKeyGenerator", unless="#result == null")
	@Cacheable(cacheNames = KEY_CATEGORY,unless="#result == null")
	@Transactional(readOnly = true)
	public List<CategoryDto.CategoryInfo> findChildCategory(Long upperCatCode) {

		Category category = categoryRepository.findById(upperCatCode).orElseThrow(()
			-> new CategoryNotFoundException("존재하지 않는 상위카테고리 입니다."));

		List<Category> secondCategoryList = categoryRepository.findAllByUpperCatCodeAndStatus(upperCatCode,CategoryStatus.ACTIVE);

		return secondCategoryList.stream()
			.map(Category::toCategoryDto)
			.collect(Collectors.toList());
	}



	//@CacheEvict(cacheNames = KEY_CATEGORY,keyGenerator = "cacheKeyGenerator")
	@CacheEvict(value = KEY_CATEGORY,allEntries = true)
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
	//@CacheEvict(cacheNames = KEY_CATEGORY,keyGenerator = "cacheKeyGenerator")
	@CacheEvict(value = KEY_CATEGORY,allEntries = true)
	public void deleteCategory(Long id){

		Category category = categoryRepository.findById(id).orElseThrow(()->new CategoryNotFoundException("존재하지 않은 카테고리 입니다."));

		categoryRepository.delete(category);
	}

}