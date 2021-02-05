package com.scalea.controllers;

import java.security.Principal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.scalea.configurations.Messages;
import com.scalea.entities.Role;
import com.scalea.entities.User;
import com.scalea.exceptions.GenericException;
import com.scalea.exceptions.UniqueUserUsernameViolationException;
import com.scalea.exceptions.UserNotFoundException;
import com.scalea.models.dto.ChangePasswordDTO;
import com.scalea.repositories.RoleRepository;
import com.scalea.services.UserService;
import com.scalea.utils.Constants;
import com.scalea.utils.Utils;
import com.scalea.validators.groups.OnCreate;
import com.scalea.validators.groups.OnUpdate;

@Controller
@RequestMapping("/users")
public class UserController {
	
	private UserService userService;
	private RoleRepository roleRepo;
	private Logger log;
	private Messages messages;
	private PasswordEncoder passwordEncoder;
	
	private static final int DEFAULT_PAGE = 1;
	private static final int DEFAULT_SIZE = 7;
	
	@Autowired
	public UserController(UserService userService, RoleRepository roleRepo, Messages messages, PasswordEncoder passwordEncoder) {
		this.userService = userService;
		this.roleRepo = roleRepo;
		this.log = LoggerFactory.getLogger(UserController.class);
		this.messages = messages;
		this.passwordEncoder = passwordEncoder;
	}
	
	@PreAuthorize("hasAnyAuthority('" + Constants.VIEW_USERS_PRIVILEGE + "', '" + Constants.UPSERT_USERS_PRIVILEGE + "', '" + Constants.DELETE_USERS_PRIVILEGE + "')")
	@GetMapping
	public String allUsers(HttpServletRequest request, Model model, Principal principal, @RequestParam("page") Optional<Integer> page) throws GenericException {
		log.info("Method allUsers()");
		
		int currentPage = page.orElse(DEFAULT_PAGE);
		User user = userService.findByUsername(principal.getName());
		Page<User> users = null;
		Iterable<Role> roles = null;
		
		if (request.isUserInRole(Constants.ROLE_ADMIN)) {
			users = userService.findAllExceptMe(user.getId(), PageRequest.of(currentPage - 1, DEFAULT_SIZE));
			roles = roleRepo.findAllByOrderByName();
	    } else {
	    	users = userService.findAllExceptMeAndNotAdmin(user.getId(), PageRequest.of(currentPage - 1, DEFAULT_SIZE));
	    	roles = roleRepo.findByNameNotInOrderByName(new ArrayList<>(Arrays.asList(new String[] {Constants.ROLE_ADMIN, Constants.ROLE_USER})));
	    }
		
		model.addAttribute("users", users);
		model.addAttribute("roles", roles);
		if (!model.containsAttribute("user")) model.addAttribute("user", new User());
		model.addAttribute("pageNumbers", Utils.getPageNumbersList(users.getTotalPages()));
		return "private/administration/users/userlist";
	}
	
	@PreAuthorize("hasAuthority('" + Constants.UPSERT_USERS_PRIVILEGE + "'")
	@PostMapping("/create")
	public String createUser(@Validated(OnCreate.class) User user, Errors errors, Model model, Principal principal, RedirectAttributes redirectAttributes,
			HttpServletRequest request, @RequestParam("page") Optional<Integer> page) throws GenericException {
		log.info("Method createUser()");
		
		if (errors.hasErrors()) {
			return this.allUsers(request, model, principal, page);
		}
		
		if (!request.isUserInRole(Constants.ROLE_ADMIN) && user.getRole().getName().equals(Constants.ROLE_ADMIN)) {
			throw new GenericException(this.messages.get("messages.unauthorized.access"));
		}
		
		if (userService.existsByUsername(user.getUsername())) {
			model.addAttribute("message", this.messages.get("messages.user.unique.username", user.getUsername()));
			model.addAttribute("alertClass", "alert-danger");
		    return this.allUsers(request, model, principal, page);
		}
		
		String rawPassword = user.getPassword();
		user.setPassword(passwordEncoder.encode(rawPassword));
		user.setConfirmPassword(user.getPassword());
		
		user.setRoles(new ArrayList<>());
		user.getRoles().add(user.getRole());
		this.userService.save(user);
		
		redirectAttributes.addFlashAttribute("message", this.messages.get("messages.user.created"));
	    redirectAttributes.addFlashAttribute("alertClass", "alert-success");
	    
		return "redirect:/users" + paginationParameters(page);
	}
	
//	@PreAuthorize("hasAuthority('" + Constants.UPSERT_USERS_PRIVILEGE + "'")
//	@GetMapping("edit/{id}")
//	public String editUser(@PathVariable("id") Long id, Model model) throws Exception {
//		log.info("Method editUser()");
//		
//		Optional<User> user = userService.findById(id);
//		if (!user.isPresent()) throw new UserNotFoundException(messages.get("messages.user.not.found"));
//		
//		Iterable<Role> roles = roleRepo.findAll();
//		
//		model.addAttribute("user", user.get());
//		model.addAttribute("roles", roles);
//		return "private/administration/users/edituser";
//	}
	
	@PreAuthorize("hasAuthority('" + Constants.UPSERT_USERS_PRIVILEGE + "'")
	@PostMapping("/edit/{id}")
	public String updateUser(@Validated(OnUpdate.class) User user, Errors errors, Model model, RedirectAttributes redirectAttributes) throws UniqueUserUsernameViolationException, UserNotFoundException {
		log.info("Method updateUser()");
		
		if (errors.hasErrors()) {
			Iterable<Role> roles = roleRepo.findAll();
			
			model.addAttribute("user", user);
			model.addAttribute("roles", roles);
			
			return "private/administration/users/edituser";
		}
		
		Optional<User> userById = userService.findById(user.getId());
		if (!userById.isPresent()) throw new UserNotFoundException(messages.get("messages.user.not.found"));
		User foundUser = userById.get();
		
		try {
			foundUser.setUsername(user.getUsername());
			foundUser.setFirstName(user.getFirstName());
			foundUser.setLastName(user.getLastName());
			foundUser.setPhoneNumber(user.getPhoneNumber());
			foundUser.setRoles(user.getRoles());
			
			this.userService.save(foundUser);
			
			redirectAttributes.addFlashAttribute("message", this.messages.get("messages.user.updated"));
		    redirectAttributes.addFlashAttribute("alertClass", "alert-success");
			return "redirect:/users";
			
		} catch (DataIntegrityViolationException e) {
		    if (e.getMostSpecificCause().getClass().getName().equals("org.postgresql.util.PSQLException") && ((SQLException) e.getMostSpecificCause()).getSQLState().equals("23505"))
		        throw new UniqueUserUsernameViolationException(messages.get("messages.user.unique.username", foundUser.getUsername()));
		    throw e;
		}
	}
	
	@PreAuthorize("hasAuthority('" + Constants.PASSWORDS_USERS_PRIVILEGE + "'")
	@GetMapping("password/{id}")
	public String changePassword(@PathVariable("id") Long id, Model model) throws Exception {
		log.info("Method changePassword()");
		
		Optional<User> user = userService.findById(id);
		if (!user.isPresent()) throw new UserNotFoundException(messages.get("messages.user.not.found"));
		
		model.addAttribute("changePasswordDTO", new ChangePasswordDTO(id));
		return "private/administration/users/changepassword";
	}
	
	@PreAuthorize("hasAuthority('" + Constants.PASSWORDS_USERS_PRIVILEGE + "'")
	@PostMapping("password/{id}")
	public String updatePassword(@Valid ChangePasswordDTO changePasswordDTO, Errors errors, Model model, RedirectAttributes redirectAttributes) throws Exception {
		log.info("Method updatePassword()");
		
		if (errors.hasErrors()) {
			model.addAttribute("changePasswordDTO", changePasswordDTO);
			return "private/administration/users/changepassword";
		}
		
		Optional<User> optionalUser = userService.findById(changePasswordDTO.getId());
		if (!optionalUser.isPresent()) throw new UserNotFoundException(messages.get("messages.user.not.found"));
		User user = optionalUser.get();
		
		user.setPassword(passwordEncoder.encode(changePasswordDTO.getPassword()));
		this.userService.save(user);
		
		redirectAttributes.addFlashAttribute("message", this.messages.get("messages.user.password.updated"));
	    redirectAttributes.addFlashAttribute("alertClass", "alert-success");
		return "redirect:/users";
	}
	
	@PreAuthorize("hasAuthority('" + Constants.DELETE_USERS_PRIVILEGE + "'")
	@PostMapping("/delete/{id}")
	public String deleteUser(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) throws UserNotFoundException {
		log.info("Method deleteUser()");
		
		Optional<User> user = userService.findById(id);
		
		if (!user.isPresent()) throw new UserNotFoundException(messages.get("messages.user.not.found"));
		User foundUser = user.get();
		foundUser.setRoles(null);
		
		this.userService.delete(foundUser);
		
		redirectAttributes.addFlashAttribute("message", this.messages.get("messages.user.deleted"));
	    redirectAttributes.addFlashAttribute("alertClass", "alert-success");
		
		return "redirect:/users";
	}
	
	private String paginationParameters(Optional<Integer> page) {
		return "?page=" + page.orElse(DEFAULT_PAGE);
	}
}
