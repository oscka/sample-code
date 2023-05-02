package com.commerce.common.config;

import java.util.ArrayList;
import java.util.List;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.commerce.common.interceptor.LoginInterceptor;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Configuration
public class WebConfig implements WebMvcConfigurer {

	private final LoginInterceptor loginInterceptor;
	private final CustomHandlerMethodArgumentResolver customHandlerMethodArgumentResolver;

	//addArgumentResolvers
	@Override
	public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
		resolvers.add(customHandlerMethodArgumentResolver);
	}

	/**
	 * addInterceptor vs addWebRequestInterceptor
	 * @param registry
	 */
	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		List<String> notLoadList = new ArrayList<>();
		notLoadList.add("/view/**");
		notLoadList.add("/resources/**");

		registry.addInterceptor(loginInterceptor)
			.addPathPatterns("/**")
			.excludePathPatterns(notLoadList);

	}
}
