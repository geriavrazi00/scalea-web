package com.scalea.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.scalea.configurations.Messages;
import com.scalea.utils.Constants;

@Controller
@RequestMapping("/profile")
public class ProfileController {

	private Logger log;
	private Messages messages;
	
	@Autowired
	public ProfileController(Messages messages) {
		this.log = LoggerFactory.getLogger(ProfileController.class);
		this.messages = messages;
	}
	
	@PreAuthorize("hasAnyAuthority('" + Constants.VIEW_PROFILE_PRIVILEGE + "', '" + Constants.UPDATE_PROFILE_PRIVILEGE + "')")
	@GetMapping
	public String showProfile(Model model) {
		log.info("Method showProfile()");
		return "private/administration/profile";
	}
}
