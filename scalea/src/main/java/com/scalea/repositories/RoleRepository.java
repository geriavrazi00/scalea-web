package com.scalea.repositories;

import org.springframework.data.repository.CrudRepository;

import com.scalea.entities.Role;

public interface RoleRepository extends CrudRepository<Role, Long> {
	Role findByName(String name);
}
