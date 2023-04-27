package com.commerce.common.config;

import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import com.commerce.common.annotation.CurrentUser;
import com.commerce.service.login.SessionLoginService;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class CustomHandlerMethodArgumentResolver implements HandlerMethodArgumentResolver {

	private final SessionLoginService sessionLoginService;

	/**
	 * 현재 parameter 를 resolver 가 지원할지 true/false 로 반환
	 * @param parameter the method parameter to check
	 * @return
	 */
	@Override
	public boolean supportsParameter(MethodParameter parameter) {
		return parameter.hasParameterAnnotation(CurrentUser.class);
	}

	/**
	 * 실제 바인딩할 객체를 반환
	 * @param parameter the method parameter to resolve. This parameter must
	 * have previously been passed to {@link #supportsParameter} which must
	 * have returned {@code true}.
	 * @param mavContainer the ModelAndViewContainer for the current request
	 * @param webRequest the current request
	 * @param binderFactory a factory for creating {@link WebDataBinder} instances
	 * @return
	 * @throws Exception
	 */
	@Override
	public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
		NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
		return 	sessionLoginService.getUserSession();
	}
}
