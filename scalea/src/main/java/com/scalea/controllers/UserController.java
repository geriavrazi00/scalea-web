package com.scalea.controllers;

import java.sql.SQLException;
import java.util.Optional;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.scalea.configurations.Messages;
import com.scalea.entities.Role;
import com.scalea.entities.User;
import com.scalea.exceptions.UniqueUserUsernameViolationException;
import com.scalea.exceptions.UserNotFoundException;
import com.scalea.models.dto.UserDTO;
import com.scalea.repositories.RoleRepository;
import com.scalea.repositories.UserRepository;
import com.scalea.utils.Constants;

@Controller
@RequestMapping("/users")
public class UserController {
	
	private UserRepository userRepo;
	private RoleRepository roleRepo;
	private Logger log;
	private Messages messages;
	private PasswordEncoder passwordEncoder;
	
	@Autowired
	public UserController(UserRepository userRepo, RoleRepository roleRepo, Messages messages, PasswordEncoder passwordEncoder) {
		this.userRepo = userRepo;
		this.roleRepo = roleRepo;
		this.log = LoggerFactory.getLogger(RoleController.class);
		this.messages = messages;
		this.passwordEncoder = passwordEncoder;
	}
	
	@PreAuthorize("hasAnyAuthority('" + Constants.VIEW_USERS_PRIVILEGE + "', '" + Constants.UPSERT_USERS_PRIVILEGE + "', '" + Constants.DELETE_USERS_PRIVILEGE + "')")
	@GetMapping
	public String allUsers(Model model) {
		log.info("Method allUsers()");
		
		Iterable<User> users = userRepo.findAll();
		model.addAttribute("users", users);
		return "private/administration/users/userlist";
	}
	
	@PreAuthorize("hasAuthority('" + Constants.UPSERT_USERS_PRIVILEGE + "'")
	@GetMapping("/create")
	public String newUser(Model model) {
		log.info("Method newUser()");
		
		Iterable<Role> roles = roleRepo.findAll();
		
		model.addAttribute("user", new User());
		model.addAttribute("roles", roles);
		return "private/administration/users/createuser";
	}
	
	@PreAuthorize("hasAuthority('" + Constants.UPSERT_USERS_PRIVILEGE + "'")
	@PostMapping("/create")
	public String createUser(@Valid User user, Errors errors, Model model, RedirectAttributes redirectAttributes) throws UniqueUserUsernameViolationException {
		log.info("Method createUser()");
		
		if (errors.hasErrors()) {
			Iterable<Role> roles = roleRepo.findAll();
			model.addAttribute("roles", roles);
			
			return "private/administration/users/createuser";
		}
		
		try {
			String rawPassword = user.getPassword();
			user.setPassword(passwordEncoder.encode(rawPassword));
			user.setConfirmPassword(user.getPassword());
			this.userRepo.save(user);
			
			redirectAttributes.addFlashAttribute("message", this.messages.get("messages.user.created"));
		    redirectAttributes.addFlashAttribute("alertClass", "alert-success");
		    
			return "redirect:/users";
		} catch (DataIntegrityViolationException e) {
		    if (e.getMostSpecificCause().getClass().getName().equals("org.postgresql.util.PSQLException") && ((SQLException) e.getMostSpecificCause()).getSQLState().equals("23505"))
		        throw new UniqueUserUsernameViolationException(messages.get("messages.user.unique.username", user.getUsername()));
		    throw e;
		}
	}
	
	@PreAuthorize("hasAuthority('" + Constants.UPSERT_USERS_PRIVILEGE + "'")
	@GetMapping("edit/{id}")
	public String editUser(@PathVariable("id") Long id, Model model) throws Exception {
		log.info("Method editUser()");
		
		Optional<User> user = userRepo.findById(id);
		if (user.isPresent()) {
			Iterable<Role> roles = roleRepo.findAll();
			
			model.addAttribute("user", user.get());
			model.addAttribute("roles", roles);
			return "private/administration/users/edituser";
		} else {
			throw new UserNotFoundException(messages.get("messages.user.not.found"));
		}
	}
	
	@PreAuthorize("hasAuthority('" + Constants.UPSERT_USERS_PRIVILEGE + "'")
	@PostMapping("/edit/{id}")
	public String updateUser(@PathVariable("id") Long id, UserDTO userDTO, Errors errors, Model model, RedirectAttributes redirectAttributes) throws UniqueUserUsernameViolationException, UserNotFoundException {
		log.info("Method updateUser()");
		
		Optional<User> user = userRepo.findById(id);
		if (!user.isPresent()) throw new UserNotFoundException(messages.get("messages.user.not.found"));
		User foundUser = user.get();
		
		if (errors.hasErrors()) {
			Iterable<Role> roles = roleRepo.findAll();
			
			model.addAttribute("user", user);
			model.addAttribute("roles", roles);
			
			return "private/administration/users/edituser";
		}
		
		try {
			redirectAttributes.addFlashAttribute("message", this.messages.get("messages.user.updated"));
		    redirectAttributes.addFlashAttribute("alertClass", "alert-success");
			
			this.userRepo.save(userDTO.toUser(foundUser, roleRepo));
			return "redirect:/users";
			
		} catch (DataIntegrityViolationException e) {
		    if (e.getMostSpecificCause().getClass().getName().equals("org.postgresql.util.PSQLException") && ((SQLException) e.getMostSpecificCause()).getSQLState().equals("23505"))
		        throw new UniqueUserUsernameViolationException(messages.get("messages.user.unique.username", foundUser.getUsername()));
		    throw e;
		}
	}
	
	@PreAuthorize("hasAuthority('" + Constants.DELETE_USERS_PRIVILEGE + "'")
	@PostMapping("/delete/{id}")
	public String deleteUser(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) throws UserNotFoundException {
		log.info("Method deleteUser()");
		
		Optional<User> user = userRepo.findById(id);
		
		if (!user.isPresent()) throw new UserNotFoundException(messages.get("messages.user.not.found"));
		User foundUser = user.get();
		foundUser.setRoles(null);
		
		this.userRepo.delete(foundUser);
		
		redirectAttributes.addFlashAttribute("message", this.messages.get("messages.user.deleted"));
	    redirectAttributes.addFlashAttribute("alertClass", "alert-success");
		
		return "redirect:/users";
	}
}
