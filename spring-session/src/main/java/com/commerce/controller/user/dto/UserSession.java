package com.commerce.controller.user.dto;

import java.io.Serializable;

import com.commerce.domain.user.common.UserLevel;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class UserSession implements Serializable {

	private String email;
	private UserLevel level;
}
