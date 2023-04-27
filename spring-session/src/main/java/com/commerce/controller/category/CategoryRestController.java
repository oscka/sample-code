package com.commerce.controller.category;

import static com.commerce.common.constant.ResponseConstants.*;

import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.commerce.controller.category.dto.CategoryDto;
import com.commerce.service.CategoryService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RequestMapping("/categories")
@RequiredArgsConstructor
@RestController
public class CategoryRestController {

	private final CategoryService categoryService;


	/**
	 * 상위 category 추가
	 */
	@PostMapping("/parent")
	private ResponseEntity<Void> saveParentCategory(@Valid @RequestBody CategoryDto.SaveParentCategory category){
		categoryService.saveParentCategory(category);
		return CREATED;
	}

	/**
	 * 하위(child) category 추가
	 */
	@PostMapping("/child")
	private ResponseEntity<Void> saveChildCategory(@Valid @RequestBody CategoryDto.SaveChildCategory category){
		categoryService.saveChildCategory(category);
		return CREATED;
	}

	/**
	 * Category 조회
	 * @param upperCatCode
	 * @return
	 */
	@GetMapping
	private ResponseEntity<?> select(@RequestParam(required = false) Long upperCatCode){

		if (Optional.ofNullable(upperCatCode).orElse(0L) != 0) {
			return ResponseEntity.ok(categoryService.findChildCategory(upperCatCode));
		}

		return ResponseEntity.ok(categoryService.findAllCategory());

	}

	/**
	 * 카테고리 수정
	 * @param id
	 * @param update
	 * @return
	 */
	@PutMapping("/{id}")
	private ResponseEntity<Void> update(@PathVariable Long id, @Valid @RequestBody CategoryDto.Save update){
		categoryService.update(id,update);
		return OK;
	}

	/**
	 * 카테고리 삭제
	 * @param id
	 * @return
	 */
	@DeleteMapping("/{id}")
	private ResponseEntity<Void> delete(@PathVariable Long id){
		categoryService.deleteCategory(id);
		return OK;
	}

}
