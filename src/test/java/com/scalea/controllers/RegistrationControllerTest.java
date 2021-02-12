/*
 * package com.scalea.controllers;
 * 
 * import static org.hamcrest.CoreMatchers.containsString; import static
 * org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
 * import static
 * org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
 * import static
 * org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
 * import static
 * org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
 * import static
 * org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
 * 
 * import org.junit.jupiter.api.Test; import org.junit.runner.RunWith; import
 * org.springframework.beans.factory.annotation.Autowired; import
 * org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest; import
 * org.springframework.test.context.junit4.SpringRunner; import
 * org.springframework.test.web.servlet.MockMvc;
 * 
 * @RunWith(SpringRunner.class)
 * 
 * @WebMvcTest(RegistrationController.class) public class
 * RegistrationControllerTest {
 * 
 * @Autowired private MockMvc mockMvc;
 * 
 * @Test public void testRegistrationPage() throws Exception {
 * mockMvc.perform(get("/register")) .andExpect(status().isOk())
 * .andExpect(view().name("public/registration"))
 * .andExpect(content().string(containsString("Register"))); }
 * 
 * @Test public void testRegistrationSave() throws Exception {
 * mockMvc.perform(post("/register")) // Performs POST
 * .andExpect(status().isOk()) // Expects HTTP 200
 * .andExpect(view().name("login")) // Expects login view
 * .andExpect(content().string(containsString("Login"))); // Expects Login } }
 */