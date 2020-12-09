package com.scalea.controllers;

import java.sql.SQLException;
import java.util.Optional;
import java.util.UUID;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
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
import com.scalea.entities.Privilege;
import com.scalea.entities.Role;
import com.scalea.entities.Vacancy;
import com.scalea.enums.ApplicationRoles;
import com.scalea.exceptions.GenericException;
import com.scalea.exceptions.RoleNotFoundException;
import com.scalea.exceptions.UniqueRoleNameViolationException;
import com.scalea.exceptions.UniqueUserUsernameViolationException;
import com.scalea.exceptions.UserNotFoundException;
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
	public String createArea(@Valid Area area, Errors errors, Model model, RedirectAttributes redirectAttributes) throws UniqueUserUsernameViolationException {
		log.info("Method createUser()");
		
		if (errors.hasErrors()) {
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
	
	@PreAuthorize("hasAuthority('" + Constants.UPSERT_ROLES_PRIVILEGE + "'")
	@PostMapping("/edit/{id}")
	public String updateArea(@Valid Area area, Errors errors, Model model, RedirectAttributes redirectAttributes) throws UniqueRoleNameViolationException {
		log.info("Method updateArea()");
		
		if (errors.hasErrors()) {
			model.addAttribute("area", area);
			return "private/areas/editarea";
		}
		
		this.areaRepo.save(area);
		
		redirectAttributes.addFlashAttribute("message", this.messages.get("messages.area.updated"));
	    redirectAttributes.addFlashAttribute("alertClass", "alert-success");
		return "redirect:/areas";

	}
	
	/*
	 * An area should not be deleted if its vacancies have employees associated to it.
	 */
	@PreAuthorize("hasAuthority('" + Constants.DELETE_AREAS_PRIVILEGE + "'")
	@PostMapping("/delete/{id}")
	public String deleteArea(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) throws UserNotFoundException {
		log.info("Method deleteUser()");
		
		Optional<Area> area = areaRepo.findById(id);
		
		if (!area.isPresent()) throw new UserNotFoundException(messages.get("messages.area.not.found"));
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
