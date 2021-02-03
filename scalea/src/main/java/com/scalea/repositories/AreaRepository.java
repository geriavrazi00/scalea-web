package com.scalea.repositories;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;

import com.scalea.entities.Area;

public interface AreaRepository extends CrudRepository<Area, Long> {
	boolean existsByName(String name);
	Iterable<Area> findByEnabled(boolean enabled);
	Iterable<Area> findByEnabledOrderByName(boolean enabled);
	Page<Area> findByEnabledOrderByName(boolean enabled, Pageable pageable);
	Optional<Area> findByIdAndEnabledIsTrue(Long id);
}
