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
import com.scalea.entities.Employee;
import com.scalea.entities.Vacancy;
import com.scalea.exceptions.GenericException;
import com.scalea.repositories.EmployeeRepository;
import com.scalea.repositories.VacancyRepository;
import com.scalea.utils.Constants;

@Controller
@RequestMapping("/employees")
public class EmployeeController {
	
	private EmployeeRepository employeeRepo;
	private VacancyRepository vacancyRepo;
	private Logger log;
	private Messages messages;
	
	@Autowired
	public EmployeeController(EmployeeRepository employeeRepo, VacancyRepository vacancyRepo, Messages messages) {
		this.employeeRepo = employeeRepo;
		this.vacancyRepo = vacancyRepo;
		this.log = LoggerFactory.getLogger(RoleController.class);
		this.messages = messages;
	}
	
	@PreAuthorize("hasAnyAuthority('" + Constants.VIEW_EMPLOYEES_PRIVILEGE + "', '" + Constants.UPSERT_EMPLOYEES_PRIVILEGE + "', '" + Constants.DELETE_EMPLOYEES_PRIVILEGE + "')")
	@GetMapping
	public String allEmployees(Model model) {
		log.info("Method allEmployees()");
		
		Iterable<Employee> employees = employeeRepo.findAll();
		model.addAttribute("employees", employees);
		return "private/employees/employeelist";
	}
	
	@PreAuthorize("hasAuthority('" + Constants.UPSERT_EMPLOYEES_PRIVILEGE + "'")
	@GetMapping("/create")
	public String newEmployee(Model model) {
		log.info("Method newEmployee()");
		
		Iterable<Vacancy> vacancies = vacancyRepo.findUnassociatedVacancies();
		
		model.addAttribute("employee", new Employee());
		model.addAttribute("vacancies", vacancies);
		return "private/employees/createemployee";
	}
	
	@PreAuthorize("hasAuthority('" + Constants.UPSERT_EMPLOYEES_PRIVILEGE + "'")
	@PostMapping("/create")
	public String createEmployee(@Valid Employee employee, Errors errors, Model model, RedirectAttributes redirectAttributes) {
		log.info("Method createEmployee()");
		
		if (errors.hasErrors()) {
			Iterable<Vacancy> vacancies = vacancyRepo.findUnassociatedVacancies();
			model.addAttribute("vacancies", vacancies);
			return "private/employees/createemployee";
		}
		
		if (employeeRepo.existsByPersonalNumber(employee.getPersonalNumber())) {
			Iterable<Vacancy> vacancies = vacancyRepo.findUnassociatedVacancies();
			model.addAttribute("vacancies", vacancies);
			model.addAttribute("message", this.messages.get("messages.employee.exists", employee.getPersonalNumber()));
			model.addAttribute("alertClass", "alert-danger");
			return "private/employees/createemployee";
		}
		
		employee = this.employeeRepo.save(employee);
		
		if (employee.getVacancy() != null) {
			Vacancy vacancy = employee.getVacancy();
			vacancy.setEmployee(employee);
			this.vacancyRepo.save(vacancy);
		}
		
		redirectAttributes.addFlashAttribute("message", this.messages.get("messages.employee.created"));
	    redirectAttributes.addFlashAttribute("alertClass", "alert-success");
		return "redirect:/employees";
	}
	
	@PreAuthorize("hasAuthority('" + Constants.UPSERT_EMPLOYEES_PRIVILEGE + "'")
	@GetMapping("edit/{id}")
	public String editEmployee(@PathVariable("id") Long id, Model model) throws Exception {
		log.info("Method editEmployee()");
		
		Optional<Employee> employee = employeeRepo.findById(id);
		if (!employee.isPresent()) throw new GenericException(messages.get("messages.employee.not.found"));
		Iterable<Vacancy> vacancies = vacancyRepo.findUnassociatedVacancies();
		
		model.addAttribute("employee", employee.get());
		model.addAttribute("vacancies", vacancies);
		return "private/employees/editemployee";
	}
	
	@PreAuthorize("hasAuthority('" + Constants.UPSERT_EMPLOYEES_PRIVILEGE + "'")
	@PostMapping("/edit/{id}")
	public String updateEmployee(@Valid Employee employee, Errors errors, Model model, RedirectAttributes redirectAttributes) throws GenericException {
		log.info("Method updateEmployee()");
		
		if (errors.hasErrors()) {
			model.addAttribute("employee", employee);
			return "private/employees/editemployee";
		}
		
		redirectAttributes.addFlashAttribute("message", this.messages.get("messages.employee.updated"));
	    redirectAttributes.addFlashAttribute("alertClass", "alert-success");
	    return "redirect:/employees";
	}
	
	@PreAuthorize("hasAuthority('" + Constants.DELETE_EMPLOYEES_PRIVILEGE + "'")
	@PostMapping("/delete/{id}")
	public String deleteEmployee(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) throws GenericException {
		log.info("Method deleteEmployee()");
		return "redirect:/areas";
	}
}
