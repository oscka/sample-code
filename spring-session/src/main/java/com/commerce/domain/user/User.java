package com.commerce.domain.user;

import java.time.LocalDateTime;

import com.commerce.controller.user.dto.UserDto;
import com.commerce.domain.BaseTimeEntity;
import com.commerce.domain.user.common.Gender;
import com.commerce.domain.user.common.UserLevel;
import com.commerce.domain.user.common.UserStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PostRemove;
import jakarta.persistence.PreRemove;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

//@EqualsAndHashCode(of = "id")
@Builder
@AllArgsConstructor
@Getter
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Table(name = "users")
@Entity
public class User extends BaseTimeEntity {


	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "users_id")
	private Long id;



	@Column(nullable = false)
	private String name;

	@Column(nullable = false, updatable = false)
	private String email;

	@Column(nullable = false)
	private String password;

	private String phone;

	@Enumerated(EnumType.STRING)
	private UserStatus status;
	@Enumerated(EnumType.STRING)
	private UserLevel userLevel;
	@Enumerated(EnumType.STRING)
	private Gender gender;
	private LocalDateTime deletedAt;


	// ------------ 비지니스 로직 --------------- //

	public UserDto.UserInfoResponse toUserDto(){
		return UserDto.UserInfoResponse.builder()
			.id(this.id)
			.name(this.name)
			.email(this.email)
			.password(this.password)
			.phone(this.phone)
			.status(this.status)
			.gender(this.gender)
			.userLevel(this.userLevel)
			.build();
	}

	public void updateByAdmin(UserDto.UpdateByAdminRequest user){
		this.name = user.getName();
		this.status = user.getStatus();
		this.phone = user.getPhone();
		this.gender = user.getGender();
		this.userLevel = user.getUserLevel();
	}

	//@PostRemove
	//@PreRemove
	public void markDeleted(){
		// softDelete : 회원 탈퇴시 실제 회원정보를 삭제하는것이 아닌, 회원 상태를 DELETE 로 처리해둔다.
		this.status = UserStatus.DELETED;
		this.deletedAt = LocalDateTime.now();
	}

}
