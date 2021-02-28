package com.scalea.repositories;

import java.util.Date;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;

import com.scalea.entities.Area;
import com.scalea.entities.Process;
import com.scalea.entities.Product;

public interface ProcessRepository extends CrudRepository<Process, Long> {
	Iterable<Process> findByStatusIn(int[] statuses);
	Optional<Process> findByStatusAndArea(int status, Area area);
	Optional<Process> findFirstByAreaOrderByStartedAtDesc(Area area);
	
	Page<Process> findByAreaOrderByCreatedAtDesc(Area area, Pageable pageable);
	Page<Process> findByAreaAndStartedAtGreaterThanEqualAndStartedAtLessThanOrderByCreatedAtDesc(Area area, Date startedAt, Date endDate, Pageable pageable);
	Page<Process> findByAreaAndProductOrderByCreatedAtDesc(Area area, Product product, Pageable pageable);
	Page<Process> findByAreaAndStartedAtGreaterThanEqualAndStartedAtLessThanAndProductOrderByCreatedAtDesc(Area area, Date startedAt, Date endDate, Product product, Pageable pageable);
}