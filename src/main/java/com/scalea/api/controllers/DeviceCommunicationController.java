package com.scalea.api.controllers;

import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.scalea.controllers.ProcessController;
import com.scalea.entities.Activity;
import com.scalea.entities.Area;
import com.scalea.entities.Process;
import com.scalea.entities.Vacancy;
import com.scalea.enums.ProcessStatus;
import com.scalea.models.dto.ActivityDTO;
import com.scalea.repositories.ActivityRepository;
import com.scalea.repositories.AreaRepository;
import com.scalea.repositories.ProcessRepository;
import com.scalea.repositories.VacancyRepository;

import javassist.NotFoundException;

/* The @RestController annotation serves two purposes. First, it’s a stereotype annotation like @Controller and @Service that marks a class for 
 * discovery by component scanning. But most relevant to the discussion of REST, the @RestController annotation tells Spring that all handler 
 * methods in the controller should have their return value written directly to the body of the response, rather than being carried in the model to 
 * a view for rendering.
 * 
 * The @RequestMapping annotation at the class level works with the @GetMapping annotation on the recentTacos() method to specify that the recentTacos() 
 * method is responsible for handling GET requests for /design/recent (which is exactly what your Angular code needs).
 * 
 * You’ll notice that the @RequestMapping annotation also sets a produces attribute. This specifies that any of the handler methods in 
 * DesignTacoController will only handle requests if the request’s Accept header includes “application/json”. Not only does this limit your API to only 
 * producing JSON results, it also allows for another controller (perhaps the DesignTacoController from chapter 2) to handle requests with the same paths, 
 * so long as those requests don’t require JSON output.
 * 
 * The other thing you may have noticed in listing 6.2 is that the class is annotated with @CrossOrigin. Because the Angular portion of the application 
 * will be running on a separate host and/or port from the API (at least for now), the web browser will prevent your Angular client from consuming the API. 
 * This restriction can be overcome by including CORS (Cross-Origin Resource Sharing) headers in the server responses. Spring makes it easy to apply 
 * CORS with the @CrossOrigin annotation. As applied here, @CrossOrigin allows clients from any domain to consume the API.
 */

@RestController
@RequestMapping(path = "/device", produces = "application/json")
@CrossOrigin(origins = "*")
public class DeviceCommunicationController {
	
	private AreaRepository areaRepository;
	private VacancyRepository vacancyRepo;
	private ActivityRepository activityRepo;
	private ProcessRepository processRepo;
	private Logger log;
	
	@Autowired
	public DeviceCommunicationController(VacancyRepository vacancyRepo, ActivityRepository activityRepo, ProcessRepository processRepo) {
		this.vacancyRepo = vacancyRepo;
		this.activityRepo = activityRepo;
		this.processRepo = processRepo;
		this.log = LoggerFactory.getLogger(ProcessController.class);
	}
	
	@PostMapping
	@Transactional(rollbackFor = Exception.class)
	public ResponseEntity<Object> receiveData(@RequestBody List<ActivityDTO> activities) throws Exception {
		if (activities != null && activities.size() > 0) {
			for (ActivityDTO activityDto: activities) {
				try {
					Optional<Area> optionalArea = areaRepository.findByEnabledIsTrueAndUuid(activityDto.getBarcode());
					
					if (optionalArea.isPresent()) {
						this.saveDataForArea(activityDto, optionalArea.get());
					} else {
						this.saveDataForEmployee(activityDto);
					}
					
				} catch(NotFoundException e) {
					log.error(e.getMessage());
					throw e;
				} catch(Exception e) {
					throw new Exception("Invalid input!");
				}
			}
		}
		
		String confirmationMessage = "Të dhënat u ruajtën me sukses!";
		log.info(confirmationMessage);
		return new ResponseEntity<>(confirmationMessage, HttpStatus.OK);	
	}
	
	private void saveDataForArea(ActivityDTO activityDto, Area area) throws NotFoundException {
		Optional<Process> optionalProcess = processRepo.findByStatusAndArea(ProcessStatus.STARTED.getStatus(), area);
		if (!optionalProcess.isPresent()) throw new NotFoundException("Asnjë proces aktiv për sallën " + area.getName() + "!");
		Process activeProcess = optionalProcess.get();
		
		double weight = Double.valueOf(activityDto.getWeight());
		Long timestamp = Long.valueOf(activityDto.getTime());
		
		Instant instant = Instant.ofEpochSecond(timestamp);
		Date date = Date.from(instant);
		
		// Use this control to prevent inserting the same record more than once. It should not be possible to have the same area, weight amount and timestamp twice in the db
		boolean alreadyInserted = activityRepo.existsByAreaAndWeightAndDate(area, weight, date);
		
		if (!alreadyInserted) {
			Activity activity = new Activity();
			activity.setVacancy(null);
			activity.setWeight(weight);
			activity.setProduct(activeProcess.getProduct());
			activity.setEmployee(null);
			activity.setDate(date);
			activity.setArea(area);
			activity.setUser(area.getUser());
			
			activityRepo.save(activity);
		}
	}
	
	private void saveDataForEmployee(ActivityDTO activityDto) throws NotFoundException {
		Optional<Vacancy> optionalVacancy = vacancyRepo.findByUuid(activityDto.getBarcode());
		if (!optionalVacancy.isPresent()) throw new NotFoundException("Vendi i punës nuk ekziston!");
		Vacancy vacancy = optionalVacancy.get();
		
		if (vacancy.getEmployee() == null) throw new NotFoundException("Vendi i punës me id " + vacancy.getId() + " nuk është shoqëruar me asnjë punonjës!");
		
		Area area = vacancy.getArea();
		Optional<Process> optionalProcess = processRepo.findByStatusAndArea(ProcessStatus.STARTED.getStatus(), area);
		if (!optionalProcess.isPresent()) throw new NotFoundException("Asnjë proces aktiv për vendin e punës me id " + vacancy.getId() + "!");
		Process activeProcess = optionalProcess.get();
		
		double weight = Double.valueOf(activityDto.getWeight());
		Long timestamp = Long.valueOf(activityDto.getTime());
		
		Instant instant = Instant.ofEpochSecond(timestamp);
		Date date = Date.from(instant);
		
		// Use this control to prevent inserting the same record more than once. It should not be possible to have the same vacancy, weight amount and timestamp twice in the db
		boolean alreadyInserted = activityRepo.existsByVacancyAndWeightAndDate(vacancy, weight, date);
		
		if (!alreadyInserted) {
			Activity activity = new Activity();
			activity.setVacancy(vacancy);
			activity.setWeight(weight);
			activity.setProduct(activeProcess.getProduct());
			activity.setEmployee(vacancy.getEmployee());
			activity.setDate(date);
			activity.setArea(area);
			activity.setUser(area.getUser());
			
			activityRepo.save(activity);
		}
	}
}
