package com.commerce.controller.user.dto;


import com.commerce.domain.user.User;
import com.commerce.domain.user.common.Gender;
import com.commerce.domain.user.common.UserLevel;
import com.commerce.domain.user.common.UserStatus;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class UserDto {

	@Builder
	@AllArgsConstructor
	@NoArgsConstructor(access = AccessLevel.PRIVATE)
	@Getter
	public static class SaveRequest {

		@NotBlank
		private String name;

		@Email
		@NotBlank
		private String email;

		@NotBlank
		private String password;

		@NotBlank
		private String phone;

		@NotNull
		private UserLevel userLevel;

		@NotNull
		private Gender gender;

		public User toEntity(){
			return User.builder()
				.name(this.name)
				.email(this.email)
				.password(this.password)
				.phone(this.phone)
				.status(UserStatus.ACTIVE)
				.gender(this.gender)
				.userLevel(this.userLevel)
				.build();
		}
	}

	@Builder
	@AllArgsConstructor
	@NoArgsConstructor(access = AccessLevel.PRIVATE)
	@Getter
	public static class UserInfoResponse {
		private Long id;
		private String name;
		private String email;
		private String password;
		private String phone;
		private UserStatus status;
		private UserLevel userLevel;
		private Gender gender;
	}

	@AllArgsConstructor
	@Builder
	@NoArgsConstructor(access = AccessLevel.PACKAGE)
	@Getter
	public static class LoginRequest{

		private String email;
		private String password;

	}

	@Builder
	@AllArgsConstructor
	@NoArgsConstructor(access = AccessLevel.PRIVATE)
	@Getter
	public static class UpdateByAdminRequest {

		@NotBlank
		private String name;

		@Email
		@NotBlank
		private String email;

		@NotBlank
		private String password;

		@NotBlank
		private String phone;

		@NotNull
		private UserLevel userLevel;

		@NotNull
		private UserStatus status;

		@NotNull
		private Gender gender;

		public User toEntity(){
			return User.builder()
				.status(this.status)
				.name(this.name)
				.email(this.email)
				.password(this.password)
				.phone(this.phone)
				.status(UserStatus.ACTIVE)
				.gender(this.gender)
				.userLevel(this.userLevel)
				.build();
		}
	}
}