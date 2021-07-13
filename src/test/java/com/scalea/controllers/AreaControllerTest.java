package com.scalea.controllers;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
public class AreaControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Test
	public void getAllAreasTest() throws Exception {		
		this.mockMvc
			.perform(get("/areas")
					.with(csrf())
					.with(SecurityMockMvcRequestPostProcessors.user("test").roles("ADMIN")))
			.andExpect(status().isOk())
			.andExpect(view().name("private/areas/arealist"))
			.andExpect(model().attributeExists("areas"))
			.andExpect(model().attributeExists("users"))
			.andExpect(model().attributeExists("area"))
			.andExpect(model().attributeExists("areaDTO"))
			.andExpect(model().attributeExists("pageNumbers"));
	}
	
	@Test
	public void failAuthentication() throws Exception {
		this.mockMvc.perform(get("/areas"))
			.andExpect(status().is(302));
	}
}
