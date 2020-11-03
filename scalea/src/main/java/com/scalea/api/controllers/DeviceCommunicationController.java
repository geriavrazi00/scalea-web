package com.scalea.api.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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

//	@GetMapping("/recent")
//	public Iterable<Taco> recentTacos() {
//		PageRequest page = PageRequest.of(0, 12, Sort.by("createdAt").descending());
//		return tacoRepo.findAll(page).getContent();
//	}
	
	@GetMapping
	public ResponseEntity<Object> receiveData(@RequestParam("weight") Double weight) {
		if (weight != null && weight > 0) {
			String confirmationMessage = "Weight received! It is " + weight + " kg!!!";
			System.out.println(confirmationMessage);
			return new ResponseEntity<>(confirmationMessage, HttpStatus.OK);
		}
		
		String errorMessage = "Wrong format of the weight.";
		return new ResponseEntity<>(errorMessage, HttpStatus.BAD_REQUEST);
	}

}
