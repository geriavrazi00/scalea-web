package com.scalea.models.dto;

import java.util.Collection;

import com.scalea.entities.Role;
import com.scalea.entities.User;
import com.scalea.repositories.RoleRepository;

import lombok.Data;

@Data
public class UserDTO {
	
    private String username;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private Collection<Role> roles;
    
    public User toUser(User user, RoleRepository roleRepository) {
    	user.setUsername(username);
    	user.setFirstName(firstName);
    	user.setLastName(lastName);
    	user.setPhoneNumber(phoneNumber);
    	user.setRoles(roles);
    	
		return user;
	}

	
}
