package com.scalea.controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.scalea.configurations.Messages;
import com.scalea.entities.Area;
import com.scalea.entities.Vacancy;
import com.scalea.exceptions.GenericException;
import com.scalea.repositories.AreaRepository;
import com.scalea.repositories.VacancyRepository;
import com.scalea.utils.Constants;

@Controller
@RequestMapping("/areas")
public class AreaController {

	private AreaRepository areaRepo;
	private VacancyRepository vacancyRepo;
	private Logger log;
	private Messages messages;
	
	@Autowired
	public AreaController(AreaRepository areaRepo, VacancyRepository vacancyRepo, Messages messages) {
		this.areaRepo = areaRepo;
		this.vacancyRepo = vacancyRepo;
		this.log = LoggerFactory.getLogger(RoleController.class);
		this.messages = messages;
	}
	
	@PreAuthorize("hasAnyAuthority('" + Constants.VIEW_AREAS_PRIVILEGE + "', '" + Constants.UPSERT_AREAS_PRIVILEGE + "', '" + Constants.DELETE_AREAS_PRIVILEGE + "')")
	@GetMapping
	public String allAreas(Model model) {
		log.info("Method allAreas()");
		
		Iterable<Area> areas = areaRepo.findAll();
		model.addAttribute("areas", areas);
		return "private/areas/arealist";
	}
	
	@PreAuthorize("hasAuthority('" + Constants.UPSERT_AREAS_PRIVILEGE + "'")
	@GetMapping("/create")
	public String newArea(Model model) {
		log.info("Method newArea()");
		
		model.addAttribute("area", new Area());
		return "private/areas/createarea";
	}
	
	@PreAuthorize("hasAuthority('" + Constants.UPSERT_AREAS_PRIVILEGE + "'")
	@PostMapping("/create")
	public String createArea(@Valid Area area, Errors errors, Model model, RedirectAttributes redirectAttributes) {
		log.info("Method createArea()");
		
		if (errors.hasErrors()) {
			return "private/areas/createarea";
		}
		
		if (areaRepo.existsByName(area.getName())) {
			model.addAttribute("message", this.messages.get("messages.area.exists", area.getName()));
			model.addAttribute("alertClass", "alert-danger");
		    return "private/areas/createarea";
		}
		
		area = this.areaRepo.save(area);
		
		for (int i = 0; i < area.getCapacity(); i++) {
			Vacancy vacancy = new Vacancy();
			vacancy.setNumber(i + 1);
			vacancy.setUuid(UUID.randomUUID().toString());
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
		
		Optional<Area> area = areaRepo.findById(id);
		if (!area.isPresent()) throw new GenericException(messages.get("messages.area.not.found"));
		
		model.addAttribute("area", area.get());
		return "private/areas/editarea";
	}
	
	@PreAuthorize("hasAuthority('" + Constants.UPSERT_AREAS_PRIVILEGE + "'")
	@PostMapping("/edit/{id}")
	public String updateArea(@Valid Area area, Errors errors, Model model, RedirectAttributes redirectAttributes) throws GenericException {
		log.info("Method updateArea()");
		
		if (errors.hasErrors()) {
			model.addAttribute("area", area);
			return "private/areas/editarea";
		}
		
		Optional<Area> existingOptionalArea = areaRepo.findById(area.getId());
		if (!existingOptionalArea.isPresent()) throw new GenericException(messages.get("messages.area.not.found"));
		Area existingArea = existingOptionalArea.get();
		
		// Checking if the modified name of the area is equal to the actual one. If it is, we check if there is already an area with the chosen name in the system
		if (!existingArea.getName().equals(area.getName())) {
			if (areaRepo.existsByName(area.getName())) {
				model.addAttribute("message", this.messages.get("messages.area.exists", area.getName()));
				model.addAttribute("alertClass", "alert-danger");
			    return "private/areas/editarea";
			}
		}
		
		/*
		 * Checking the capacity. It may remain the same, increase or decrease. If it increases or it is the same we should simply create the additional
		 * vacancies. If it decreases, we should check first if there are employees associated to the area. We do not decrease the capacity if there 
		 * are employees associated.
		*/
		if (area.getCapacity() >= existingArea.getCapacity()) {
			int newVacancies = area.getCapacity() - existingArea.getCapacity();
			int lastVacancyNumber = vacancyRepo.findMaxVacancyNumberOfArea(existingArea) + 1;
			
			for (int i = 0; i < newVacancies; i++) {
				Vacancy vacancy = new Vacancy();
				vacancy.setNumber(i + lastVacancyNumber);
				vacancy.setUuid(UUID.randomUUID().toString());
				vacancy.setArea(existingArea);
				
				this.vacancyRepo.save(vacancy);
			}
			
			this.areaRepo.save(area);
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
				int vacanciesToDelete = existingArea.getCapacity() - area.getCapacity();
				List<Vacancy> toDelete = new ArrayList<>();
				
				for (int i = existingArea.getVacancies().size() - 1; i >= 0; i--) {
					if (vacanciesToDelete == 0) break;
					
					Vacancy v = (Vacancy) existingArea.getVacancies().toArray()[i];
					toDelete.add(v);
					vacanciesToDelete--;
				}
				
				for (Vacancy vacancy: toDelete) {
					this.vacancyRepo.delete(vacancy);
				}
				
				this.areaRepo.save(area);
			}
		}
		
		redirectAttributes.addFlashAttribute("message", this.messages.get("messages.area.updated"));
	    redirectAttributes.addFlashAttribute("alertClass", "alert-success");
		return "redirect:/areas";
	}
	
	/*
	 * An area should not be deleted if its vacancies have employees associated to it.
	 */
	@PreAuthorize("hasAuthority('" + Constants.DELETE_AREAS_PRIVILEGE + "'")
	@PostMapping("/delete/{id}")
	public String deleteArea(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) throws GenericException {
		log.info("Method deleteArea()");
		
		Optional<Area> area = areaRepo.findById(id);
		
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
		    this.areaRepo.delete(foundArea);
		}
		
		return "redirect:/areas";
	}
}
