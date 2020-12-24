package com.scalea.repositories;

import org.springframework.data.repository.CrudRepository;
import com.scalea.entities.Process;

public interface ProcessRepository extends CrudRepository<Process, Long> {
	Iterable<Process> findByStatus(int status);
}