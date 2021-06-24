package com.scalea.controllers;

import java.text.ParseException;
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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.scalea.configurations.Messages;
import com.scalea.entities.Area;
import com.scalea.entities.Process;
import com.scalea.entities.Product;
import com.scalea.exceptions.GenericException;
import com.scalea.services.AreaService;
import com.scalea.services.ProcessService;
import com.scalea.services.ProductService;
import com.scalea.utils.Constants;
import com.scalea.utils.Utils;

@Controller
@RequestMapping("/areas/{id}/historic")
public class AreaHistoricController {
	
	private AreaService areaService;
	private ProcessService processService;
	private ProductService productService;
	private Logger log;
	private Messages messages;
	
	private static final int DEFAULT_PAGE = 1;
	private static final int DEFAULT_SIZE = 7;
	
	@Autowired
	public AreaHistoricController(AreaService areaService, ProcessService processService, ProductService productService, Messages messages) {
		this.areaService = areaService;
		this.processService = processService;
		this.productService = productService;
		this.log = LoggerFactory.getLogger(AreaHistoricController.class);
		this.messages = messages;
	}
	
	@PreAuthorize("hasAnyAuthority('" + Constants.VIEW_PROCESSES_HISTORIC_PRIVELEGE + "')")
	@GetMapping
	public String showHistoric(Model model, @PathVariable("id") Long id, @RequestParam("page") Optional<Integer> page, @RequestParam("size") Optional<Integer> size, 
			@RequestParam("date") Optional<String> date, @RequestParam("product") Optional<Long> product) throws GenericException, ParseException {
		log.info("Method showHistoric()");
		
		int currentPage = page.orElse(DEFAULT_PAGE);
        int pageSize = size.orElse(DEFAULT_SIZE);
        Date startedAt = date.orElse(null) == null ? null : Utils.inputDateStringToDate(date.orElse(null));
        Optional<Product> selectedProduct = productService.findById(product.orElse(0L));
        
        Optional<Area> optionalArea = areaService.findById(id);
		if (!optionalArea.isPresent()) throw new GenericException(messages.get("messages.area.not.found"));
		Area area = optionalArea.get();
		area.calculateEmployeeNumber();
		
		Optional<Process> latestProcess = processService.findFirstByAreaOrderByStartedAtDesc(area);
		Iterable<Product> products = productService.findByEnabledIsTrueAndWithSubProductsIsFalse();
		Page<Process> processes = processService.findFilteredProcesses(area, startedAt, selectedProduct, PageRequest.of(currentPage - 1, pageSize));
		
        if (product.isPresent() && selectedProduct.isEmpty()) {
        	model.addAttribute("message", this.messages.get("messages.product.selected.not.exists"));
        	model.addAttribute("alertClass", "alert-warning");
        }
		
		model.addAttribute("area", area);
		model.addAttribute("processes", processes);
		model.addAttribute("process", latestProcess);
		model.addAttribute("products", products);
		model.addAttribute("selectedDate", date);
		model.addAttribute("selectedProduct", product);
		model.addAttribute("pageNumbers", Utils.getPageNumbersList(processes.getTotalPages()));
		return "private/areas/historic/historiclist";
	}
}
