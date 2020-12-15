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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.scalea.configurations.Messages;
import com.scalea.entities.Area;
import com.scalea.entities.Employee;
import com.scalea.entities.Vacancy;
import com.scalea.exceptions.GenericException;
import com.scalea.repositories.AreaRepository;
import com.scalea.repositories.EmployeeRepository;
import com.scalea.repositories.VacancyRepository;
import com.scalea.utils.Constants;
import com.scalea.utils.Utils;

@Controller
@RequestMapping("/vacancies")
public class VacancyController {
	
	private VacancyRepository vacancyRepo;
	private EmployeeRepository employeeRepo;
	private AreaRepository areaRepo;
	private Logger log;
	private Messages messages;
	
	@Autowired
	public VacancyController(VacancyRepository vacancyRepo, EmployeeRepository employeeRepo, AreaRepository areaRepo, Messages messages) {
		this.vacancyRepo = vacancyRepo;
		this.employeeRepo = employeeRepo;
		this.areaRepo = areaRepo;
		this.log = LoggerFactory.getLogger(VacancyController.class);
		this.messages = messages;
	}
	
	@PreAuthorize("hasAnyAuthority('" + Constants.VIEW_VACANCIES_PRIVILEGE + "', '" + Constants.UPSERT_VACANCIES_PRIVILEGE + "', '" + Constants.DELETE_VACANCIES_PRIVILEGE + "')")
	@GetMapping
	public String allVacancies(Model model) {
		log.info("Method allVacancies()");
		Iterable<Vacancy> vacancies = vacancyRepo.findByEnabled(true);
		
		model.addAttribute("vacancies", vacancies);
		return "private/vacancies/vacancylist";
	}
	
	@PreAuthorize("hasAuthority('" + Constants.UPSERT_VACANCIES_PRIVILEGE + "'")
	@GetMapping("/create")
	public String newVacancy(Model model) {
		log.info("Method newVacancy()");
		
		Iterable<Employee> employees = employeeRepo.findUnassociatedEmployees();
		Iterable<Area> areas = areaRepo.findAll();
		
		model.addAttribute("employees", employees);
		model.addAttribute("areas", areas);
		model.addAttribute("vacancy", new Vacancy());
		return "private/vacancies/createvacancy";
	}
	
	@PreAuthorize("hasAuthority('" + Constants.UPSERT_VACANCIES_PRIVILEGE + "'")
	@PostMapping("/create")
	public String createVacancy(@Valid Vacancy vacancy, Errors errors, Model model, RedirectAttributes redirectAttributes) {
		log.info("Method createVacancy()");
		
		if (errors.hasErrors()) {
			Iterable<Employee> employees = employeeRepo.findUnassociatedEmployees();
			Iterable<Area> areas = areaRepo.findAll();
			
			model.addAttribute("areas", areas);
			model.addAttribute("employees", employees);
			return "private/vacancies/createvacancy";
		}
		
		// We get the max vacancy number in the area and increase it by 1
		int newVacancyNumber = vacancyRepo.findMaxVacancyNumberOfArea(vacancy.getArea()) + 1;
		
		vacancy.setNumber(newVacancyNumber);
		vacancy.setUuid(Utils.generateUniqueVacancyCodes());
		this.vacancyRepo.save(vacancy);
		
		// Since we're adding a vacancy to an area, the capacity of the area should increase by 1
		Area area = vacancy.getArea();
		area.setCapacity(area.getCapacity() + 1);
		this.areaRepo.save(area);
		
		redirectAttributes.addFlashAttribute("message", this.messages.get("messages.vacancy.created"));
	    redirectAttributes.addFlashAttribute("alertClass", "alert-success");
		
		return "redirect:/vacancies";
	}
	
	@PreAuthorize("hasAuthority('" + Constants.UPSERT_VACANCIES_PRIVILEGE + "'")
	@PostMapping("/detach/{id}")
	public String detachVacancy(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) throws GenericException {
		log.info("Method detachVacancy()");
		
		Optional<Vacancy> optionalVacancy = vacancyRepo.findById(id);
		if (!optionalVacancy.isPresent()) throw new GenericException(messages.get("messages.vacancy.not.found"));
		Vacancy vacancy = optionalVacancy.get();
		
		// To delete a vacancy, we disable it, remove its associated employee and set its number to 0 so it doesn't interfere with future numbers.
		vacancy.setEmployee(null);
		this.vacancyRepo.save(vacancy);
		
		redirectAttributes.addFlashAttribute("message", this.messages.get("messages.vacancy.detached"));
	    redirectAttributes.addFlashAttribute("alertClass", "alert-success");
		
		return "redirect:/vacancies";
	}
	
	@PreAuthorize("hasAuthority('" + Constants.DELETE_VACANCIES_PRIVILEGE + "'")
	@PostMapping("/delete/{id}")
	public String deleteVacancy(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) throws GenericException {
		log.info("Method deleteVacancy()");
		
		Optional<Vacancy> optionalVacancy = vacancyRepo.findById(id);
		if (!optionalVacancy.isPresent()) throw new GenericException(messages.get("messages.vacancy.not.found"));
		Vacancy vacancy = optionalVacancy.get();
		
		// To delete a vacancy, we disable it, remove its associated employee and set its number to 0 so it doesn't interfere with future numbers.
		vacancy.setEnabled(false);
		vacancy.setEmployee(null);
		vacancy.setNumber(0);
		this.vacancyRepo.save(vacancy);
		
		// Since we're disabling a vacancy to an area, the capacity of the area should decrease by 1
		Area area = vacancy.getArea();
		area.setCapacity(area.getCapacity() - 1);
		this.areaRepo.save(area);
		
		redirectAttributes.addFlashAttribute("message", this.messages.get("messages.vacancy.deleted"));
	    redirectAttributes.addFlashAttribute("alertClass", "alert-success");
		
		return "redirect:/vacancies";
	}
}
