package com.scalea.controllers;

import java.sql.SQLException;
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
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.scalea.configurations.Messages;
import com.scalea.entities.Privilege;
import com.scalea.entities.Role;
import com.scalea.enums.ApplicationRoles;
import com.scalea.exceptions.RoleCannotBeDeletedException;
import com.scalea.exceptions.RoleNotFoundException;
import com.scalea.exceptions.UniqueRoleNameViolationException;
import com.scalea.repositories.PrivilegeRepository;
import com.scalea.repositories.RoleRepository;
import com.scalea.utils.Constants;

@Controller
@RequestMapping("/roles")
public class RoleController {
	
	private RoleRepository roleRepo;
	private PrivilegeRepository privilegeRepo;
	private Logger log;
	private Messages messages;
	
	private static final int DEFAULT_PAGE = 1;
	private static final int DEFAULT_SIZE = 7;

	@Autowired
	public RoleController(RoleRepository roleRepo, PrivilegeRepository privilegeRepo, Messages messages) {
		this.roleRepo = roleRepo;
		this.privilegeRepo = privilegeRepo;
		this.log = LoggerFactory.getLogger(RoleController.class);
		this.messages = messages;
	}

	@PreAuthorize("hasAnyAuthority('" + Constants.VIEW_ROLES_PRIVILEGE + "', '" + Constants.UPSERT_ROLES_PRIVILEGE + "', '" + Constants.DELETE_ROLES_PRIVILEGE + "')")
	@GetMapping
	public String allRoles(HttpServletRequest request, Model model, @RequestParam("page") Optional<Integer> page) {
		log.info("Method allRoles()");
		
		int currentPage = page.orElse(DEFAULT_PAGE);
		Page<Role> roles = null;
		
		if (request.isUserInRole(Constants.ROLE_ADMIN)) {
			roles = roleRepo.findAll(PageRequest.of(currentPage - 1, DEFAULT_SIZE));
		} else {
			roles = roleRepo.findByNameNotIn(new String[] {Constants.ROLE_ADMIN, Constants.ROLE_USER}, PageRequest.of(currentPage - 1, DEFAULT_SIZE));
		}
		
		model.addAttribute("roles", roles);
		return "private/administration/roles/rolelist";
	}
	
	@PreAuthorize("hasAuthority('" + Constants.UPSERT_ROLES_PRIVILEGE + "'")
	@GetMapping("/create")
	public String newRole(Model model) {
		log.info("Method newRole()");
		
		Iterable<Privilege> privileges = privilegeRepo.findAll();
		
		model.addAttribute("role", new Role());
		model.addAttribute("privileges", privileges);
		return "private/administration/roles/createrole";
	}

	@PreAuthorize("hasAuthority('" + Constants.UPSERT_ROLES_PRIVILEGE + "'")
	@PostMapping("/create")
	public String createRole(@Valid Role role, Errors errors, Model model, RedirectAttributes redirectAttributes) throws UniqueRoleNameViolationException {
		log.info("Method createRole()");
		
		if (errors.hasErrors()) {
			Iterable<Privilege> privileges = privilegeRepo.findAll();
			model.addAttribute("privileges", privileges);
			
			return "private/administration/roles/createrole";
		}
		
		try {
			this.roleRepo.save(role);
			
			redirectAttributes.addFlashAttribute("message", this.messages.get("messages.role.created"));
		    redirectAttributes.addFlashAttribute("alertClass", "alert-success");
		    
			return "redirect:/roles";
		} catch (DataIntegrityViolationException e) {
		    if (e.getMostSpecificCause().getClass().getName().equals("org.postgresql.util.PSQLException") && ((SQLException) e.getMostSpecificCause()).getSQLState().equals("23505"))
		        throw new UniqueRoleNameViolationException(messages.get("messages.role.unique.name", role.getName()));
		    throw e;
		}
	}
	
	@PreAuthorize("hasAuthority('" + Constants.UPSERT_ROLES_PRIVILEGE + "'")
	@GetMapping("edit/{id}")
	public String editRole(@PathVariable("id") Long id, Model model) throws Exception {
		log.info("Method editRole()");
		
		Optional<Role> role = roleRepo.findById(id);
		if (role.isPresent()) {
			Iterable<Privilege> privileges = privilegeRepo.findAll();
			
			model.addAttribute("role", role.get());
			model.addAttribute("privileges", privileges);
			return "private/administration/roles/editrole";
		} else {
			throw new RoleNotFoundException(messages.get("messages.role.not.found"));
		}
	}
	
	@PreAuthorize("hasAuthority('" + Constants.UPSERT_ROLES_PRIVILEGE + "'")
	@PostMapping("/edit/{id}")
	public String updateRole(@Valid Role role, Errors errors, Model model, RedirectAttributes redirectAttributes) throws UniqueRoleNameViolationException {
		log.info("Method updateRole()");
		
		if (errors.hasErrors()) {
			Iterable<Privilege> privileges = privilegeRepo.findAll();
			
			model.addAttribute("role", role);
			model.addAttribute("privileges", privileges);
			
			return "private/administration/roles/editrole";
		}
		
		try {
			redirectAttributes.addFlashAttribute("message", this.messages.get("messages.role.updated"));
		    redirectAttributes.addFlashAttribute("alertClass", "alert-success");
			if (ApplicationRoles.getRoleByName(role.getName()) != null) role.setDeletable(false);
		    
			this.roleRepo.save(role);
			return "redirect:/roles";
			
		} catch (DataIntegrityViolationException e) {
		    if (e.getMostSpecificCause().getClass().getName().equals("org.postgresql.util.PSQLException") && ((SQLException) e.getMostSpecificCause()).getSQLState().equals("23505"))
		        throw new UniqueRoleNameViolationException(messages.get("messages.role.unique.name", role.getName()));
		    throw e;
		}
	}
	
	@PreAuthorize("hasAuthority('" + Constants.DELETE_ROLES_PRIVILEGE + "'")
	@PostMapping("/delete/{id}")
	public String deleteRole(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) throws RoleCannotBeDeletedException, RoleNotFoundException {
		log.info("Method deleteRole()");
		
		Optional<Role> role = roleRepo.findById(id);
		
		if (!role.isPresent()) throw new RoleNotFoundException(messages.get("messages.role.not.found"));
		Role foundRole = role.get();
		if (!foundRole.isDeletable()) throw new RoleCannotBeDeletedException(messages.get("messages.role.not.deletable"));
			
		foundRole.setPrivileges(null);
		foundRole.setUsers(null);
		this.roleRepo.delete(foundRole);
		
		redirectAttributes.addFlashAttribute("message", this.messages.get("messages.role.deleted"));
	    redirectAttributes.addFlashAttribute("alertClass", "alert-success");
		
		return "redirect:/roles";
	}
}
