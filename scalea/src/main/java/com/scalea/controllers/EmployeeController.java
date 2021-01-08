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
		this.log = LoggerFactory.getLogger(EmployeeController.class);
		this.messages = messages;
	}
	
	@PreAuthorize("hasAnyAuthority('" + Constants.VIEW_EMPLOYEES_PRIVILEGE + "', '" + Constants.UPSERT_EMPLOYEES_PRIVILEGE + "', '" + Constants.DELETE_EMPLOYEES_PRIVILEGE + "')")
	@GetMapping
	public String allEmployees(Model model) {
		log.info("Method allEmployees()");
		
		// We only show the enabled employees at the moment
		Iterable<Employee> employees = employeeRepo.findByEnabled(true);
		Iterable<Vacancy> vacancies = vacancyRepo.findUnassociatedVacancies();
		
		if (model.getAttribute("employee") == null) model.addAttribute("employee", new Employee());
		model.addAttribute("employees", employees);
		model.addAttribute("vacancies", vacancies);
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
			//Iterable<Vacancy> vacancies = vacancyRepo.findUnassociatedVacancies();
			//model.addAttribute("vacancies", vacancies);
			
			//this.allEmployees(model);
			return this.allEmployees(model);
		}
		
		if (employeeRepo.existsByPersonalNumber(employee.getPersonalNumber())) {
//			Iterable<Vacancy> vacancies = vacancyRepo.findUnassociatedVacancies();
//			model.addAttribute("vacancies", vacancies);
			model.addAttribute("message", this.messages.get("messages.employee.exists", employee.getPersonalNumber()));
			model.addAttribute("alertClass", "alert-danger");
			return this.allEmployees(model);
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
			Iterable<Vacancy> vacancies = vacancyRepo.findUnassociatedVacancies();
			
			model.addAttribute("employee", employee);
			model.addAttribute("vacancies", vacancies);
			return "private/employees/editemployee";
		}
		
		Optional<Employee> existingOptionalEmployee = employeeRepo.findById(employee.getId());
		if (!existingOptionalEmployee.isPresent()) throw new GenericException(messages.get("messages.employee.not.found"));
		Employee existingEmployee = existingOptionalEmployee.get();
		
		// Checking if the modified personal number of the employee is equal to the actual one. If it is, we check if there is already an employee with the chosen personal number in the system
		if (!existingEmployee.getPersonalNumber().equals(employee.getPersonalNumber())) {
			if (employeeRepo.existsByPersonalNumber(employee.getPersonalNumber())) {
				Iterable<Vacancy> vacancies = vacancyRepo.findUnassociatedVacancies();
				employee.setVacancy(existingEmployee.getVacancy()); // When the validation fails and a value for the vacancy was selected, the new value was displayed like it was saved to the employee. Setting it back to the old value, avoids all this
				
				model.addAttribute("message", this.messages.get("messages.employee.exists", employee.getPersonalNumber()));
				model.addAttribute("alertClass", "alert-danger");
				model.addAttribute("employee", employee);
				model.addAttribute("vacancies", vacancies);
				
			    return "private/employees/editemployee";
			}
		}
		
		// When the detach check box is checked, we detach the existing employee from the old vacancy. If a vacancy is selected we attach it the employee.
		if (employee.isDetach()) {
			this.detachEmployeeFromVacancy(existingEmployee);
			if (employee.getVacancy() != null) this.attachEmployeeToVacancy(employee);
		} else {
			if (employee.getVacancy() != null) this.attachEmployeeToVacancy(employee);
		}
		
		employee = employeeRepo.save(employee);
		
		redirectAttributes.addFlashAttribute("message", this.messages.get("messages.employee.updated"));
	    redirectAttributes.addFlashAttribute("alertClass", "alert-success");
	    return "redirect:/employees";
	}
	
	@PreAuthorize("hasAuthority('" + Constants.DELETE_EMPLOYEES_PRIVILEGE + "'")
	@PostMapping("/delete/{id}")
	public String deleteEmployee(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) throws GenericException {
		log.info("Method deleteEmployee()");
		
		Optional<Employee> optionalEmployee = employeeRepo.findById(id);
		if (!optionalEmployee.isPresent()) throw new GenericException(messages.get("messages.employee.not.found"));
		Employee employee = optionalEmployee.get();
		
		/*
		 * To delete an employee, we just detach its vacancy if there is one and set the enabled employee to false
		 */
		if (employee.getVacancy() != null) this.detachEmployeeFromVacancy(employee);
		
		employee.setVacancy(null);
		employee.setEnabled(false);
		employeeRepo.save(employee);
		
		redirectAttributes.addFlashAttribute("message", this.messages.get("messages.employee.deleted"));
	    redirectAttributes.addFlashAttribute("alertClass", "alert-success");
		
		return "redirect:/employees";
	}
	
	private void attachEmployeeToVacancy(Employee employee) {
		Vacancy vacancy = employee.getVacancy();
		vacancy.setEmployee(employee);
		this.vacancyRepo.save(vacancy);
	}
	
	private void detachEmployeeFromVacancy(Employee employee) {
		Vacancy vacancy = employee.getVacancy();
		vacancy.setEmployee(null);
		this.vacancyRepo.save(vacancy);
	}
}
