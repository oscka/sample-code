package com.commerce.service.login;

import com.commerce.controller.user.dto.UserDto;
import com.commerce.domain.user.common.UserStatus;

public interface LoginService {

	void login(UserDto.LoginRequest loginRequest);

	void logOut(String email);

	void isActiveUser(UserStatus status);

	UserDto.UserInfoResponse currentUser(String email);

}
