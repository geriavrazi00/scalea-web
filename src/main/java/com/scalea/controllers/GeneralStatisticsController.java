package com.scalea.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.scalea.utils.Constants;

@Controller
@RequestMapping("/statistics")
public class GeneralStatisticsController {
	private Logger log;
	
	@Autowired
	public GeneralStatisticsController() {
		this.log = LoggerFactory.getLogger(GeneralStatisticsController.class);
	}
	
	@PreAuthorize("hasAnyAuthority('" + Constants.VIEW_GENERAL_STATISTICS_PRIVILEGE + "')")
	@GetMapping
	public String generateStatistics(Model model) throws NumberFormatException, Exception {
		log.info("Method generateStatistics()");
		return "private/statistics/generalstatistics";
	}
}
