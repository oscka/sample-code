package com.commerce.common.interceptor;

import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import com.commerce.common.annotation.LoginCheck;
import com.commerce.domain.user.common.UserLevel;
import com.commerce.service.login.SessionLoginService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class LoginInterceptor implements HandlerInterceptor {

	private final SessionLoginService sessionLoginService;

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {



		/**
		 * Interceptor 의 preHandle , postHandle 메서드를 보면, Object handler 인자가 있다.
		 * 이 인자는 RequestMapping 으로 매핑된 하나의 메서드로 Spring 에 의해 org.springframework.web.method.HandlerMethod 라는 클래로 Bind 되어 전달되는 인자다.
		 * getMethod 를 호출하면 실제 java Reflection 의 Method 형 객체를 얻을 수 있다.
		 *
		 * Custom Annotation 을 사용한다면, Interceptor 에서 위 handler 객체를 사용하여 프로젝트 내에 선언해둔 어노테이션을 가져올수 있다.
		 */
		HandlerMethod handlerMethod = (HandlerMethod) handler;
		LoginCheck loginCheck = handlerMethod.getMethodAnnotation(LoginCheck.class);

		if (loginCheck == null){
			return true;
		}

		if (sessionLoginService.getUserEmail() == null){
			throw new RuntimeException("로그인 후 이용가능합니다.");
		}

		UserLevel userLevel = loginCheck.authority();

		switch (userLevel){
			case SELLER:
				sellerCheck();
				break;
			case ADMIN:
				adminCheck();
				break;
		}

		return true;
	}

	private void adminCheck(){
		UserLevel level = sessionLoginService.getUserLevel();
		if (!UserLevel.ADMIN.equals(level)){
			throw new RuntimeException("접근할 수 없는 리소스입니다.");
		}
	}

	private void sellerCheck(){
		UserLevel level = sessionLoginService.getUserLevel();
		if (!UserLevel.SELLER.equals(level)){
			throw new RuntimeException("접근할 수 없는 리소스입니다.");
		}
	}

}
