package com.scalea.controllers;

import java.security.Principal;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.scalea.configurations.Messages;
import com.scalea.entities.User;
import com.scalea.exceptions.GenericException;
import com.scalea.models.dto.ChangePasswordDTO;
import com.scalea.models.dto.ProfileDTO;
import com.scalea.services.UserService;
import com.scalea.utils.Constants;

@Controller
@RequestMapping("/profile")
public class ProfileController {

	private Logger log;
	private Messages messages;
	private UserService userService;
	private PasswordEncoder passwordEncoder;
	
	@Autowired
	public ProfileController(Messages messages, UserService userService, PasswordEncoder passwordEncoder) {
		this.log = LoggerFactory.getLogger(ProfileController.class);
		this.messages = messages;
		this.userService = userService;
		this.passwordEncoder = passwordEncoder;
	}
	
	@PreAuthorize("hasAnyAuthority('" + Constants.VIEW_PROFILE_PRIVILEGE + "', '" + Constants.UPDATE_PROFILE_PRIVILEGE + "')")
	@GetMapping
	public String showProfile(Model model, Principal principal) {
		log.info("Method showProfile()");
		
		User user = userService.findByUsername(principal.getName());
		ProfileDTO profile = new ProfileDTO();
		profile.toDTO(user);
		
		if (!model.containsAttribute("profileDTO")) model.addAttribute("profileDTO", profile);
		if (!model.containsAttribute("changePasswordDTO")) model.addAttribute("changePasswordDTO", new ChangePasswordDTO());
		return "private/administration/profile";
	}
	
	@PreAuthorize("hasAnyAuthority('" + Constants.UPDATE_PROFILE_PRIVILEGE + "')")
	@PostMapping
	public String updateProfile(@Valid ProfileDTO profile, Errors errors, Model model, Principal principal, RedirectAttributes redirectAttributes) throws GenericException {
		log.info("Method updateProfile()");
		
		if (errors.hasErrors()) {
			return this.showProfile(model, principal);
		}
		
		User user = userService.findByUsername(principal.getName());
		profile.toUser(user);
		userService.save(user);
		
		redirectAttributes.addFlashAttribute("message", this.messages.get("messages.profile.updated"));
	    redirectAttributes.addFlashAttribute("alertClass", "alert-success");
		return "redirect:/profile";
	}
	
	@PreAuthorize("hasAnyAuthority('" + Constants.UPDATE_PROFILE_PRIVILEGE + "')")
	@PostMapping("/password")
	public String updatePassword(@Valid ChangePasswordDTO passwordDTO, Errors errors, Model model, Principal principal, RedirectAttributes redirectAttributes) throws GenericException {
		log.info("Method updatePassword()");
		
		if (errors.hasErrors()) {
			return this.showProfile(model, principal);
		}
		
		User user = userService.findByUsername(principal.getName());
		user.setPassword(passwordEncoder.encode(passwordDTO.getPassword()));
		userService.save(user);
		
		redirectAttributes.addFlashAttribute("message", this.messages.get("messages.user.password.updated"));
	    redirectAttributes.addFlashAttribute("alertClass", "alert-success");
		return "redirect:/profile";
	}
}
