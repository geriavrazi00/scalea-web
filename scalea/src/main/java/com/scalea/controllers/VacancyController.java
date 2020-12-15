package com.scalea.controllers;

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
import com.scalea.entities.Vacancy;
import com.scalea.exceptions.GenericException;
import com.scalea.repositories.VacancyRepository;
import com.scalea.utils.Constants;

@Controller
@RequestMapping("/vacancies")
public class VacancyController {
	
	private VacancyRepository vacancyRepo;
	private Logger log;
	private Messages messages;
	
	@Autowired
	public VacancyController(VacancyRepository vacancyRepo, Messages messages) {
		this.vacancyRepo = vacancyRepo;
		this.log = LoggerFactory.getLogger(VacancyController.class);
		this.messages = messages;
	}
	
	@PreAuthorize("hasAnyAuthority('" + Constants.VIEW_VACANCIES_PRIVILEGE + "', '" + Constants.UPSERT_VACANCIES_PRIVILEGE + "', '" + Constants.DELETE_VACANCIES_PRIVILEGE + "')")
	@GetMapping
	public String allVacancies(Model model) {
		log.info("Method allVacancies()");
		return "private/vacancies/vacancylist";
	}
	
	@PreAuthorize("hasAuthority('" + Constants.UPSERT_VACANCIES_PRIVILEGE + "'")
	@GetMapping("/create")
	public String newVacancy(Model model) {
		log.info("Method newVacancy()");
		return "private/vacancies/createvacancy";
	}
	
	@PreAuthorize("hasAuthority('" + Constants.UPSERT_VACANCIES_PRIVILEGE + "'")
	@PostMapping("/create")
	public String createVacancy(@Valid Vacancy vacancy, Errors errors, Model model, RedirectAttributes redirectAttributes) {
		log.info("Method createVacancy()");
		return "redirect:/vacancies";
	}
	
	@PreAuthorize("hasAuthority('" + Constants.UPSERT_VACANCIES_PRIVILEGE + "'")
	@GetMapping("edit/{id}")
	public String editVacancy(@PathVariable("id") Long id, Model model) throws Exception {
		log.info("Method editVacancy()");
		return "private/vacancies/editvacancy";
	}
	
	@PreAuthorize("hasAuthority('" + Constants.UPSERT_VACANCIES_PRIVILEGE + "'")
	@PostMapping("/edit/{id}")
	public String updateVacancy(@Valid Vacancy vacancy, Errors errors, Model model, RedirectAttributes redirectAttributes) throws GenericException {
		log.info("Method updateVacancy()");
	    return "redirect:/vacancies";
	}
	
	@PreAuthorize("hasAuthority('" + Constants.DELETE_VACANCIES_PRIVILEGE + "'")
	@PostMapping("/delete/{id}")
	public String deleteVacancy(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) throws GenericException {
		log.info("Method deleteVacancy()");
		return "redirect:/vacancies";
	}

}
