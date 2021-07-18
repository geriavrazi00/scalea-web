package com.scalea.repositories;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;

import com.scalea.entities.Area;
import com.scalea.entities.Group;

public interface GroupRepository extends CrudRepository<Group, Long> {
	Page<Group> findByAreaOrderByName(Area area, Pageable pageable);
	Optional<Group> findByAreaAndDefaultGroupIsTrue(Area area);
	Page<Group> findAByAreaEnabledIsTrueOrderByAreaNameAscNameAsc(Pageable pageable);
}
