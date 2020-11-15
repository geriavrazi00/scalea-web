package com.scalea.controllers.forms;

import java.util.Arrays;

import org.springframework.security.crypto.password.PasswordEncoder;

import com.scalea.entities.User;
import com.scalea.enums.InitialRoles;
import com.scalea.repositories.RoleRepository;

import lombok.Data;

@Data
public class RegistrationForm {
	
	private String username;
	private String password;
	private String firstname;
	private String lastname;
	private String phonenumber;
	private String identification;

	public User toUser(PasswordEncoder passwordEncoder, RoleRepository roleRepository) {
		return new User(username, passwordEncoder.encode(password), firstname, lastname, phonenumber, identification, 
			Arrays.asList(roleRepository.findByName(InitialRoles.ROLE_USER.getName())));
	}
}
