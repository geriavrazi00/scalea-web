package com.scalea.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;

import com.scalea.entities.Role;
import com.scalea.entities.User;

public interface UserRepository extends CrudRepository<User, Long> {
	User findByUsername(String username);
	boolean existsByUsername(String username);
	boolean existsByUsernameAndIdNot(String username, Long id);
	Page<User> findByIdNotOrderByUsername(Long id, Pageable pageable);
	Page<User> findByIdNotAndRolesNotOrderByUsername(Long id, Role role, Pageable pageable);
}
