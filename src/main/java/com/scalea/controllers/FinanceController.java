package com.scalea.controllers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.scalea.configurations.Messages;
import com.scalea.enums.Months;
import com.scalea.exceptions.GenericException;
import com.scalea.models.dto.ActivityAggregatorDTO;
import com.scalea.models.dto.FinanceDTO;
import com.scalea.models.dto.MonthDTO;
import com.scalea.services.ActivityService;
import com.scalea.utils.Constants;
import com.scalea.utils.Utils;

@Controller
@RequestMapping("/finances")
public class FinanceController {
	private ActivityService activityService;
	private Logger log;
	private Messages messages;
	
	@Autowired
	public FinanceController(ActivityService activityService, Messages messages) {
		this.activityService = activityService;
		this.log = LoggerFactory.getLogger(EmployeeController.class);
		this.messages = messages;
	}
	
	@PreAuthorize("hasAnyAuthority('" + Constants.VIEW_FINANCIAL_ACTIVITIES_PRIVILEGE + "')")
	@GetMapping
	public String allFinancialActivities(Model model, @RequestParam("page") Optional<Integer> page, 
		@RequestParam("month") Optional<Integer> selectedMonth, @RequestParam("year") Optional<Integer> selectedYear) throws NumberFormatException, Exception {
		log.info("Method allFinancialActivities()");
		
		int currentPage = page.orElse(Constants.DEFAULT_PAGE);
		Date date = new Date();
		
		int month = selectedMonth.orElse(Utils.getMonthFromDate(date));
		int year = selectedYear.orElse(Utils.getYearFromDate(date));
		
		Page<ActivityAggregatorDTO> activities = activityService.findAllActivities(month, year, PageRequest.of(currentPage - 1, Constants.DEFAULT_SIZE));
		List<FinanceDTO> finances = activityService.findAllFinancialActivities(activities.getContent());
		Collections.sort(finances);
		
		// Setting the filters
		List<MonthDTO> months = new ArrayList<>();
		for (Months m: Months.values()) {
			MonthDTO monthDto = new MonthDTO(m.getNumber(), m.getLocaleNameByNumber(m.getNumber(), messages));
			months.add(monthDto);
		}
		
		Iterable<Integer> years = activityService.findYearsWithData();
		
		model.addAttribute("finances", finances);
		model.addAttribute("activities", activities);
		model.addAttribute("selectedMonth", month);
		model.addAttribute("selectedYear", year);
		model.addAttribute("months", months);
		model.addAttribute("years", years);
		model.addAttribute("pageNumbers", Utils.getPageNumbersList(activities.getTotalPages()));
		return "private/finances/financelist";
	}
	
	@PreAuthorize("hasAnyAuthority('" + Constants.UPSERT_AREAS_PRIVILEGE + "')")
	@GetMapping("/{id}")
	public @ResponseBody FinanceDTO getFinanceDTO(@PathVariable("id") Long id, @RequestParam("month") Integer month, @RequestParam("year") Integer year) throws NumberFormatException, Exception {
		log.info("Method getFinanceDTO()");
		
		Optional<ActivityAggregatorDTO> activityOptional = activityService.findActivityAggByIds(month, year, id);
		if (!activityOptional.isPresent()) throw new GenericException(messages.get("messages.activity.not.found"));
		
		List<ActivityAggregatorDTO> activities = new ArrayList<>();
		activities.add(activityOptional.get());
		List<FinanceDTO> finances = activityService.findAllFinancialActivities(activities);
		return finances.get(0);
	}
}
