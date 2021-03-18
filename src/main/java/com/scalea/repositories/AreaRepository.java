package com.scalea.repositories;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.scalea.entities.Area;
import com.scalea.entities.User;

public interface AreaRepository extends CrudRepository<Area, Long> {
	boolean existsByName(String name);
	Iterable<Area> findByEnabled(boolean enabled);
	Iterable<Area> findByEnabledOrderByName(boolean enabled);
	
	@Query("SELECT a FROM Area a WHERE enabled = ?1 ORDER BY LENGTH(a.name), a.name")
	Page<Area> findByEnabledOrderByName(boolean enabled, Pageable pageable);
	
	Optional<Area> findByIdAndEnabledIsTrue(Long id);
	Iterable<Area> findByEnabledIsTrueAndUserOrderByName(User user);
	boolean existsByEnabledIsTrueAndUser(User user);
	Optional<Area> findByEnabledIsTrueAndUser(User user);
}
