package com.commerce.service;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.commerce.controller.user.dto.UserDto;
import com.commerce.domain.user.User;
import com.commerce.domain.user.UserRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class UserService {

	private final UserRepository userRepository;

	/**
	 * 회원가입
	 */
	@Transactional
	public void saveUser (UserDto.SaveRequest save) {

		if (isDuplicateEmail(save.getEmail())){
			throw new RuntimeException("중복된 이메일은 사용할 수 없습니다.");
		}

		userRepository.save(save.toEntity());
	}

	/**
	 * 이메일 중복 체크
	 */
	@Transactional(readOnly = true)
	public boolean isDuplicateEmail (String email) {
		return userRepository.existsUserByEmail(email);
	}


	/**
	 * (Admin) 회원 리스트
	 */
	@Transactional(readOnly = true)
	public Page<UserDto.UserInfoResponse> findAll(Pageable pageable){
		// page=3&size=10&sort=id,DESC

		List<UserDto.UserInfoResponse> userInfoResponseList = userRepository.findAll(pageable)
			.stream()
			.map(User::toUserDto)
			.collect(Collectors.toList());

		return new PageImpl<>(userInfoResponseList);
	}

	/**
	 * (Admin) 회원 정보 찾기
	 */
	@Transactional(readOnly = true)
	public UserDto.UserInfoResponse findById(Long id){
		User user = userRepository.findById(id).orElseThrow(()-> new RuntimeException("존재하지않는 사용자입니다."));
		return user.toUserDto();
	}


	/**
	 * 회원 정보 수정
	 */
	@Transactional
	public void update (Long id, UserDto.UpdateByAdminRequest update) {
		User user = userRepository.findById(id).orElseThrow(()-> new RuntimeException("존재하지않는 사용자입니다."));
		user.updateByAdmin(update);
	}

	/**
	 * 회원 탈퇴 (By Admin)
	 */
	@Transactional
	public void delete (Long id) {
		User user = userRepository.findById(id).orElseThrow(()-> new RuntimeException("존재하지않는 사용자입니다."));
		userRepository.delete(user);
	}

	/**
	 * 회원 탈퇴 (By Email ,User)
	 */
	@Transactional
	public void deleteByEmail (String email) {
		User user = userRepository.findByEmail(email).orElseThrow(()-> new RuntimeException("존재하지않는 사용자입니다."));
		//userRepository.delete(user);
		user.markDeleted();
	}



}