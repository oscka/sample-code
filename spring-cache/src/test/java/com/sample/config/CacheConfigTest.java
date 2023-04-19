package com.sample.config;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.sample.Application;
import com.sample.domain.category.Category;
import com.sample.domain.category.CategoryRepository;

@ExtendWith(SpringExtension.class)
//@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@SpringBootTest(classes = Application.class)
public class CacheConfigTest {

	@Autowired
	CacheManager cacheManager;

	@Autowired
	CategoryRepository categoryRepository;


}
