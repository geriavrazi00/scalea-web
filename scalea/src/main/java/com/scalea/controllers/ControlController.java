package com.scalea.controllers;

import java.util.Optional;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.scalea.configurations.Messages;
import com.scalea.entities.Area;
import com.scalea.entities.Product;
import com.scalea.exceptions.GenericException;
import com.scalea.models.dto.ControlStep1DTO;
import com.scalea.models.dto.ControlStep2DTO;
import com.scalea.services.AreaService;
import com.scalea.services.ProductService;
import com.scalea.utils.Constants;

@Controller
@RequestMapping("/control")
public class ControlController {
	private ProductService productService;
	private AreaService areaService;
	private Logger log;
	private Messages messages;
	
	@Autowired
	public ControlController(ProductService productService, AreaService areaService, Messages messages) {
		this.productService = productService;
		this.areaService = areaService;
		this.log = LoggerFactory.getLogger(ControlController.class);
		this.messages = messages;
	}
	
	@PreAuthorize("hasAnyAuthority('" + Constants.MANAGE_CONTROL_PRIVILEGE + "')")
	@GetMapping
	public String loadStep1(Model model) {
		log.info("Method loadStep1()");
		
		Iterable<Product> products = productService.findByFatherProductIsNullAndEnabledIsTrueOrderByName();
		Iterable<Area> areas = areaService.findByEnabledOrderByName();
		
		model.addAttribute("products", products);
		model.addAttribute("areas", areas);
		model.addAttribute("controlDto", new ControlStep1DTO());
		return "private/control/controlstep1";
	}
	
	@PreAuthorize("hasAnyAuthority('" + Constants.MANAGE_CONTROL_PRIVILEGE + "')")
	@GetMapping("/subproduct")
	public String loadStep2(Model model, @Valid ControlStep1DTO step1dto, Errors errors) throws GenericException {
		log.info("Method loadStep2()");
		
		if (errors.hasErrors()) {
			return this.loadStep1(model);
		}
		
		Optional<Area> optionalArea = areaService.findByIdAndEnabledIsTrue(step1dto.getAreaId());
		if (optionalArea.isEmpty()) throw new GenericException(messages.get("messages.area.not.found"));
		Optional<Product> optionalProduct = productService.findByIdAndFatherProductIsNullAndEnabledIsTrue(step1dto.getProductId());
		if (optionalProduct.isEmpty()) throw new GenericException(messages.get("messages.product.not.found"));
		
		if (optionalProduct.get().isWithSubProducts() && optionalProduct.get().getChildrenProducts() != null && optionalProduct.get().getChildrenProducts().size() > 0) {
			model.addAttribute("products", optionalProduct.get().getChildrenProducts());
			model.addAttribute("controlDto2", new ControlStep2DTO());
		} else {
			
		}
		
		
		return "private/control/controlstep2";
	}
}
