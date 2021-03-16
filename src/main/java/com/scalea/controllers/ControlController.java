package com.scalea.controllers;

import java.security.Principal;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.scalea.configurations.Messages;
import com.scalea.entities.Area;
import com.scalea.entities.Process;
import com.scalea.entities.Product;
import com.scalea.entities.User;
import com.scalea.enums.ProcessStatus;
import com.scalea.exceptions.GenericException;
import com.scalea.models.dto.ControlProductDTO;
import com.scalea.models.dto.ControlSubProductDTO;
import com.scalea.services.AreaService;
import com.scalea.services.ProcessService;
import com.scalea.services.ProductService;
import com.scalea.services.UserService;
import com.scalea.utils.Constants;

@Controller
@RequestMapping("/control")
public class ControlController {
	private ProductService productService;
	private AreaService areaService;
	private UserService userService;
	private ProcessService processService;
	private Logger log;
	private Messages messages;
	
	@Autowired
	public ControlController(ProductService productService, AreaService areaService, UserService userService, ProcessService processService, 
			Messages messages) {
		this.productService = productService;
		this.areaService = areaService;
		this.userService = userService;
		this.processService = processService;
		this.log = LoggerFactory.getLogger(ControlController.class);
		this.messages = messages;
	}
	
	@PreAuthorize("hasAnyAuthority('" + Constants.MANAGE_CONTROL_PRIVILEGE + "')")
	@GetMapping
	public String loadStep1(Model model, Principal principal, HttpServletRequest request) throws GenericException {
		log.info("Method loadStep1()");
		
		Iterable<Product> products = productService.findByFatherProductIsNullAndEnabledIsTrueOrderByName();
		List<Area> areas = null;
		ControlProductDTO controlProductDTO = new ControlProductDTO();
		
		if (request.isUserInRole(Constants.ROLE_ADMIN)) {
			areas = (List<Area>) areaService.findByEnabledOrderByName();
		} else {
			User user = userService.findByUsername(principal.getName());
			areas = (List<Area>) areaService.findByUserId(user);
			if (areas != null && areas.size() == 1) controlProductDTO.setAreaId(areas.get(0).getId());
			
			if (areas.size() == 1) {
				Optional<Process> optionalProcess = processService.findByStatusesAndArea(new int[] {ProcessStatus.STARTED.getStatus(), 
					ProcessStatus.PAUSED.getStatus()}, areas.get(0));
				if (optionalProcess.isPresent()) {
					Process activeProcess = optionalProcess.get();
					Area area = areas.get(0);
					
					ControlSubProductDTO controlSubProductDTO = new ControlSubProductDTO();
					controlSubProductDTO.setAreaId(area.getId());
					controlSubProductDTO.setAreaName(area.getName());
					controlSubProductDTO.setActiveProcess(activeProcess);
					
					if (activeProcess.getProduct().getFatherProduct() != null) {
						controlSubProductDTO.setProductId(activeProcess.getProduct().getFatherProduct().getId());
						controlSubProductDTO.setProductName(activeProcess.getProduct().getFatherProduct().getName());
						controlSubProductDTO.setSubProductId(activeProcess.getProduct().getId());
						controlSubProductDTO.setSubProductName(activeProcess.getProduct().getName());
					} else {
						controlSubProductDTO.setProductId(activeProcess.getProduct().getId());
						controlSubProductDTO.setProductName(activeProcess.getProduct().getName());
					}
					
					return this.loadStep3(model, controlSubProductDTO, null, principal, request);
				}
			}
		}
		
		model.addAttribute("products", products);
		model.addAttribute("areas", areas);
		if (model.getAttribute("controlProductDTO") == null) model.addAttribute("controlProductDTO", controlProductDTO);
		return "private/control/controlproduct";
	}
	
	@PreAuthorize("hasAnyAuthority('" + Constants.MANAGE_CONTROL_PRIVILEGE + "')")
	@GetMapping("/subproduct")
	public String loadStep2(Model model, @Valid ControlProductDTO controlProductDto, Errors errors, Principal principal, HttpServletRequest request, 
			RedirectAttributes redirectAttributes) throws GenericException {
		log.info("Method loadStep2()");
		
		if (errors.hasErrors()) {
			return this.loadStep1(model, principal, request);
		}
		
		Optional<Area> optionalArea = areaService.findByIdAndEnabledIsTrue(controlProductDto.getAreaId());
		if (optionalArea.isEmpty()) throw new GenericException(messages.get("messages.area.not.found"));
		Area area = optionalArea.get();
		
		User user = userService.findByUsername(principal.getName());
		if (area.getUser() == null || !area.getUser().getId().equals(user.getId())) throw new GenericException(messages.get("messages.area.not.found"));
		
		Optional<Product> optionalProduct = productService.findByIdAndFatherProductIsNullAndEnabledIsTrue(controlProductDto.getProductId());
		if (optionalProduct.isEmpty()) throw new GenericException(messages.get("messages.product.not.found"));
		Product product = optionalProduct.get();
		
		ControlSubProductDTO controlSubProduct = new ControlSubProductDTO();
		controlSubProduct.setAreaId(controlProductDto.getAreaId());
		controlSubProduct.setProductId(controlProductDto.getProductId());
		
		if (product.isWithSubProducts() && product.getChildrenProducts() != null && product.getChildrenProducts().size() > 0) {
			model.addAttribute("products", product.getChildrenProducts());
			model.addAttribute("controlSubProductDTO", controlSubProduct);
		} else if (!product.isWithSubProducts()) {
			redirectAttributes.addAttribute("productId", controlSubProduct.getProductId());
			redirectAttributes.addAttribute("areaId", controlSubProduct.getAreaId());
			return "redirect:/control/process";
		} else {
			throw new GenericException(messages.get("messages.product.not.found"));
		}
		
		return "private/control/controlsubproduct";
	}
	
	@PreAuthorize("hasAnyAuthority('" + Constants.MANAGE_CONTROL_PRIVILEGE + "')")
	@GetMapping("/process")
	public String loadStep3(Model model, @Valid ControlSubProductDTO controlSubProductDTO, Errors errors, Principal principal, HttpServletRequest request) throws GenericException {
		log.info("Method loadStep3()");
		
		Optional<Area> optionalArea = areaService.findByIdAndEnabledIsTrue(controlSubProductDTO.getAreaId());
		if (optionalArea.isEmpty()) throw new GenericException(messages.get("messages.area.not.found"));
		Area area = optionalArea.get();
		
		User user = userService.findByUsername(principal.getName());
		if (area.getUser() == null || !area.getUser().getId().equals(user.getId())) throw new GenericException(messages.get("messages.area.not.found"));
		
		Optional<Product> optionalProduct = productService.findByIdAndFatherProductIsNullAndEnabledIsTrue(controlSubProductDTO.getProductId());
		if (optionalProduct.isEmpty()) throw new GenericException(messages.get("messages.product.not.found"));
		Product product = optionalProduct.get();
		
		Optional<Product> optionalSubProduct = Optional.empty();
		if (product.isWithSubProducts() && controlSubProductDTO.getSubProductId() == null) {
			throw new GenericException(messages.get("messages.product.not.found"));
		} else if (product.isWithSubProducts()) {
			optionalSubProduct = productService.findByIdAndFatherProductAndEnabledIsTrue(controlSubProductDTO.getSubProductId(), product);
		}
		
		if (product.isWithSubProducts() && optionalSubProduct.isEmpty()) throw new GenericException(messages.get("messages.product.not.found"));
		
		controlSubProductDTO.setAreaName(area.getName());
		controlSubProductDTO.setProductName(product.getName());
		if (optionalSubProduct.isPresent()) controlSubProductDTO.setSubProductName(optionalSubProduct.get().getName());
		
		model.addAttribute("controlSubProductDTO", controlSubProductDTO);
		return "private/control/controlprocess";
	}
	
	@PreAuthorize("hasAnyAuthority('" + Constants.MANAGE_PROCESSES_PRIVILEGE + "')")
	@PostMapping("/process/start")
	public String startProcess(Model model, @Valid ControlSubProductDTO controlSubProductDTO, Principal principal, 
			RedirectAttributes redirectAttributes, @RequestParam("page") Optional<Integer> page, HttpServletRequest request) throws GenericException {
		log.info("Method startProcess()");
		
		Optional<Area> optionalArea = areaService.findByIdAndEnabledIsTrue(controlSubProductDTO.getAreaId());
		if (optionalArea.isEmpty()) throw new GenericException(messages.get("messages.area.not.found"));
		Area area = optionalArea.get();
		
		User user = userService.findByUsername(principal.getName());
		if (area.getUser() == null || !area.getUser().getId().equals(user.getId())) throw new GenericException(messages.get("messages.area.not.found"));
		
		Optional<Product> optionalProduct = productService.findByIdAndFatherProductIsNullAndEnabledIsTrue(controlSubProductDTO.getProductId());
		if (optionalProduct.isEmpty()) throw new GenericException(messages.get("messages.product.not.found"));
		Product product = optionalProduct.get();
		
		Optional<Product> optionalSubProduct = Optional.empty();
		if (product.isWithSubProducts() && controlSubProductDTO.getSubProductId() == null) {
			throw new GenericException(messages.get("messages.product.not.found"));
		} else if (product.isWithSubProducts()) {
			optionalSubProduct = productService.findByIdAndFatherProductAndEnabledIsTrue(controlSubProductDTO.getSubProductId(), product);
		}
		
		if (product.isWithSubProducts() && optionalSubProduct.isEmpty()) throw new GenericException(messages.get("messages.product.not.found"));
		Product selectedProduct = optionalSubProduct.isPresent() ? optionalSubProduct.get() : product;
		
		if (processService.existsByStatusAndArea(new int[] {ProcessStatus.STARTED.getStatus(), ProcessStatus.PAUSED.getStatus()}, area)) {
			model.addAttribute("message", this.messages.get("messages.process.already.started.in.area", area.getName()));
			model.addAttribute("alertClass", "alert-danger");
			
			return this.loadStep3(model, controlSubProductDTO, null, principal, request);
		}
		
		Process process = new Process();
		process.setStatus(ProcessStatus.STARTED.getStatus());
		process.setStartedAt(new Date());
		process.setProduct(selectedProduct);
		process.setArea(area);
		process.setUser(userService.findByUsername(principal.getName()));
		process.setElapsedTime(0L);
		
		processService.save(process);
	    
		controlSubProductDTO.setActiveProcess(process);
	    model.addAttribute("controlSubProductDTO", controlSubProductDTO);
	    model.addAttribute("message", this.messages.get("messages.process.started", area.getName()));
		model.addAttribute("alertClass", "alert-success");
		
		return this.loadStep3(model, controlSubProductDTO, null, principal, request);
	}
	
	@PreAuthorize("hasAnyAuthority('" + Constants.MANAGE_PROCESSES_PRIVILEGE + "')")
	@PostMapping("/process/pause")
	public String pauseProcess(Model model, @Valid ControlSubProductDTO controlSubProductDTO, Principal principal, 
			RedirectAttributes redirectAttributes, @RequestParam("page") Optional<Integer> page, HttpServletRequest request) throws GenericException {
		log.info("Method pauseProcess()");
		
		Optional<Area> optionalArea = areaService.findByIdAndEnabledIsTrue(controlSubProductDTO.getAreaId());
		if (optionalArea.isEmpty()) throw new GenericException(messages.get("messages.area.not.found"));
		Area area = optionalArea.get();
		
		User user = userService.findByUsername(principal.getName());
		if (area.getUser() == null || !area.getUser().getId().equals(user.getId())) throw new GenericException(messages.get("messages.area.not.found"));
		
		Optional<Product> optionalProduct = productService.findByIdAndFatherProductIsNullAndEnabledIsTrue(controlSubProductDTO.getProductId());
		if (optionalProduct.isEmpty()) throw new GenericException(messages.get("messages.product.not.found"));
		Product product = optionalProduct.get();
		
		Optional<Product> optionalSubProduct = Optional.empty();
		if (product.isWithSubProducts() && controlSubProductDTO.getSubProductId() == null) {
			throw new GenericException(messages.get("messages.product.not.found"));
		} else if (product.isWithSubProducts()) {
			optionalSubProduct = productService.findByIdAndFatherProductAndEnabledIsTrue(controlSubProductDTO.getSubProductId(), product);
		}
		
		if (product.isWithSubProducts() && optionalSubProduct.isEmpty()) throw new GenericException(messages.get("messages.product.not.found"));
		
		Process activeProcess = controlSubProductDTO.getActiveProcess();
		
		if (activeProcess.getStatus() == ProcessStatus.STARTED.getStatus()) {
			activeProcess.setStatus(ProcessStatus.PAUSED.getStatus());
			activeProcess.setStoppedAt(new Date());
			activeProcess.calculateElapsedTime();
			
			processService.save(activeProcess);
		}
	    
		controlSubProductDTO.setActiveProcess(activeProcess);
	    model.addAttribute("controlSubProductDTO", controlSubProductDTO);
	    model.addAttribute("message", this.messages.get("messages.process.paused", area.getName()));
		model.addAttribute("alertClass", "alert-success");
		
		return this.loadStep3(model, controlSubProductDTO, null, principal, request);
	}
	
	@PreAuthorize("hasAnyAuthority('" + Constants.MANAGE_PROCESSES_PRIVILEGE + "')")
	@PostMapping("/process/resume")
	public String resumeProcess(Model model, @Valid ControlSubProductDTO controlSubProductDTO, Principal principal, 
			RedirectAttributes redirectAttributes, @RequestParam("page") Optional<Integer> page, HttpServletRequest request) throws GenericException {
		log.info("Method resumeProcess()");
		
		Optional<Area> optionalArea = areaService.findByIdAndEnabledIsTrue(controlSubProductDTO.getAreaId());
		if (optionalArea.isEmpty()) throw new GenericException(messages.get("messages.area.not.found"));
		Area area = optionalArea.get();
		
		User user = userService.findByUsername(principal.getName());
		if (area.getUser() == null || !area.getUser().getId().equals(user.getId())) throw new GenericException(messages.get("messages.area.not.found"));
		
		Optional<Product> optionalProduct = productService.findByIdAndFatherProductIsNullAndEnabledIsTrue(controlSubProductDTO.getProductId());
		if (optionalProduct.isEmpty()) throw new GenericException(messages.get("messages.product.not.found"));
		Product product = optionalProduct.get();
		
		Optional<Product> optionalSubProduct = Optional.empty();
		if (product.isWithSubProducts() && controlSubProductDTO.getSubProductId() == null) {
			throw new GenericException(messages.get("messages.product.not.found"));
		} else if (product.isWithSubProducts()) {
			optionalSubProduct = productService.findByIdAndFatherProductAndEnabledIsTrue(controlSubProductDTO.getSubProductId(), product);
		}
		
		if (product.isWithSubProducts() && optionalSubProduct.isEmpty()) throw new GenericException(messages.get("messages.product.not.found"));
		
		Process activeProcess = controlSubProductDTO.getActiveProcess();
		
		if (activeProcess.getStatus() == ProcessStatus.PAUSED.getStatus()) {
			activeProcess.setStatus(ProcessStatus.STARTED.getStatus());
			activeProcess.setStartedAt(new Date());
			activeProcess.setStoppedAt(null);
			
			processService.save(activeProcess);
		}
	    
		controlSubProductDTO.setActiveProcess(activeProcess);
	    model.addAttribute("controlSubProductDTO", controlSubProductDTO);
	    model.addAttribute("message", this.messages.get("messages.process.resumed", area.getName()));
		model.addAttribute("alertClass", "alert-success");
		
		return this.loadStep3(model, controlSubProductDTO, null, principal, request);
	}
	
	@PreAuthorize("hasAnyAuthority('" + Constants.MANAGE_PROCESSES_PRIVILEGE + "')")
	@PostMapping("/process/stop")
	public String stopProcess(Model model, @Valid ControlSubProductDTO controlSubProductDTO, Principal principal, 
			RedirectAttributes redirectAttributes, @RequestParam("page") Optional<Integer> page, HttpServletRequest request) throws GenericException {
		log.info("Method stopProcess()");
		
		Optional<Area> optionalArea = areaService.findByIdAndEnabledIsTrue(controlSubProductDTO.getAreaId());
		if (optionalArea.isEmpty()) throw new GenericException(messages.get("messages.area.not.found"));
		Area area = optionalArea.get();
		
		User user = userService.findByUsername(principal.getName());
		if (area.getUser() == null || !area.getUser().getId().equals(user.getId())) throw new GenericException(messages.get("messages.area.not.found"));
		
		Optional<Product> optionalProduct = productService.findByIdAndFatherProductIsNullAndEnabledIsTrue(controlSubProductDTO.getProductId());
		if (optionalProduct.isEmpty()) throw new GenericException(messages.get("messages.product.not.found"));
		Product product = optionalProduct.get();
		
		Optional<Product> optionalSubProduct = Optional.empty();
		if (product.isWithSubProducts() && controlSubProductDTO.getSubProductId() == null) {
			throw new GenericException(messages.get("messages.product.not.found"));
		} else if (product.isWithSubProducts()) {
			optionalSubProduct = productService.findByIdAndFatherProductAndEnabledIsTrue(controlSubProductDTO.getSubProductId(), product);
		}
		
		if (product.isWithSubProducts() && optionalSubProduct.isEmpty()) throw new GenericException(messages.get("messages.product.not.found"));
		
		Process activeProcess = controlSubProductDTO.getActiveProcess();
		
		if (activeProcess.getStatus() != ProcessStatus.FINISHED.getStatus()) {
			activeProcess.setStatus(ProcessStatus.FINISHED.getStatus());
			activeProcess.setStoppedAt(new Date());
			activeProcess.calculateElapsedTime();
			
			processService.save(activeProcess);
		}
	    
		model.addAttribute("message", this.messages.get("messages.process.finished", area.getName()));
		model.addAttribute("alertClass", "alert-success");
		
		return this.loadStep1(model, principal, request);
	}
}
