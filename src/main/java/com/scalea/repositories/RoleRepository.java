package com.scalea.repositories;

import java.util.Collection;
import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.scalea.entities.Role;
import com.scalea.entities.User;

public interface RoleRepository extends CrudRepository<Role, Long> {
	Role findByName(String name);
	List<Role> findByUsersIn(Collection<User> users);
	boolean existsByName(String name);
	Iterable<Role> findByNameNotInOrderByName(Collection<String> roleNames);
	Iterable<Role> findAllByOrderByName();
}
