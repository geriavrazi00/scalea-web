package com.scalea.controllers.forms;

import java.util.Arrays;

import org.springframework.security.crypto.password.PasswordEncoder;

import com.scalea.entities.User;
import com.scalea.enums.ApplicationRoles;
import com.scalea.repositories.RoleRepository;

import lombok.Data;

@Data
public class RegistrationForm {
	
	private String username;
	private String password;
	private String firstname;
	private String lastname;
	private String phonenumber;

	public User toUser(PasswordEncoder passwordEncoder, RoleRepository roleRepository) {
		return new User(username, passwordEncoder.encode(password), firstname, lastname, phonenumber, 
			Arrays.asList(roleRepository.findByName(ApplicationRoles.ROLE_USER.getName())));
	}
}
