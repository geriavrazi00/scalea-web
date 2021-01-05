package com.scalea.repositories;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import com.scalea.entities.Area;
import com.scalea.entities.Process;

public interface ProcessRepository extends CrudRepository<Process, Long> {
	Iterable<Process> findByStatusIn(int[] statuses);
	Optional<Process> findByStatusAndArea(int status, Area area);
	Optional<Process> findFirstByAreaOrderByStartedAtDesc(Area area);
}