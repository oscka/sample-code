package com.commerce.controller.product;

import static com.commerce.common.constant.ResponseConstants.*;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.commerce.controller.product.dto.ProductDto;
import com.commerce.service.ProductService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RequestMapping("/products")
@RequiredArgsConstructor
@RestController
public class ProductRestController {

	private final ProductService productService;

	@PostMapping
	private ResponseEntity<Void> save(@Valid @RequestBody ProductDto.Request saveRequest){
		productService.save(saveRequest);
		return CREATED;
	}

	@GetMapping
	private Page<ProductDto.Response> findAll(Pageable pageable, ProductDto.SearchCondition searchCondition){
		return productService.findAll(pageable,searchCondition);
	}

	@GetMapping("/{id}")
	private ProductDto.Response findById(@PathVariable Long id){

		return productService.findById(id);
	}

	@PutMapping("/{id}")
	private ResponseEntity<Void> update(@PathVariable Long id, @Valid @RequestBody ProductDto.Request update){
		productService.update(id,update);
		return OK;
	}

	@DeleteMapping("/{id}")
	private ResponseEntity<Void> delete(@PathVariable Long id){
		productService.delete(id);
		return OK;
	}

	@GetMapping("/best")
	private List<ProductDto.ProductRankingDto> getBestProductList(){
		return productService.getBestProductList();
	}


}