package com.scalea.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.scalea.configurations.Messages;

@Controller
@RequestMapping("/processes")
public class ProcessController {

	//private ProcessRepository processRepo;
	private Logger log;
	private Messages messages;
	
	@Autowired
	public ProcessController(/*ProcessRepository processRepo,*/ Messages messages) {
		//this.processRepo = processRepo;
		this.log = LoggerFactory.getLogger(ProcessController.class);
		this.messages = messages;
	}
}
