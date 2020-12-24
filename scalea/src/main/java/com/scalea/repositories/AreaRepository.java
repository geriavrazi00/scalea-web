package com.scalea.repositories;

import org.springframework.data.repository.CrudRepository;

import com.scalea.entities.Area;

public interface AreaRepository extends CrudRepository<Area, Long> {
	boolean existsByName(String name);
	Iterable<Area> findByEnabled(boolean enabled);
}
