package com.commerce.controller;

import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import com.commerce.common.interceptor.LoginInterceptor;
import com.commerce.controller.category.CategoryRestController;
import com.commerce.service.CategoryService;
import com.commerce.service.login.SessionLoginService;
import com.fasterxml.jackson.databind.ObjectMapper;

@ExtendWith(RestDocumentationExtension.class)
@WebMvcTest(controllers = {
	CategoryRestController.class
})
@MockBean(JpaMetamodelMappingContext.class)
@EnableAutoConfiguration(exclude = LoginInterceptor.class)
@ActiveProfiles("test")
public abstract class ApiDocumentationTest {

	@MockBean
	protected CategoryService categoryService;

	@MockBean
	protected SessionLoginService sessionLoginService;

	@Autowired
	protected MockMvc mockMvc;

	@Autowired
	protected ObjectMapper mapper;
}
