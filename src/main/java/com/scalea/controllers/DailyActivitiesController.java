package com.scalea.controllers;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.scalea.configurations.Messages;
import com.scalea.entities.Activity;
import com.scalea.entities.Area;
import com.scalea.entities.Employee;
import com.scalea.exceptions.GenericException;
import com.scalea.models.dto.DailyActivityDTO;
import com.scalea.models.dto.DailyActivityDetailDTO;
import com.scalea.services.ActivityService;
import com.scalea.services.AreaService;
import com.scalea.services.EmployeeService;
import com.scalea.utils.Constants;
import com.scalea.utils.ExcelUtil;
import com.scalea.utils.Utils;

@Controller
@RequestMapping("/daily-activities")
public class DailyActivitiesController {
	
	private AreaService areaService;
	private ActivityService activityService;
	private EmployeeService employeeService;
	private Logger log;
	private Messages messages;
	
	@Autowired
	public DailyActivitiesController(AreaService areaService, ActivityService activityService, EmployeeService employeeService, Messages messages) {
		this.areaService = areaService;
		this.activityService = activityService;
		this.employeeService = employeeService;
		this.log = LoggerFactory.getLogger(DailyActivitiesController.class);
		this.messages = messages;
	}
	
	@PreAuthorize("hasAnyAuthority('" + Constants.VIEW_DAILY_ACTIVITIES_PRIVILEGE + "')")
	@GetMapping
	public String allDailyActivities(Model model, @RequestParam("date") Optional<String> date, 
		@RequestParam("area") Optional<Long> area) throws ParseException {
		log.info("Method allDailyActivities()");
        
        List<Area> areas = (List<Area>) areaService.findByEnabled(true);
        
        date = date.isPresent() ? date : Optional.of(Utils.dateToDateString(new Date()));
        area = area.isPresent() ? area : ((areas != null && areas.size() > 0) ? Optional.of(areas.get(0).getId()) : Optional.empty());
        
        Date selectedDate = Utils.inputDateStringToDate(date.get());
        Optional<Area> selectedArea = area.isPresent() ? areaService.findById(area.get()) : Optional.empty();
        
        Iterable<DailyActivityDTO> activities = activityService.findActivityByAreaAndDate(selectedArea.get(), selectedDate);
        double totalWeight = 0;
        long totalWeighings = 0;
        
        for (DailyActivityDTO dailyActivityDTO : activities) {
        	totalWeight += dailyActivityDTO.getTotalWeight();
        	totalWeighings += dailyActivityDTO.getWeighings();
		}
        
        if (area.isPresent() && selectedArea.isEmpty()) {
        	model.addAttribute("message", this.messages.get("messages.area.selected.not.exists"));
        	model.addAttribute("alertClass", "alert-warning");
        }
        
        model.addAttribute("areas", areas);
        model.addAttribute("activities", activities);
        model.addAttribute("selectedArea", selectedArea);
        model.addAttribute("selectedDate", date);
        model.addAttribute("totalWeight", Utils.twoDecimalPlacesDouble(totalWeight));
        model.addAttribute("totalWeighings", totalWeighings);
		return "private/daily-activities/dailyactivitieslist";
	}
	
	@PreAuthorize("hasAnyAuthority('" + Constants.VIEW_DAILY_ACTIVITIES_PRIVILEGE + "')")
	@GetMapping("/{id}")
	public @ResponseBody Iterable<DailyActivityDetailDTO> getDailyActivityDetails(@PathVariable("id") Long employeeId, 
			@RequestParam("date") String dateString, @RequestParam("area") Long areaId) throws Exception {
		log.info("Method getDailyActivityDetails()");
		
		Optional<Area> area = areaService.findById(areaId);
		if (!area.isPresent()) throw new GenericException(messages.get("messages.area.not.found"));
		
		Optional<Employee> employee = employeeService.findById(employeeId);
		if (!employee.isPresent()) throw new GenericException(messages.get("messages.employee.not.found"));
		
		Date selectedDate = Utils.inputDateStringToDate(dateString);
		
		Iterable<DailyActivityDetailDTO> activityDetails = activityService.findActivityByAreaAndDateAndEmployee(area.get(), 
			selectedDate, employee.get());
		return activityDetails;
	}
	
	@PreAuthorize("hasAnyAuthority('" + Constants.DOWNLOAD_DAILY_ACTIVITIES_PRIVILEGE + "')")
	@PostMapping("/export")
	public ResponseEntity<byte[]> exportDailyActivities(@RequestParam("date") String dateString, @RequestParam("area") Long areaId) throws GenericException, IOException, ParseException {
		Optional<Area> area = areaService.findById(areaId);
		if (!area.isPresent()) throw new GenericException(messages.get("messages.area.not.found"));
		Date selectedDate = Utils.inputDateStringToDate(dateString);
		
		Iterable<Activity> activities = activityService.findAllActivityByAreaAndDate(area.get(), selectedDate);
		
		Workbook workbook = new XSSFWorkbook();
		Sheet sheet = workbook.createSheet(dateString);
		
		String[] headers = new String[] {
			messages.get("messages.name"),
			messages.get("messages.vacancy"),
			messages.get("messages.product"),
			messages.get("messages.weight"),
			messages.get("messages.date"),
			messages.get("messages.area"),
			messages.get("messages.supervisor")
		};
		ExcelUtil.addHeaders(workbook, sheet, 0, headers);
		
		List<Object[]> data = new ArrayList<>();
		
		for (Activity activity : activities) {
			Object[] object = new Object[] {
				activity.getEmployee().getFirstName() + " " + activity.getEmployee().getLastName(),
				activity.getVacancy().getNumber(), 
				activity.getProduct().getName(), 
				activity.getWeight(), 
				activity.getDate(), 
				activity.getArea().getName(), 
				activity.getUser() != null ? activity.getUser().getFirstName() + " " + activity.getUser().getLastName() : activity.getUser()
			};
			
			data.add(object);
		}
		
		ExcelUtil.addRows(sheet, 1, data);
		
		String fileName = this.messages.get("messages.daily.activities.export") + "_" + dateString 
			+ "_" + area.get().getName() + ".xlsx";
		fileName = fileName.replace(' ', '_').replace('-', '_');
		
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		try {
		    workbook.write(bos);
		} finally {
		    bos.close();
		    workbook.close();
		}
		
		HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set(HttpHeaders.CONTENT_TYPE, MediaType.parseMediaType("application/vnd.ms-excel").toString());
        httpHeaders.set("Content-Disposition", "attachment; filename=" + fileName);
        return ResponseEntity.ok().headers(httpHeaders).body(bos.toByteArray());
	}
}
