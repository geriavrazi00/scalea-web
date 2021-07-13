package com.scalea.controllers;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.scalea.configurations.Messages;
import com.scalea.entities.Area;
import com.scalea.entities.Group;
import com.scalea.entities.User;
import com.scalea.entities.Vacancy;
import com.scalea.exceptions.GenericException;
import com.scalea.models.dto.AreaDTO;
import com.scalea.services.AreaService;
import com.scalea.services.GroupService;
import com.scalea.services.UserService;
import com.scalea.services.VacancyService;
import com.scalea.utils.Constants;
import com.scalea.utils.Utils;

@Controller
@RequestMapping("/areas")
public class AreaController {

	private AreaService areaService;
	private VacancyService vacancyService;
	private UserService userService;
	private GroupService groupService;
	private Logger log;
	private Messages messages;
	
	@Autowired
	public AreaController(AreaService areaService, VacancyService vacancyService, UserService userService, GroupService groupService,
			Messages messages) {
		this.areaService = areaService;
		this.vacancyService = vacancyService;
		this.userService = userService;
		this.groupService = groupService;
		this.log = LoggerFactory.getLogger(AreaController.class);
		this.messages = messages;
	}
	
	@PreAuthorize("hasAnyAuthority('" + Constants.VIEW_AREAS_PRIVILEGE + "', '" + Constants.UPSERT_AREAS_PRIVILEGE + "', '" + Constants.DELETE_AREAS_PRIVILEGE + "')")
	@GetMapping
	public String allAreas(Model model, @RequestParam("page") Optional<Integer> page, @RequestParam("size") Optional<Integer> size) {
		log.info("Method allAreas()");
		
		int currentPage = page.orElse(Constants.DEFAULT_PAGE);
        int pageSize = size.orElse(Constants.DEFAULT_SIZE);
        
		Page<Area> areas = areaService.findPaginatedByEnabledOrderByName(PageRequest.of(currentPage - 1, pageSize));
		
		for (Area area: areas.getContent()) {
			area.calculateEmployeeNumber();
		}
		
		Iterable<User> users = userService.findUserWithPrivilege(Constants.MANAGE_CONTROL_PRIVILEGE);
		
		model.addAttribute("areas", areas);
		model.addAttribute("users", users);
		if (model.getAttribute("area") == null) model.addAttribute("area", new Area());
		if (model.getAttribute("areaDTO") == null) model.addAttribute("areaDTO", new AreaDTO());
		model.addAttribute("pageNumbers", Utils.getPageNumbersList(areas.getTotalPages()));
		return "private/areas/arealist";
	}
	
	@PreAuthorize("hasAnyAuthority('" + Constants.UPSERT_AREAS_PRIVILEGE + "')")
	@GetMapping("/{id}")
	public @ResponseBody AreaDTO getArea(@PathVariable("id") Long id) throws GenericException {
		log.info("Method getArea()");
		
		Optional<Area> area = areaService.findById(id);
		if (!area.isPresent()) throw new GenericException(messages.get("messages.area.not.found"));
		
		AreaDTO newArea = new AreaDTO();
		newArea.toDTO(area.get());
		return newArea;
	}
	
	@PreAuthorize("hasAuthority('" + Constants.UPSERT_AREAS_PRIVILEGE + "'")
	@PostMapping("/create")
	public String createArea(@Valid Area area, Errors errors, Model model, RedirectAttributes redirectAttributes, 
			@RequestParam("page") Optional<Integer> page, @RequestParam("size") Optional<Integer> size) {
		log.info("Method createArea()");
		
		if (errors.hasErrors()) {
			return this.allAreas(model, page, size);
		}
		
		if (areaService.existsByName(area.getName())) {
			model.addAttribute("message", this.messages.get("messages.area.exists", area.getName()));
			model.addAttribute("alertClass", "alert-danger");
			return this.allAreas(model, page, size);
		}
		
		// Creating the area
		area.setEnabled(true);
		area.setUuid(Utils.generateUniqueVacancyCodes());
		area = this.areaService.save(area);
		
		// Creating the vacancies
		for (int i = 0; i < area.getCapacity(); i++) {
			Vacancy vacancy = new Vacancy();
			vacancy.setNumber(i + 1);
			vacancy.setUuid(Utils.generateUniqueVacancyCodes());
			vacancy.setArea(area);
			
			this.vacancyService.save(vacancy);
		}
		
		// Creating the default group
		Group group = new Group();
		group.setName("1");
		group.setDefaultGroup(true);
		group.setArea(area);
		this.groupService.save(group);
		
		redirectAttributes.addFlashAttribute("message", this.messages.get("messages.area.created"));
	    redirectAttributes.addFlashAttribute("alertClass", "alert-success");
		return "redirect:/areas" + paginationParameters(page, size);
	}
	
	@PreAuthorize("hasAuthority('" + Constants.UPSERT_AREAS_PRIVILEGE + "'")
	@PostMapping("/update")
	public String updateArea(@Valid AreaDTO selectedArea, Errors errors, Model model, RedirectAttributes redirectAttributes, 
			@RequestParam("page") Optional<Integer> page, @RequestParam("size") Optional<Integer> size) throws GenericException {
		log.info("Method updateArea()");
		
		if (errors.hasErrors()) {
			return this.allAreas(model, page, size);
		}
		
		Optional<Area> existingOptionalArea = areaService.findById(selectedArea.getId());
		if (!existingOptionalArea.isPresent()) throw new GenericException(messages.get("messages.area.not.found"));
		Area existingArea = existingOptionalArea.get();
		
		// Checking if the modified name of the area is equal to the actual one. If it is, we check if there is already an area with the chosen name in the system
		if (!existingArea.getName().equals(selectedArea.getName())) {
			if (areaService.existsByName(selectedArea.getName())) {
				model.addAttribute("message", this.messages.get("messages.area.exists", selectedArea.getName()));
				model.addAttribute("alertClass", "alert-danger");
				return this.allAreas(model, page, size);
			}
		}
		
		// We check if a user is selected. If so and if the user actually exists in the system, we associate it to the area
		if (selectedArea.getUserId() != null) {
			Optional<User> optionalUser = userService.findById(selectedArea.getUserId());
			if (optionalUser.isPresent()) existingArea.setUser(optionalUser.get()); 
		} else {
			existingArea.setUser(null); 
		}
		
		/*
		 * Checking the capacity. It may remain the same, increase or decrease. If it increases or it is the same we should simply create the additional
		 * vacancies. If it decreases, we should check first if there are employees associated to the area. We do not decrease the capacity if there 
		 * are employees associated.
		*/
		if (selectedArea.getCapacity() >= existingArea.getCapacity()) {
			int newVacancies = selectedArea.getCapacity() - existingArea.getCapacity();
			int lastVacancyNumber = vacancyService.findMaxVacancyNumberOfArea(existingArea) + 1;
			
			for (int i = 0; i < newVacancies; i++) {
				Vacancy vacancy = new Vacancy();
				vacancy.setNumber(i + lastVacancyNumber);
				vacancy.setUuid(Utils.generateUniqueVacancyCodes());
				vacancy.setArea(existingArea);
				
				this.vacancyService.save(vacancy);
			}
			
			selectedArea.mergeWithExistingArea(existingArea);
			this.areaService.save(existingArea);
		} else {
			int employeeAssociations = 0;
			
			if (existingArea.getVacancies() != null && existingArea.getVacancies().size() > 0) {
				for (Vacancy vacancy: existingArea.getVacancies()) {
					if (vacancy.getEmployee() != null) employeeAssociations++;
				}
			}
			
			if (employeeAssociations > 0) {
				model.addAttribute("message", this.messages.get("message.area.capacity.cannot.be.decreased", employeeAssociations));
				model.addAttribute("alertClass", "alert-danger");
				return "private/areas/editarea";
			} else {
				int vacanciesToDelete = existingArea.getCapacity() - selectedArea.getCapacity();
				List<Vacancy> toDelete = new ArrayList<>();
				
				for (int i = existingArea.getVacancies().size() - 1; i >= 0; i--) {
					if (vacanciesToDelete == 0) break;
					
					Vacancy v = (Vacancy) existingArea.getVacancies().toArray()[i];
					toDelete.add(v);
					vacanciesToDelete--;
				}
				
				for (Vacancy vacancy: toDelete) {
					existingArea.getVacancies().remove(vacancy);
					this.vacancyService.delete(vacancy);
				}
				
				selectedArea.mergeWithExistingArea(existingArea);
				this.areaService.save(existingArea);
			}
		}
		
		redirectAttributes.addFlashAttribute("message", this.messages.get("messages.area.updated"));
	    redirectAttributes.addFlashAttribute("alertClass", "alert-success");
		return "redirect:/areas" + paginationParameters(page, size);
	}
	
	/*
	 * An area should not be deleted if its vacancies have employees associated to it. Otherwise we disable it
	 */
	@PreAuthorize("hasAuthority('" + Constants.DELETE_AREAS_PRIVILEGE + "'")
	@PostMapping("/delete/{id}")
	public String deleteArea(@PathVariable("id") Long id, @RequestParam("page") Optional<Integer> page, @RequestParam("size") Optional<Integer> size,
			RedirectAttributes redirectAttributes) throws GenericException {
		log.info("Method deleteArea()");
		
		Optional<Area> area = areaService.findById(id);
		
		if (!area.isPresent()) throw new GenericException(messages.get("messages.area.not.found"));
		Area foundArea = area.get();
		int employeeAssociations = 0;
		
		if (foundArea.getVacancies() != null && foundArea.getVacancies().size() > 0) {
			for (Vacancy vacancy: foundArea.getVacancies()) {
				if (vacancy.getEmployee() != null) employeeAssociations++;
			}
		}
		
		if (employeeAssociations > 0) {
			redirectAttributes.addFlashAttribute("message", this.messages.get("message.cannot.delete.area", employeeAssociations));
		    redirectAttributes.addFlashAttribute("alertClass", "alert-danger");
		} else {
			redirectAttributes.addFlashAttribute("message", this.messages.get("messages.area.deleted"));
		    redirectAttributes.addFlashAttribute("alertClass", "alert-success");
		    
		    
		    if (foundArea.getVacancies() != null && foundArea.getVacancies().size() > 0) {
				for (Vacancy vacancy: foundArea.getVacancies()) {
					vacancy.setEnabled(false);
					this.vacancyService.save(vacancy);
				}
			}
		    
		    foundArea.setName("disabled--" + System.currentTimeMillis() + "--" + foundArea.getName());
		    foundArea.setEnabled(false);
		    this.areaService.save(foundArea);
		}
		
		return "redirect:/areas" + paginationParameters(page, size);
	}
	
	@PreAuthorize("hasAuthority('" + Constants.VIEW_AREAS_PRIVILEGE + "'")
	@GetMapping("/download/{id}")
	public ResponseEntity<byte[]> downloadAreaBarcode(@PathVariable("id") Long id, @RequestParam("page") Optional<Integer> page, 
			@RequestParam("size") Optional<Integer> size, RedirectAttributes redirectAttributes) throws GenericException {
		log.info("Method downloadAreaBarcode()");
		
		Optional<Area> optionalArea = areaService.findById(id);
		if (!optionalArea.isPresent()) throw new GenericException(messages.get("messages.area.not.found"));
		Area area = optionalArea.get();
		
		String areaName = area.getName().trim().replaceAll(this.messages.get("messages.area"), "");
		String barcodeLabel = this.messages.get("messages.area") + " " + areaName 
			+ ", " + this.messages.get("messages.supervisor").toLowerCase();
		
		String encodedBarcode = Utils.getBarCodeImage(area.getUuid(), 450, 80, barcodeLabel);
		byte[] decodedBarcode = Base64.getDecoder().decode(encodedBarcode);
		
		areaName = areaName.replaceAll(" ", "_");
		if (areaName.startsWith("_")) areaName = areaName.substring(1);
		String fileName = this.messages.get("messages.area") + "_" + areaName 
			+ "-" + this.messages.get("messages.supervisor") + "-" + System.currentTimeMillis() + ".png";
		
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set(HttpHeaders.CONTENT_TYPE, MediaType.IMAGE_PNG_VALUE); // Content-Type: image/jpeg
        httpHeaders.set("Content-Disposition", "attachment; filename=" + fileName); // Content-Disposition: attachment; filename="demo-file.txt"
        return ResponseEntity.ok().headers(httpHeaders).body(decodedBarcode); // Return Response
	}
	
	private String paginationParameters(Optional<Integer> page, Optional<Integer> size) {
		return "?page=" + page.orElse(Constants.DEFAULT_PAGE) + "&size=" + size.orElse(Constants.DEFAULT_SIZE);
	}
}
