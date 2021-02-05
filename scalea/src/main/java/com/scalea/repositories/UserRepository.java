package com.scalea.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;

import com.scalea.entities.Role;
import com.scalea.entities.User;

public interface UserRepository extends CrudRepository<User, Long> {
	User findByUsername(String username);
	boolean existsByUsernameAndIdNot(String username, Long id);
	Page<User> findByIdNot(Long id, Pageable pageable);
	Page<User> findByIdNotAndRolesNot(Long id, Role role, Pageable pageable);
}
