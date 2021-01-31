package com.scalea.controllers;

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
import com.scalea.entities.Employee;
import com.scalea.entities.Vacancy;
import com.scalea.exceptions.GenericException;
import com.scalea.models.dto.EmployeeDTO;
import com.scalea.services.EmployeeService;
import com.scalea.services.VacancyService;
import com.scalea.utils.Constants;
import com.scalea.utils.Utils;

@Controller
@RequestMapping("/employees")
public class EmployeeController {
	
	private EmployeeService employeeService;
	private VacancyService vacancyService;
	private Logger log;
	private Messages messages;
	
	private static final int DEFAULT_PAGE = 1;
	private static final int DEFAULT_SIZE = 7;
	
	@Autowired
	public EmployeeController(EmployeeService employeeService, VacancyService vacancyService, Messages messages) {
		this.employeeService = employeeService;
		this.vacancyService = vacancyService;
		this.log = LoggerFactory.getLogger(EmployeeController.class);
		this.messages = messages;
	}
	
	@PreAuthorize("hasAnyAuthority('" + Constants.VIEW_EMPLOYEES_PRIVILEGE + "', '" + Constants.UPSERT_EMPLOYEES_PRIVILEGE + "', '" + Constants.DELETE_EMPLOYEES_PRIVILEGE + "')")
	@GetMapping
	public String allEmployees(Model model, @RequestParam("page") Optional<Integer> page) {
		log.info("Method allEmployees()");
		
		int currentPage = page.orElse(DEFAULT_PAGE);
		
		// We only show the enabled employees at the moment
		Page<Employee> employees = employeeService.findByEnabledOrderByFirstName(true, PageRequest.of(currentPage - 1, DEFAULT_SIZE));
		Iterable<Vacancy> vacancies = vacancyService.findUnassociatedVacancies();
		
		if (model.getAttribute("employee") == null) model.addAttribute("employee", new Employee());
		if (model.getAttribute("employeeDTO") == null) model.addAttribute("employeeDTO", new EmployeeDTO());
		model.addAttribute("employees", employees);
		model.addAttribute("vacancies", vacancies);
		model.addAttribute("pageNumbers", Utils.getPageNumbersList(employees.getTotalPages()));
		return "private/employees/employeelist";
	}
	
	@PreAuthorize("hasAnyAuthority('" + Constants.UPSERT_EMPLOYEES_PRIVILEGE + "')")
	@GetMapping("/{id}")
	public @ResponseBody EmployeeDTO getEmployee(@PathVariable("id") Long id) throws GenericException {
		log.info("Method getEmployee()");
		
		Optional<Employee> employee = employeeService.findById(id);
		if (!employee.isPresent()) throw new GenericException(messages.get("messages.employee.not.found"));
		
		EmployeeDTO newEmployee = new EmployeeDTO();
		newEmployee.toDTO(employee.get());
		return newEmployee;
	}
	
	@PreAuthorize("hasAuthority('" + Constants.UPSERT_EMPLOYEES_PRIVILEGE + "'")
	@GetMapping("/create")
	public String newEmployee(Model model) {
		log.info("Method newEmployee()");
		
		Iterable<Vacancy> vacancies = vacancyService.findUnassociatedVacancies();
		
		model.addAttribute("employee", new Employee());
		model.addAttribute("vacancies", vacancies);
		return "private/employees/createemployee";
	}
	
	@PreAuthorize("hasAuthority('" + Constants.UPSERT_EMPLOYEES_PRIVILEGE + "'")
	@PostMapping("/create")
	public String createEmployee(@Valid Employee employee, Errors errors, Model model, RedirectAttributes redirectAttributes, 
		@RequestParam("page") Optional<Integer> page) {
		log.info("Method createEmployee()");
		
		if (errors.hasErrors()) {
			return this.allEmployees(model, page);
		}
		
		if (employeeService.existsByPersonalNumber(employee.getPersonalNumber())) {
			model.addAttribute("message", this.messages.get("messages.employee.exists", employee.getPersonalNumber()));
			model.addAttribute("alertClass", "alert-danger");
			return this.allEmployees(model, page);
		}
		
		employee = this.employeeService.save(employee);
		
		if (employee.getVacancy() != null) {
			Vacancy vacancy = employee.getVacancy();
			vacancy.setEmployee(employee);
			this.vacancyService.save(vacancy);
		}
		
		redirectAttributes.addFlashAttribute("message", this.messages.get("messages.employee.created"));
	    redirectAttributes.addFlashAttribute("alertClass", "alert-success");
		return "redirect:/employees";
	}
	
	@PreAuthorize("hasAuthority('" + Constants.UPSERT_EMPLOYEES_PRIVILEGE + "'")
	@PostMapping("/update")
	public String updateEmployee(@Valid EmployeeDTO employee, Errors errors, Model model, RedirectAttributes redirectAttributes, 
			@RequestParam("page") Optional<Integer> page) throws GenericException {
		log.info("Method updateEmployee()");
		
		if (errors.hasErrors()) {
			return this.allEmployees(model, page);
		}
		
		Optional<Employee> existingOptionalEmployee = employeeService.findById(employee.getId());
		if (!existingOptionalEmployee.isPresent()) throw new GenericException(messages.get("messages.employee.not.found"));
		Employee existingEmployee = existingOptionalEmployee.get();
		
		// Checking if the modified personal number of the employee is equal to the actual one. If it is, we check if there is already an employee with the chosen personal number in the system
		if (!existingEmployee.getPersonalNumber().equals(employee.getPersonalNumber())) {
			if (employeeService.existsByPersonalNumber(employee.getPersonalNumber())) {
				model.addAttribute("message", this.messages.get("messages.employee.exists", employee.getPersonalNumber()));
				model.addAttribute("alertClass", "alert-danger");
				return this.allEmployees(model, page);
			}
		}
		
		employee.mergeWithExistingEmployee(existingEmployee);
		
		if (employee.getVacancyId() != null) {
			Optional<Vacancy> optionalVacancy = vacancyService.findById(employee.getVacancyId());
			
			if (optionalVacancy.isPresent()) {
				// We first check if the vacancy and employee are still not associated with anything. Otherwise we throw an exception
				Vacancy vacancy = optionalVacancy.get();
				
				if (vacancy.getEmployee() != null) throw new GenericException(messages.get("messages.vacancy.occupied"));
				if (existingEmployee.getVacancy() != null) throw new GenericException(messages.get("messages.employee.occupied"));
				
				vacancy.setEmployee(existingEmployee);
				this.vacancyService.save(vacancy);
			}
		}
		
		employeeService.save(existingEmployee);
		
		redirectAttributes.addFlashAttribute("message", this.messages.get("messages.employee.updated"));
	    redirectAttributes.addFlashAttribute("alertClass", "alert-success");
	    return "redirect:/employees";
	}
	
	@PreAuthorize("hasAuthority('" + Constants.UPSERT_EMPLOYEES_PRIVILEGE + "'")
	@PostMapping("/detach/{id}")
	public String detachEmployee(@PathVariable("id") Long id, RedirectAttributes redirectAttributes, @RequestParam("page") Optional<Integer> page) throws GenericException {
		log.info("Method detachEmployee()");
		
		Optional<Employee> optionalEmployee = employeeService.findById(id);
		if (!optionalEmployee.isPresent()) throw new GenericException(messages.get("messages.employee.not.found"));
		Employee employee = optionalEmployee.get();
		
		/*
		 * To detach an employee from its vacancy, we just detach its vacancy if there is one
		 */
		if (employee.getVacancy() != null) this.detachEmployeeFromVacancy(employee);
		employee.setVacancy(null);
		employeeService.save(employee);
		
		redirectAttributes.addFlashAttribute("message", this.messages.get("messages.employee.detached"));
	    redirectAttributes.addFlashAttribute("alertClass", "alert-success");
		
		return "redirect:/employees";
	}
	
	@PreAuthorize("hasAuthority('" + Constants.DELETE_EMPLOYEES_PRIVILEGE + "'")
	@PostMapping("/delete/{id}")
	public String deleteEmployee(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) throws GenericException {
		log.info("Method deleteEmployee()");
		
		Optional<Employee> optionalEmployee = employeeService.findById(id);
		if (!optionalEmployee.isPresent()) throw new GenericException(messages.get("messages.employee.not.found"));
		Employee employee = optionalEmployee.get();
		
		/*
		 * To delete an employee, we just detach its vacancy if there is one and set the enabled employee to false
		 */
		if (employee.getVacancy() != null) this.detachEmployeeFromVacancy(employee);
		
		employee.setVacancy(null);
		employee.setEnabled(false);
		employeeService.save(employee);
		
		redirectAttributes.addFlashAttribute("message", this.messages.get("messages.employee.deleted"));
	    redirectAttributes.addFlashAttribute("alertClass", "alert-success");
		
		return "redirect:/employees";
	}
	
//	private void attachEmployeeToVacancy(Employee employee) {
//		Vacancy vacancy = employee.getVacancy();
//		vacancy.setEmployee(employee);
//		this.vacancyRepo.save(vacancy);
//	}
	
	private void detachEmployeeFromVacancy(Employee employee) {
		Vacancy vacancy = employee.getVacancy();
		vacancy.setEmployee(null);
		this.vacancyService.save(vacancy);
	}
}
