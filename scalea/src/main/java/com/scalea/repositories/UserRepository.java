package com.scalea.repositories;

import org.springframework.data.repository.CrudRepository;

import com.scalea.entities.User;

public interface UserRepository extends CrudRepository<User, Long> {
	User findByUsername(String username);
}
