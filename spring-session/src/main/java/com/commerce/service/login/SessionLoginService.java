package com.commerce.service.login;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.commerce.common.config.SessionConfig;
import com.commerce.controller.user.dto.UserDto;
import com.commerce.controller.user.dto.UserSession;
import com.commerce.domain.user.User;
import com.commerce.domain.user.UserRepository;
import com.commerce.domain.user.common.UserLevel;
import com.commerce.domain.user.common.UserStatus;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class SessionLoginService implements LoginService {

	private final UserRepository userRepository;
	private final HttpSession session;

	public final static String USER_SESSION = "users";


	public void login(UserDto.LoginRequest loginRequest){
		UserLevel userLevel = findByEmailAndPassword(loginRequest);

		UserSession userSession = UserSession.builder()
			.email(loginRequest.getEmail())
			.level(userLevel)
			.build();

		session.setAttribute(USER_SESSION,userSession);
		session.setMaxInactiveInterval(60 * 60); // session 만료시간

		// --------- 테스트
		String sessionId = session.getId();
		log.debug(sessionId);
		// --------- 테스트
	}

	public void logOut(UserSession userSession){
		session.removeAttribute(USER_SESSION);
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


	public UserSession getUserSession(){
		return (UserSession) session.getAttribute(USER_SESSION);
	}

	@Transactional(readOnly = true)
	public UserDto.UserInfoResponse currentUser(UserSession userSession){
		User user = userRepository.findByEmail(userSession.getEmail()).orElseThrow(()
				->new RuntimeException("존재하지않는 사용자입니다."));

		return user.toUserDto();
	}

}