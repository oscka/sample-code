package com.commerce.service;


import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.commerce.common.constant.CacheType;
import com.commerce.common.redis.RedisOperation;
import com.commerce.controller.category.dto.CategoryDto;
import com.commerce.domain.category.Category;
import com.commerce.domain.category.CategoryRepository;
import com.commerce.domain.category.CategoryStatus;
import com.commerce.error.exception.CategoryNotFoundException;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class CategoryService {

	private final CategoryRepository categoryRepository;


	private final RedisTemplate<Object,Object> redisTemplate;

	@Transactional
	public void saveParentCategory(CategoryDto.SaveParentCategory requestDto){
		categoryRepository.save(requestDto.toEntity());
		// 카테고리 캐싱
		setCategoryCaching();
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

		// 카테고리 캐싱
		setCategoryCaching();
	}

	@Transactional(readOnly = true)
	public List<CategoryDto.CategoryInfo> findAllCategory(){

		HashMap<String,Object> category = (HashMap<String, Object>) redisTemplate.opsForValue().get(CacheType.CATEGORY);

		if (Optional.ofNullable(category).orElse(category).isEmpty()){
			setCategoryCaching();
		}

		List<CategoryDto.CategoryInfo> categoryInfoList = ( List<CategoryDto.CategoryInfo> ) category.get("category");

		return categoryInfoList;
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

		// 카테고리 정보 update
		category.update(update);

		// 카테고리 캐싱
		setCategoryCaching();
	}

	@Transactional
	public void deleteCategory(Long id){
		Category category = categoryRepository.findById(id).orElseThrow(()->new CategoryNotFoundException("존재하지 않은 카테고리 입니다."));
		categoryRepository.delete(category);

		// 카테고리 캐싱
		setCategoryCaching();
	}

	/**
	 * 카테고리는 전체 list 를 불러온다는 조건하에, CUD 가 발생할경우 category key 로 저장된  value 전체를 삭제후 다시 캐싱.
	 */
	private void setCategoryCaching(){
		HashMap<String, Object> resultMap = new HashMap<>();
		List<CategoryDto.CategoryInfo> categoryInfoList = categoryRepository.findAllByStatus(CategoryStatus.ACTIVE)
			.stream()
			.map(Category::toCategoryDto)
			.collect(Collectors.toList());

		resultMap.put("category",categoryInfoList);
		redisTemplate.opsForValue().set(CacheType.CATEGORY,"");
		redisTemplate.opsForValue().set(CacheType.CATEGORY,resultMap);

	}

}