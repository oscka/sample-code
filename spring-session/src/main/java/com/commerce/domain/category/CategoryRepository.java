package com.commerce.domain.category;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

	List<Category> findAllByUpperCatCodeAndStatus(Long upperCatCode, CategoryStatus status);

	List<Category> findAllByStatus(CategoryStatus status);

	Category findByCatLevelAndStatus(Integer level, CategoryStatus status);

}
