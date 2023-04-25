package com.commerce.service.login;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.commerce.controller.user.dto.UserDto;
import com.commerce.domain.user.User;
import com.commerce.domain.user.UserRepository;
import com.commerce.domain.user.common.UserLevel;
import com.commerce.domain.user.common.UserStatus;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class SessionLoginService implements LoginService {

	private final UserRepository userRepository;
	private final HttpSession session;

	public final static String USER_ID = "email";

	public final static String USER_LEVEL = "auth";

	public void login(UserDto.LoginRequest loginRequest){
		UserLevel userLevel = findByEmailAndPassword(loginRequest);
		session.setAttribute(USER_ID,loginRequest.getEmail());
		session.setAttribute(USER_LEVEL,userLevel);
	}

	public void logOut(String email){
		session.removeAttribute(email);
		session.removeAttribute(USER_ID);
		session.removeAttribute(USER_LEVEL);
	}

	@Transactional(readOnly = true)
	private UserLevel findByEmailAndPassword(UserDto.LoginRequest loginRequest){

		User user = userRepository.findByEmailAndPassword(loginRequest.getEmail(), loginRequest.getPassword()).orElseThrow(
			()-> new RuntimeException("존재하지않는 사용자 입니다."));

		// 로그인 가능한 사용지인지 체크
		isActiveUser(user.getStatus());

		return user.getUserLevel();
	}

	public void isActiveUser(UserStatus status){
		if (!status.equals(UserStatus.ACTIVE)){
			throw new RuntimeException("로그인할 수 없는 사용자입니다.");
		}
	}

	public String getUserEmail(){
		return (String) session.getAttribute(USER_ID);
	}

	public UserLevel getUserLevel(){
		return (UserLevel) session.getAttribute(USER_LEVEL);
	}

	@Transactional(readOnly = true)
	public UserDto.UserInfoResponse currentUser(String email){
		User user = userRepository.findByEmail(email).orElseThrow(()->new RuntimeException("존재하지않는 사용자입니다."));
		return user.toUserDto();
	}

}