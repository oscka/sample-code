package com.commerce.service.login;

import com.commerce.controller.user.dto.UserDto;
import com.commerce.controller.user.dto.UserSession;
import com.commerce.domain.user.common.UserStatus;

public interface LoginService {

	void login(UserDto.LoginRequest loginRequest);

	void logOut(UserSession userSession);

	void isActiveUser(UserStatus status);

	UserDto.UserInfoResponse currentUser(UserSession userSession);

}
