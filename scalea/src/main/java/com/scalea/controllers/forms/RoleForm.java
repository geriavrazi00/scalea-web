package com.scalea.controllers.forms;

import com.scalea.entities.Role;
import com.scalea.repositories.RoleRepository;

import lombok.Data;

@Data
public class RoleForm {
	private String name;
	
	public Role toRole(RoleRepository roleRepository) {
		return new Role(name);
	}
}
