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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.scalea.configurations.Messages;
import com.scalea.entities.Area;
import com.scalea.entities.Employee;
import com.scalea.entities.Process;
import com.scalea.entities.Vacancy;
import com.scalea.exceptions.GenericException;
import com.scalea.models.dto.AlterVacancyDTO;
import com.scalea.services.AreaService;
import com.scalea.services.EmployeeService;
import com.scalea.services.ProcessService;
import com.scalea.services.VacancyService;
import com.scalea.utils.Constants;
import com.scalea.utils.Utils;

@Controller
@RequestMapping("/areas/{id}/vacancies")
public class VacancyController {
	
	private VacancyService vacancyService;
	private EmployeeService employeeService;
	private AreaService areaService;
	private ProcessService processService;
	private Logger log;
	private Messages messages;
	
	private static final int DEFAULT_PAGE = 1;
	private static final int DEFAULT_SIZE = 7;
	
	@Autowired
	public VacancyController(VacancyService vacancyService, EmployeeService employeeService, AreaService areaService, ProcessService processService,
		Messages messages) {
		this.vacancyService = vacancyService;
		this.employeeService = employeeService;
		this.areaService = areaService;
		this.processService = processService;
		this.log = LoggerFactory.getLogger(VacancyController.class);
		this.messages = messages;
	}
	
	@PreAuthorize("hasAnyAuthority('" + Constants.VIEW_VACANCIES_PRIVILEGE + "', '" + Constants.UPSERT_VACANCIES_PRIVILEGE + "', '" + Constants.DELETE_VACANCIES_PRIVILEGE + "')")
	@GetMapping
	public String allVacancies(Model model, @PathVariable("id") Long id, @RequestParam("page") Optional<Integer> page, @RequestParam("size") Optional<Integer> size) throws GenericException {
		log.info("Method allVacancies()");
		
		int currentPage = page.orElse(DEFAULT_PAGE);
        int pageSize = size.orElse(DEFAULT_SIZE);
        
        Optional<Area> area = areaService.findById(id);
		if (!area.isPresent()) throw new GenericException(messages.get("messages.area.not.found"));
		area.get().calculateEmployeeNumber();
		
		Page<Vacancy> vacancies = vacancyService.findByAreaAndEnabled(area.get(), true, PageRequest.of(currentPage - 1, pageSize));
		Optional<Process> latestProcess = processService.findFirstByAreaOrderByStartedAtDesc(area.get());
		Iterable<Employee> employees = employeeService.findByVacancyIsNullAndEnabledIsTrue();
		
		model.addAttribute("area", area.get());
		model.addAttribute("vacancies", vacancies);
		model.addAttribute("process", latestProcess);
		model.addAttribute("employees", employees);
		
		if (model.getAttribute("alterVacancyDTO") == null) model.addAttribute("alterVacancyDTO", new AlterVacancyDTO());
		model.addAttribute("pageNumbers", Utils.getPageNumbersList(vacancies.getTotalPages()));
		return "private/areas/vacancies/vacancylist";
	}
	
	@PreAuthorize("hasAnyAuthority('" + Constants.UPSERT_VACANCIES_PRIVILEGE + "')")
	@PostMapping("/create")
	public String alterVacancies(Model model, @Valid AlterVacancyDTO alterVacancyDTO, Errors errors, @PathVariable("id") Long id, 
			@RequestParam("page") Optional<Integer> page, @RequestParam("size") Optional<Integer> size, RedirectAttributes redirectAttributes) throws GenericException {
		log.info("Method alterVacancies()");
		
		if (errors.hasErrors()) {
			return this.allVacancies(model, id, page, size);
		}
        
        Optional<Area> optionalArea = areaService.findById(id);
		if (!optionalArea.isPresent()) throw new GenericException(messages.get("messages.area.not.found"));
		Area area = optionalArea.get();
		
		int lastVacancyNumber = vacancyService.findMaxVacancyNumberOfArea(area) + 1;
		
		for (int i = 0; i < alterVacancyDTO.getCapacity(); i++) {
			Vacancy vacancy = new Vacancy();
			vacancy.setNumber(i + lastVacancyNumber);
			vacancy.setUuid(Utils.generateUniqueVacancyCodes());
			vacancy.setArea(area);
			
			this.vacancyService.save(vacancy);
		}
		
		area.setCapacity(area.getCapacity() + alterVacancyDTO.getCapacity());
		this.areaService.save(area);
		
		redirectAttributes.addFlashAttribute("message", this.messages.get("messages.vacancies.and.codes.created"));
	    redirectAttributes.addFlashAttribute("alertClass", "alert-success");
		return "redirect:/areas/" + id + "/vacancies" + paginationParameters(page, size);
	}
	
	@PreAuthorize("hasAuthority('" + Constants.UPSERT_VACANCIES_PRIVILEGE + "'")
	@PostMapping("/update")
	public String updateVacancy(@PathVariable("id") Long id, @RequestParam("edit-vacancy-id") Long vId, @RequestParam("employee") Long employeeId,
			@RequestParam("page") Optional<Integer> page, @RequestParam("size") Optional<Integer> size, RedirectAttributes redirectAttributes) throws GenericException {
		log.info("Method updateVacancy()");
		
		Optional<Area> optionalArea = areaService.findById(id);
		if (!optionalArea.isPresent()) throw new GenericException(messages.get("messages.area.not.found"));
		
		Optional<Vacancy> optionalVacancy = vacancyService.findById(vId);
		if (!optionalVacancy.isPresent()) throw new GenericException(messages.get("messages.vacancy.not.found"));
		
		if (!vacancyService.existsByIdAndArea(vId, optionalArea.get())) throw new GenericException(messages.get("messages.vacancy.not.found"));
		
		Optional<Employee> optionalEmployee = employeeService.findById(employeeId);
		
		if (optionalEmployee.isPresent()) {
			// We first check if the vacancy and employee are still not associated with anything. Otherwise we throw an exception
			Vacancy vacancy = optionalVacancy.get();
			Employee employee = optionalEmployee.get();
			
			if (vacancy.getEmployee() != null) throw new GenericException(messages.get("messages.vacancy.occupied"));
			if (employee.getVacancy() != null) throw new GenericException(messages.get("messages.employee.occupied"));
			
			vacancy.setEmployee(employee);
			this.vacancyService.save(vacancy);
		}
		
		redirectAttributes.addFlashAttribute("message", this.messages.get("messages.vacancy.updated"));
	    redirectAttributes.addFlashAttribute("alertClass", "alert-success");
	    return "redirect:/areas/" + id + "/vacancies" + paginationParameters(page, size);
	}
	
	@PreAuthorize("hasAuthority('" + Constants.UPSERT_VACANCIES_PRIVILEGE + "'")
	@PostMapping("/detach/{vId}")
	public String detachVacancy(@PathVariable("id") Long id, @PathVariable("vId") Long vId, @RequestParam("page") Optional<Integer> page, 
			@RequestParam("size") Optional<Integer> size, RedirectAttributes redirectAttributes) throws GenericException {
		log.info("Method detachVacancy()");
		
		Optional<Area> optionalArea = areaService.findById(id);
		if (!optionalArea.isPresent()) throw new GenericException(messages.get("messages.area.not.found"));
		
		Optional<Vacancy> optionalVacancy = vacancyService.findById(vId);
		if (!optionalVacancy.isPresent()) throw new GenericException(messages.get("messages.vacancy.not.found"));
		
		if (!vacancyService.existsByIdAndArea(vId, optionalArea.get())) throw new GenericException(messages.get("messages.vacancy.not.found"));
		
		Vacancy vacancy = optionalVacancy.get();
		
		// To detach an employee, we set it to null
		vacancy.setEmployee(null);
		this.vacancyService.save(vacancy);
		
		redirectAttributes.addFlashAttribute("message", this.messages.get("messages.vacancy.detached"));
	    redirectAttributes.addFlashAttribute("alertClass", "alert-success");
	    return "redirect:/areas/" + id + "/vacancies" + paginationParameters(page, size);
	}
	
	@PreAuthorize("hasAuthority('" + Constants.DELETE_VACANCIES_PRIVILEGE + "'")
	@PostMapping("/delete/{vId}")
	public String deleteVacancy(@PathVariable("id") Long id, @PathVariable("vId") Long vId, @RequestParam("page") Optional<Integer> page, 
			@RequestParam("size") Optional<Integer> size, RedirectAttributes redirectAttributes) throws GenericException {
		log.info("Method deleteVacancy()");
		
		Optional<Area> optionalArea = areaService.findById(id);
		if (!optionalArea.isPresent()) throw new GenericException(messages.get("messages.area.not.found"));
		
		Optional<Vacancy> optionalVacancy = vacancyService.findById(vId);
		if (!optionalVacancy.isPresent()) throw new GenericException(messages.get("messages.vacancy.not.found"));
		
		if (!vacancyService.existsByIdAndArea(vId, optionalArea.get())) throw new GenericException(messages.get("messages.vacancy.not.found"));
		
		Vacancy vacancy = optionalVacancy.get();
		
		// To delete a vacancy, we disable it, remove its associated employee and set its number to 0 so it doesn't interfere with future numbers.
		vacancy.setEnabled(false);
		vacancy.setEmployee(null);
		vacancy.setNumber(0);
		this.vacancyService.save(vacancy);
		
		// Since we're disabling a vacancy to an area, the capacity of the area should decrease by 1
		Area area = optionalArea.get();
		area.setCapacity(area.getCapacity() - 1);
		this.areaService.save(area);
		
		redirectAttributes.addFlashAttribute("message", this.messages.get("messages.vacancy.deleted"));
	    redirectAttributes.addFlashAttribute("alertClass", "alert-success");
	    return "redirect:/areas/" + id + "/vacancies" + paginationParameters(page, size);
	}
	
	private String paginationParameters(Optional<Integer> page, Optional<Integer> size) {
		return "?page=" + page.orElse(DEFAULT_PAGE) + "&size=" + size.orElse(DEFAULT_SIZE);
	}
}
