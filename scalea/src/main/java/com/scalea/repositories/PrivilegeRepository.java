package com.scalea.repositories;

import org.springframework.data.repository.CrudRepository;

import com.scalea.entities.Privilege;

public interface PrivilegeRepository extends CrudRepository<Privilege, Long> {
	Privilege findByName(String name);
}
