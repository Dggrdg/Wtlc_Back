package com.wtlc.controller;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.wtlc.bean.User;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "http://localhost:5173")
public class AuthController {

	@PostMapping("/login")
	public User Login() {

		User user = new User();

		user.setUserName("Mason");
		user.setPassword("A1234567");

		return user;
	}

}
