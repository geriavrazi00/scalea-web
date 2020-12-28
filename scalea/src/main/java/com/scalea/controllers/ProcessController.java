package com.scalea.controllers;

import java.security.Principal;
import java.util.Date;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.scalea.configurations.Messages;
import com.scalea.entities.Area;
import com.scalea.entities.Process;
import com.scalea.entities.Product;
import com.scalea.enums.ProcessStatus;
import com.scalea.exceptions.GenericException;
import com.scalea.repositories.AreaRepository;
import com.scalea.repositories.ProcessRepository;
import com.scalea.repositories.ProductRepository;
import com.scalea.repositories.UserRepository;
import com.scalea.utils.Constants;

@Controller
@RequestMapping("/processes")
public class ProcessController {

	private ProcessRepository processRepo;
	private AreaRepository areaRepo;
	private ProductRepository productRepo;
	private UserRepository userRepo;
	private Logger log;
	private Messages messages;
	
	@Autowired
	public ProcessController(ProcessRepository processRepo, AreaRepository areaRepo, ProductRepository productRepo, UserRepository userRepo,
			Messages messages) {
		this.processRepo = processRepo;
		this.areaRepo = areaRepo;
		this.productRepo = productRepo;
		this.userRepo = userRepo;
		this.log = LoggerFactory.getLogger(ProcessController.class);
		this.messages = messages;
	}
	
	@PreAuthorize("hasAnyAuthority('" + Constants.VIEW_PROCESSES_PRIVILEGE + "', '" + Constants.MANAGE_PROCESSES_PRIVILEGE + "')")
	@GetMapping
	public String allActiveProcesses(Model model) {
		log.info("Method allActiveProcesses()");
		Iterable<Process> activeProcesses = processRepo.findByStatusIn(new int[] {ProcessStatus.STARTED.getStatus(), ProcessStatus.PAUSED.getStatus()});
		Iterable<Area> areas = areaRepo.findByEnabled(true);
		Iterable<Product> products = productRepo.findByEnabledIsTrueAndWithSubProductsIsFalse();
		
		for (Area area: areas) {
			for (Process process: activeProcesses) {
				if (process.getArea().equals(area)) {
					area.setActiveProcess(process);
				}
			}
		}
		
		model.addAttribute("areas", areas);
		model.addAttribute("products", products);
		return "private/processes/processmonitoring";
	}
	
	@PreAuthorize("hasAnyAuthority('" + Constants.MANAGE_PROCESSES_PRIVILEGE + "')")
	@PostMapping("/start/{id}")
	public String startProcess(@PathVariable("id") Long id, @RequestParam("products") Long productId, Principal principal, 
			RedirectAttributes redirectAttributes) throws GenericException {
		log.info("Method startProcess()");
		
		Optional<Area> area = areaRepo.findById(id);
		if (!area.isPresent()) throw new GenericException(messages.get("messages.area.not.found"));
		
		if (productId == null) throw new GenericException(messages.get("messages.product.not.found"));
		Optional<Product> product = productRepo.findById(productId);
		if (!product.isPresent()) throw new GenericException(messages.get("messages.product.not.found"));
		
		Process process = new Process();
		process.setStatus(ProcessStatus.STARTED.getStatus());
		process.setStartedAt(new Date());
		process.setProduct(product.get());
		process.setArea(area.get());
		process.setUser(userRepo.findByUsername(principal.getName()));
		process.setElapsedTime(0L);
		
		processRepo.save(process);
		
		redirectAttributes.addFlashAttribute("message", this.messages.get("messages.process.started", area.get().getName()));
	    redirectAttributes.addFlashAttribute("alertClass", "alert-success");
	    return "redirect:/processes";
	}
	
	@PreAuthorize("hasAnyAuthority('" + Constants.MANAGE_PROCESSES_PRIVILEGE + "')")
	@PostMapping("/pause/{id}")
	public String pauseProcess(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) throws GenericException {
		log.info("Method pauseProcess()");
		
		Optional<Process> optionalProcess = processRepo.findById(id);
		if (!optionalProcess.isPresent()) throw new GenericException(messages.get("messages.process.not.found"));
		Process process = optionalProcess.get();
		
		process.setStatus(ProcessStatus.PAUSED.getStatus());
		process.setStoppedAt(new Date());
		process.calculateElapsedTime();
		
		processRepo.save(process);
		
		redirectAttributes.addFlashAttribute("message", this.messages.get("messages.process.paused", process.getArea().getName()));
	    redirectAttributes.addFlashAttribute("alertClass", "alert-success");
	    return "redirect:/processes";
	}
	
	@PreAuthorize("hasAnyAuthority('" + Constants.MANAGE_PROCESSES_PRIVILEGE + "')")
	@PostMapping("/resume/{id}")
	public String resumeProcess(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) throws GenericException {
		log.info("Method resumeProcess()");
		
		Optional<Process> optionalProcess = processRepo.findById(id);
		if (!optionalProcess.isPresent()) throw new GenericException(messages.get("messages.process.not.found"));
		Process process = optionalProcess.get();
		
		process.setStatus(ProcessStatus.STARTED.getStatus());
		process.setStartedAt(new Date());
		process.setStoppedAt(null);
		
		processRepo.save(process);
		
		redirectAttributes.addFlashAttribute("message", this.messages.get("messages.process.resumed", process.getArea().getName()));
	    redirectAttributes.addFlashAttribute("alertClass", "alert-success");
	    return "redirect:/processes";
	}
	
	@PreAuthorize("hasAnyAuthority('" + Constants.MANAGE_PROCESSES_PRIVILEGE + "')")
	@PostMapping("/finish/{id}")
	public String finishProcess(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) throws GenericException {
		log.info("Method finishProcess()");
		
		Optional<Process> optionalProcess = processRepo.findById(id);
		if (!optionalProcess.isPresent()) throw new GenericException(messages.get("messages.process.not.found"));
		Process process = optionalProcess.get();
		
		process.setStatus(ProcessStatus.FINISHED.getStatus());
		process.setStoppedAt(new Date());
		process.calculateElapsedTime();
		
		processRepo.save(process);
		
		redirectAttributes.addFlashAttribute("message", this.messages.get("messages.process.finished", process.getArea().getName()));
	    redirectAttributes.addFlashAttribute("alertClass", "alert-success");
	    return "redirect:/processes";
	}
}
