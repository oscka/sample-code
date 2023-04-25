package com.commerce.controller;

import static com.commerce.util.ApiDocumentUtils.*;
import static org.mockito.BDDMockito.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.setup.SharedHttpSessionConfigurer.*;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.commerce.controller.category.dto.CategoryDto;
import com.commerce.domain.category.CategoryStatus;

public class CategoryRestControllerTest extends ApiDocumentationTest {

	CategoryDto.SaveParentCategory parentCategory = CategoryDto.SaveParentCategory.builder()
		.catName("상의")
		.catLevel(1)
		.status(CategoryStatus.ACTIVE)
		.build();

	CategoryDto.SaveChildCategory childCategory = CategoryDto.SaveChildCategory.builder()
		.catName("가디건")
		.catLevel(2)
		.status(CategoryStatus.ACTIVE)
		.upperCatCode(1L)
		.build();

	private List<CategoryDto.CategoryInfo> getAllCategory(){
		List<CategoryDto.CategoryInfo> responseList = Arrays.asList(
			CategoryDto.CategoryInfo.builder()
				.id(1L)
				.catName(parentCategory.getCatName())
				.catLevel(parentCategory.getCatLevel())
				.status(parentCategory.getStatus())
				.upperCatCode(null)
				.build(),
			CategoryDto.CategoryInfo.builder()
				.id(2L)
				.catName(childCategory.getCatName())
				.catLevel(childCategory.getCatLevel())
				.status(childCategory.getStatus())
				.upperCatCode(childCategory.getUpperCatCode())
				.build()
		);

		return responseList;
	}

	@BeforeEach
	public void setup(WebApplicationContext webApplicationContext,RestDocumentationContextProvider restDocumentation) {
		this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
			.apply(documentationConfiguration(restDocumentation))
			.apply(sharedHttpSession())
			.build();
	}


	@DisplayName("상위 카테고리 추가")
	@Test
	void saveParentCategory() throws Exception {


		doNothing().when(categoryService).saveParentCategory(parentCategory);

		mockMvc.perform(post("/categories/parent")
			.contentType(MediaType.APPLICATION_JSON)
			.content(mapper.writeValueAsString(parentCategory)))
			.andDo(print())
			.andExpect(status().isCreated())
			.andDo(document("categories/parent/create",
				getDocumentRequest(),
				requestFields(
				fieldWithPath("catName").type(JsonFieldType.STRING).description("카테고리 이름"),
				fieldWithPath("catLevel").type(JsonFieldType.NUMBER).description("카테고리 LEVEL"),
				fieldWithPath("status").type(JsonFieldType.STRING).description("카테고리 상태")
			)));
	}

	@DisplayName("하위 카테고리 추가")
	@Test
	void saveChildCategoryTest() throws Exception {


		doNothing().when(categoryService).saveChildCategory(childCategory);

		mockMvc.perform(post("/categories/child")
				.contentType(MediaType.APPLICATION_JSON)
				.content(mapper.writeValueAsString(childCategory)))
			.andDo(print())
			.andExpect(status().isCreated())
			.andDo(document("categories/child/create",
				getDocumentRequest(),
				requestFields(
				fieldWithPath("catName").type(JsonFieldType.STRING).description("카테고리 이름"),
					fieldWithPath("status").type(JsonFieldType.STRING).description("카테고리 상태"),
				fieldWithPath("catLevel").type(JsonFieldType.NUMBER).description("카테고리 LEVEL"),
				fieldWithPath("upperCatCode").type(JsonFieldType.NUMBER).description("상위 카테고리 Code")
			)));
	}

	@DisplayName("상위카테고리를 지정하지 않을시 모든 카테고리를 return 한다.")
	@Test
	void selectAllCategories() throws Exception {

		given(categoryService.findAllCategory()).willReturn(getAllCategory());

		mockMvc.perform(get("/categories").accept(MediaType.APPLICATION_JSON))
			.andDo(print())
			.andExpect(status().isOk())
			.andDo(document("categories/find-all-category",
				getDocumentResponse(),
				responseFields(
					// response 가 array 일 경우 [].변수명 으로 입력한다.
					fieldWithPath("[].id").type(JsonFieldType.NUMBER).description("카테고리 id"),
					fieldWithPath("[].catName").type(JsonFieldType.STRING).description("카테고리 이름"),
					fieldWithPath("[].catLevel").type(JsonFieldType.NUMBER).description("카테고리 LEVEL"),
					fieldWithPath("[].status").type(JsonFieldType.STRING).description("카테고리 상태"),
					// null 을 허용하는 response 일경우 optional() 을 붙여준다.
					fieldWithPath("[].upperCatCode").type(JsonFieldType.NUMBER).description("상위 카테고리 Code").optional()
				)));
	}


	@DisplayName("상위카테고리를 지정할 경우 하위 카테고리만 return 한다.")
	@Test
	void selectChildCategories() throws Exception {

		List<CategoryDto.CategoryInfo> getChildCategory = Arrays.asList(
			CategoryDto.CategoryInfo.builder()
				.id(2L)
				.catName(childCategory.getCatName())
				.upperCatCode(childCategory.getUpperCatCode())
				.catLevel(childCategory.getCatLevel())
				.status(childCategory.getStatus())
				.build()
		);

		given(categoryService.findChildCategory(1L)).willReturn(getChildCategory);

		mockMvc.perform(get("/categories?upperCatCode=1").accept(MediaType.APPLICATION_JSON))
			.andDo(print())
			.andExpect(status().isOk())
			.andDo(document("categories/find-child-category",
				getDocumentResponse(),
				responseFields(
					fieldWithPath("[].id").type(JsonFieldType.NUMBER).description("카테고리 id"),
					fieldWithPath("[].catName").type(JsonFieldType.STRING).description("카테고리 이름"),
					fieldWithPath("[].catLevel").type(JsonFieldType.NUMBER).description("카테고리 LEVEL"),
					fieldWithPath("[].status").type(JsonFieldType.STRING).description("카테고리 상태"),
					fieldWithPath("[].upperCatCode").type(JsonFieldType.NUMBER).description("상위 카테고리 Code")
				)));
	}

	@DisplayName("카테고리를 수정한다.")
	@Test
	void updateCategoryTest() throws Exception {
		Long id = 2L;
		CategoryDto.Save update = CategoryDto.Save.builder()
			.catName("니트")
			.catLevel(2)
			.upperCatCode(1L)
			.status(CategoryStatus.ACTIVE)
			.build();

		doNothing().when(categoryService).update(id,update);



		this.mockMvc.perform(RestDocumentationRequestBuilders.put("/categories/{id}",id)
				.content(mapper.writeValueAsString(update))
				.contentType(MediaType.APPLICATION_JSON))
			.andDo(print())
			.andExpect(status().isOk())
			.andDo(document("categories/update"
				,getDocumentRequest()
				,pathParameters(parameterWithName("id").description("카테고리 ID"))
				,requestFields(
					fieldWithPath("catName").type(JsonFieldType.STRING).description("카테고리 이름"),
					fieldWithPath("status").type(JsonFieldType.STRING).description("카테고리 상태"),
					fieldWithPath("catLevel").type(JsonFieldType.NUMBER).description("카테고리 LEVEL"),
					fieldWithPath("upperCatCode").type(JsonFieldType.NUMBER).description("상위 카테고리 Code").optional()
				)
			));
	}


	@DisplayName("카테고리를 삭제한다.")
	@Test
	void deleteCategoryTest() throws Exception {
		Long id = 2L;

		doNothing().when(categoryService).deleteCategory(id);

		this.mockMvc.perform(RestDocumentationRequestBuilders.delete("/categories/{id}",id))
			.andDo(print())
			.andExpect(status().isOk())
			.andDo(document("categories/delete",pathParameters(
				parameterWithName("id").description("카테고리 ID"))
		));
	}

}
