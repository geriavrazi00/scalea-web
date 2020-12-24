package com.scalea.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.scalea.configurations.Messages;
import com.scalea.entities.Area;
import com.scalea.entities.Process;
import com.scalea.enums.ProcessStatus;
import com.scalea.repositories.AreaRepository;
import com.scalea.repositories.ProcessRepository;
import com.scalea.utils.Constants;

@Controller
@RequestMapping("/processes")
public class ProcessController {

	private ProcessRepository processRepo;
	private AreaRepository areaRepo;
	private Logger log;
	private Messages messages;
	
	@Autowired
	public ProcessController(ProcessRepository processRepo, AreaRepository areaRepo, Messages messages) {
		this.processRepo = processRepo;
		this.areaRepo = areaRepo;
		this.log = LoggerFactory.getLogger(ProcessController.class);
		this.messages = messages;
	}
	
	@PreAuthorize("hasAnyAuthority('" + Constants.VIEW_PROCESSES_PRIVILEGE + "', '" + Constants.MANAGE_PROCESSES_PRIVILEGE + "')")
	@GetMapping
	public String allActiveProcesses(Model model) {
		log.info("Method allActiveProcesses()");
		Iterable<Process> activeProcesses = processRepo.findByStatus(ProcessStatus.STARTED.getStatus());
		Iterable<Area> areas = areaRepo.findByEnabled(true);
		
		// Maybe it would be better to get the started processes directly from the areas. Or maybe split the results in two sides
		
		model.addAttribute("processes", activeProcesses);
		model.addAttribute("areas", areas);
		return "private/processes/processmonitoring";
	}
}
