package com.commerce.controller.user;

import static com.commerce.common.constant.ResponseConstants.*;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.commerce.common.annotation.CurrentUser;
import com.commerce.common.annotation.LoginCheck;
import com.commerce.controller.user.dto.UserDto;
import com.commerce.controller.user.dto.UserSession;
import com.commerce.domain.user.common.UserLevel;
import com.commerce.service.login.SessionLoginService;
import com.commerce.service.UserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;

@RequestMapping("/users")
@RequiredArgsConstructor
@RestController
public class UserRestController {

	private final UserService userService;

	private final SessionLoginService sessionLoginService;

	/**
	 * (Admin)
	 * @param id
	 * @return
	 */
	@LoginCheck(authority = UserLevel.ADMIN)
	@GetMapping("/{id}")
	private ResponseEntity<UserDto.UserInfoResponse> findById(@PathVariable Long id){
		return ResponseEntity.ok().body(userService.findById(id));
	}

	/**
	 * (Admin)
	 * @param pageable
	 * @return
	 */
	@LoginCheck(authority = UserLevel.ADMIN)
	@GetMapping
	private ResponseEntity<Page<UserDto.UserInfoResponse>> findAll(Pageable pageable){
		return ResponseEntity.ok().body(userService.findAll(pageable));
	}

	/**
	 * (Admin)
	 * @param id
	 * @param update
	 * @return
	 */
	@LoginCheck(authority = UserLevel.ADMIN)
	@PutMapping("/{id}")
	private ResponseEntity<Void> updateByAdmin (@PathVariable Long id, @Valid @RequestBody UserDto.UpdateByAdminRequest update){
		userService.update(id,update);
		return OK;
	}

	/**
	 * (Admin)
	 * @param id
	 * @return
	 */
	@LoginCheck(authority = UserLevel.ADMIN)
	@DeleteMapping("/{id}")
	private ResponseEntity<Void> deleteByAdmin(@PathVariable Long id){
		userService.delete(id);
		return OK;
	}

	@PostMapping
	private ResponseEntity<Void> save (@Valid @RequestBody UserDto.SaveRequest save){
		userService.saveUser(save);
		return CREATED;
	}

	@PostMapping("/login")
	private void logIn(@Valid @RequestBody UserDto.LoginRequest loginRequest){
		sessionLoginService.login(loginRequest);
	}

	@LoginCheck
	@GetMapping("/my")
	//private ResponseEntity<UserDto.UserInfoResponse> findMyInfo(@CurrentUser String email){
	private ResponseEntity<UserDto.UserInfoResponse> findMyInfo(@CurrentUser UserSession userSession){
		return ResponseEntity.ok().body(sessionLoginService.currentUser(userSession));
	}

	@LoginCheck
	@GetMapping("/logout")
	private ResponseEntity<Void> logOut(@CurrentUser UserSession userSession){
		sessionLoginService.logOut(userSession);
		return OK;
	}


	/**
	 * 회원 탈퇴
	 * @param userSession
	 * @return
	 */
	@LoginCheck
	@DeleteMapping
	private ResponseEntity<Void> deleteByEmail(@CurrentUser UserSession userSession){
		sessionLoginService.logOut(userSession);
		userService.deleteByEmail(userSession.getEmail());
		return OK;
	}
}