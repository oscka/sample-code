package com.commerce.controller.view;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/view")
@Controller
public class UserController {

	@GetMapping("/login")
	private String loginPage(){
		return "login";
	}


	@GetMapping("/loginSuccess")
	private String loginSuccessPage(){
		return "loginSuccess";
	}

}
