package com.scalea.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.scalea.entities.Role;
import com.scalea.entities.User;

public interface UserRepository extends CrudRepository<User, Long> {
	User findByUsername(String username);
	boolean existsByUsername(String username);
	boolean existsByUsernameAndIdNot(String username, Long id);
	Page<User> findByIdNotOrderByUsername(Long id, Pageable pageable);
	Page<User> findByIdNotAndRoleNotOrderByUsername(Long id, Role role, Pageable pageable);
	
	@Query("SELECT DISTINCT u FROM User u JOIN u.role.privileges AS p WHERE u.role.name != ?1 AND p.name = ?2 ORDER BY u.username")
	Iterable<User> findNotAdminUsersByPrivilege(String roleName, String privilegeName);
}
