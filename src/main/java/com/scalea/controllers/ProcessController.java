package com.scalea.controllers;

import java.security.Principal;
import java.util.Date;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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
import com.scalea.services.AreaService;
import com.scalea.services.ProcessService;
import com.scalea.services.ProductService;
import com.scalea.services.UserService;
import com.scalea.utils.Constants;
import com.scalea.utils.Utils;

@Controller
@RequestMapping("/processes")
public class ProcessController {

	private ProcessService processService;
	private AreaService areaService;
	private ProductService productService;
	private UserService userService;
	private Logger log;
	private Messages messages;
	
	private static final int DEFAULT_PAGE = 1;
	private static final int DEFAULT_SIZE = 7;
	
	@Autowired
	public ProcessController(ProcessService processService, AreaService areaService, ProductService productService, UserService userService,
			Messages messages) {
		this.processService = processService;
		this.areaService = areaService;
		this.productService = productService;
		this.userService = userService;
		this.log = LoggerFactory.getLogger(ProcessController.class);
		this.messages = messages;
	}
	
	@PreAuthorize("hasAnyAuthority('" + Constants.VIEW_PROCESSES_PRIVILEGE + "', '" + Constants.MANAGE_PROCESSES_PRIVILEGE + "')")
	@GetMapping
	public String allActiveProcesses(Model model, @RequestParam("page") Optional<Integer> page) {
		log.info("Method allActiveProcesses()");
		
		int currentPage = page.orElse(DEFAULT_PAGE);
		
		Iterable<Process> activeProcesses = processService.findByStatusIn(new int[] {ProcessStatus.STARTED.getStatus(), ProcessStatus.PAUSED.getStatus()});
		Page<Area> areas = areaService.findPaginatedByEnabledOrderByName(PageRequest.of(currentPage - 1, DEFAULT_SIZE));
		Iterable<Product> products = productService.findByEnabledIsTrueAndWithSubProductsIsFalse();
		
		for (Area area: areas) {
			for (Process process: activeProcesses) {
				if (process.getArea().equals(area)) {
					area.setActiveProcess(process);
				}
			}
		}
		
		model.addAttribute("areas", areas);
		model.addAttribute("products", products);
		model.addAttribute("pageNumbers", Utils.getPageNumbersList(areas.getTotalPages()));
		return "private/processes/processmonitoring";
	}
	
	@PreAuthorize("hasAnyAuthority('" + Constants.MANAGE_PROCESSES_PRIVILEGE + "')")
	@PostMapping("/start/{id}")
	public String startProcess(@PathVariable("id") Long id, @RequestParam("products") Long productId, Principal principal, 
			RedirectAttributes redirectAttributes, @RequestParam("page") Optional<Integer> page) throws GenericException {
		log.info("Method startProcess()");
		
		Optional<Area> area = areaService.findById(id);
		if (!area.isPresent()) throw new GenericException(messages.get("messages.area.not.found"));
		
		if (productId == null) throw new GenericException(messages.get("messages.product.not.found"));
		Optional<Product> product = productService.findById(productId);
		if (!product.isPresent()) throw new GenericException(messages.get("messages.product.not.found"));
		
		Process process = new Process();
		process.setStatus(ProcessStatus.STARTED.getStatus());
		process.setStartedAt(new Date());
		process.setProduct(product.get());
		process.setArea(area.get());
		process.setUser(userService.findByUsername(principal.getName()));
		process.setElapsedTime(0L);
		
		processService.save(process);
		
		redirectAttributes.addFlashAttribute("message", this.messages.get("messages.process.started", area.get().getName()));
	    redirectAttributes.addFlashAttribute("alertClass", "alert-success");
	    return "redirect:/processes" + paginationParameters(page);
	}
	
	@PreAuthorize("hasAnyAuthority('" + Constants.MANAGE_PROCESSES_PRIVILEGE + "')")
	@PostMapping("/pause/{id}")
	public String pauseProcess(@PathVariable("id") Long id, RedirectAttributes redirectAttributes, @RequestParam("page") Optional<Integer> page) throws GenericException {
		log.info("Method pauseProcess()");
		
		Optional<Process> optionalProcess = processService.findById(id);
		if (!optionalProcess.isPresent()) throw new GenericException(messages.get("messages.process.not.found"));
		Process process = optionalProcess.get();
		
		process.setStatus(ProcessStatus.PAUSED.getStatus());
		process.setStoppedAt(new Date());
		process.calculateElapsedTime();
		
		processService.save(process);
		
		redirectAttributes.addFlashAttribute("message", this.messages.get("messages.process.paused", process.getArea().getName()));
	    redirectAttributes.addFlashAttribute("alertClass", "alert-success");
	    return "redirect:/processes" + paginationParameters(page);
	}
	
	@PreAuthorize("hasAnyAuthority('" + Constants.MANAGE_PROCESSES_PRIVILEGE + "')")
	@PostMapping("/resume/{id}")
	public String resumeProcess(@PathVariable("id") Long id, RedirectAttributes redirectAttributes, @RequestParam("page") Optional<Integer> page) throws GenericException {
		log.info("Method resumeProcess()");
		
		Optional<Process> optionalProcess = processService.findById(id);
		if (!optionalProcess.isPresent()) throw new GenericException(messages.get("messages.process.not.found"));
		Process process = optionalProcess.get();
		
		process.setStatus(ProcessStatus.STARTED.getStatus());
		process.setStartedAt(new Date());
		process.setStoppedAt(null);
		
		processService.save(process);
		
		redirectAttributes.addFlashAttribute("message", this.messages.get("messages.process.resumed", process.getArea().getName()));
	    redirectAttributes.addFlashAttribute("alertClass", "alert-success");
	    return "redirect:/processes" + paginationParameters(page);
	}
	
	@PreAuthorize("hasAnyAuthority('" + Constants.MANAGE_PROCESSES_PRIVILEGE + "')")
	@PostMapping("/finish/{id}")
	public String finishProcess(@PathVariable("id") Long id, RedirectAttributes redirectAttributes, @RequestParam("page") Optional<Integer> page) throws GenericException {
		log.info("Method finishProcess()");
		
		Optional<Process> optionalProcess = processService.findById(id);
		if (!optionalProcess.isPresent()) throw new GenericException(messages.get("messages.process.not.found"));
		Process process = optionalProcess.get();
		
		process.setStatus(ProcessStatus.FINISHED.getStatus());
		process.setStoppedAt(new Date());
		process.calculateElapsedTime();
		
		processService.save(process);
		
		redirectAttributes.addFlashAttribute("message", this.messages.get("messages.process.finished", process.getArea().getName()));
	    redirectAttributes.addFlashAttribute("alertClass", "alert-success");
	    return "redirect:/processes" + paginationParameters(page);
	}
	
	private String paginationParameters(Optional<Integer> page) {
		return "?page=" + page.orElse(DEFAULT_PAGE);
	}
}
