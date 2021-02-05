package com.scalea.services;

import java.util.Date;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.scalea.entities.Area;
import com.scalea.entities.Process;
import com.scalea.entities.Product;
import com.scalea.repositories.ProcessRepository;
import com.scalea.utils.Utils;

@Service
@Transactional
public class ProcessService {
	private ProcessRepository processRepo;

	@Autowired
	public ProcessService(ProcessRepository processRepo) {
		this.processRepo = processRepo;
	}
	
	public Optional<Process> findById(Long id) {
		return processRepo.findById(id);
	}
	
	public Iterable<Process> findByStatusIn(int[] statuses) {
		return processRepo.findByStatusIn(statuses);
	}
	
	public Optional<Process> findByStatusAndArea(int status, Area area) {
		return processRepo.findByStatusAndArea(status, area);
	}
	
	public Optional<Process> findFirstByAreaOrderByStartedAtDesc(Area area) {
		return processRepo.findFirstByAreaOrderByStartedAtDesc(area);
	}
	
	public Page<Process> findFilteredProcesses(Area area, Date startedAt, Optional<Product> product, Pageable pageable) {
		Page<Process> processes = null;
		
		if (startedAt != null && product.isPresent()) {
			Date nextDay = Utils.addOrRemoveDaysToDate(startedAt, 1);
			processes = processRepo.findByAreaAndStartedAtGreaterThanEqualAndStartedAtLessThanAndProductOrderByCreatedAtDesc(area, startedAt, nextDay, product.get(), pageable);
		} else if (startedAt != null && product.isEmpty()) {
			Date nextDay = Utils.addOrRemoveDaysToDate(startedAt, 1);
			processes = processRepo.findByAreaAndStartedAtGreaterThanEqualAndStartedAtLessThanOrderByCreatedAtDesc(area, startedAt, nextDay, pageable);
		} else if (startedAt == null && product.isPresent()) {
			processes = processRepo.findByAreaAndProductOrderByCreatedAtDesc(area, product.get(), pageable);
		} else {
			processes = processRepo.findByAreaOrderByCreatedAtDesc(area, pageable);
		}
		
		return processes;
	}
	
	public Process save(Process process) {
		return processRepo.save(process);
	}
}
