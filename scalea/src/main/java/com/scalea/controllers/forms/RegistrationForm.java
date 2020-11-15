package com.scalea.controllers.forms;

import org.springframework.security.crypto.password.PasswordEncoder;

import com.scalea.entities.User;

import lombok.Data;

@Data
public class RegistrationForm {
	
	private String username;
	private String password;
	private String firstname;
	private String lastname;
	private String phonenumber;
	private String identification;

	public User toUser(PasswordEncoder passwordEncoder) {
		return new User(username, passwordEncoder.encode(password), firstname, lastname, phonenumber, identification);
	}
}
