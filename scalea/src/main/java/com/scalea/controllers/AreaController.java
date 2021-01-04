package com.scalea.controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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
import com.scalea.entities.Vacancy;
import com.scalea.exceptions.GenericException;
import com.scalea.models.dto.AreaDTO;
import com.scalea.repositories.VacancyRepository;
import com.scalea.services.AreaService;
import com.scalea.utils.Constants;
import com.scalea.utils.Utils;

@Controller
@RequestMapping("/areas")
public class AreaController {

	private AreaService areaService;
	private VacancyRepository vacancyRepo;
	private Logger log;
	private Messages messages;
	
	private static final int DEFAULT_PAGE = 1;
	private static final int DEFAULT_SIZE = 7;
	
	@Autowired
	public AreaController(AreaService areaService, VacancyRepository vacancyRepo, Messages messages) {
		this.areaService = areaService;
		this.vacancyRepo = vacancyRepo;
		this.log = LoggerFactory.getLogger(AreaController.class);
		this.messages = messages;
	}
	
	@PreAuthorize("hasAnyAuthority('" + Constants.VIEW_AREAS_PRIVILEGE + "', '" + Constants.UPSERT_AREAS_PRIVILEGE + "', '" + Constants.DELETE_AREAS_PRIVILEGE + "')")
	@GetMapping
	public String allAreas(Model model, @RequestParam("page") Optional<Integer> page, @RequestParam("size") Optional<Integer> size) {
		log.info("Method allAreas()");
		
		int currentPage = page.orElse(DEFAULT_PAGE);
        int pageSize = size.orElse(DEFAULT_SIZE);
        
		Page<Area> areas = areaService.findPaginatedByEnabledOrderByName(PageRequest.of(currentPage - 1, pageSize));
		
		for (Area area: areas.getContent()) {
			area.calculateEmployeeNumber();
		}
		
		model.addAttribute("areas", areas);
		if (model.getAttribute("area") == null) model.addAttribute("area", new Area());
		if (model.getAttribute("areaDTO") == null) model.addAttribute("areaDTO", new AreaDTO());
		areaService.setPageNumberToModel(model, areas.getTotalPages());
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
		
		area.setEnabled(true);
		area = this.areaService.save(area);
		
		for (int i = 0; i < area.getCapacity(); i++) {
			Vacancy vacancy = new Vacancy();
			vacancy.setNumber(i + 1);
			vacancy.setUuid(Utils.generateUniqueVacancyCodes());
			vacancy.setArea(area);
			
			this.vacancyRepo.save(vacancy);
		}
		
		redirectAttributes.addFlashAttribute("message", this.messages.get("messages.area.created"));
	    redirectAttributes.addFlashAttribute("alertClass", "alert-success");
		return "redirect:/areas";
	}
	
	@PreAuthorize("hasAuthority('" + Constants.UPSERT_AREAS_PRIVILEGE + "'")
	@GetMapping("edit/{id}")
	public String editArea(@PathVariable("id") Long id, Model model) throws Exception {
		log.info("Method editArea()");
		
		Optional<Area> area = areaService.findById(id);
		if (!area.isPresent()) throw new GenericException(messages.get("messages.area.not.found"));
		
		model.addAttribute("area", area.get());
		return "private/areas/editarea";
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
		
		/*
		 * Checking the capacity. It may remain the same, increase or decrease. If it increases or it is the same we should simply create the additional
		 * vacancies. If it decreases, we should check first if there are employees associated to the area. We do not decrease the capacity if there 
		 * are employees associated.
		*/
		if (selectedArea.getCapacity() >= existingArea.getCapacity()) {
			int newVacancies = selectedArea.getCapacity() - existingArea.getCapacity();
			int lastVacancyNumber = vacancyRepo.findMaxVacancyNumberOfArea(existingArea) + 1;
			
			for (int i = 0; i < newVacancies; i++) {
				Vacancy vacancy = new Vacancy();
				vacancy.setNumber(i + lastVacancyNumber);
				vacancy.setUuid(Utils.generateUniqueVacancyCodes());
				vacancy.setArea(existingArea);
				
				this.vacancyRepo.save(vacancy);
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
					this.vacancyRepo.delete(vacancy);
				}
				
				selectedArea.mergeWithExistingArea(existingArea);
				this.areaService.save(existingArea);
			}
		}
		
		redirectAttributes.addFlashAttribute("message", this.messages.get("messages.area.updated"));
	    redirectAttributes.addFlashAttribute("alertClass", "alert-success");
		return "redirect:/areas";
	}
	
	/*
	 * An area should not be deleted if its vacancies have employees associated to it. Otherwise we disable it
	 */
	@PreAuthorize("hasAuthority('" + Constants.DELETE_AREAS_PRIVILEGE + "'")
	@PostMapping("/delete/{id}")
	public String deleteArea(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) throws GenericException {
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
					this.vacancyRepo.save(vacancy);
				}
			}
		    
		    foundArea.setName("disabled--" + System.currentTimeMillis() + "--" + foundArea.getName());
		    foundArea.setEnabled(false);
		    this.areaService.save(foundArea);
		}
		
		return "redirect:/areas";
	}
}
