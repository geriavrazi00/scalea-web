package com.scalea.controllers;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.scalea.entities.Privilege;
import com.scalea.entities.Role;
import com.scalea.repositories.PrivilegeRepository;
import com.scalea.repositories.RoleRepository;
import com.scalea.utils.Constants;

@Controller
@RequestMapping("/roles")
public class RoleController {
	
	private RoleRepository roleRepo;
	private PrivilegeRepository privilegeRepo;

	@Autowired
	public RoleController(RoleRepository roleRepo, PrivilegeRepository privilegeRepo) {
		this.roleRepo = roleRepo;
		this.privilegeRepo = privilegeRepo;
	}

	@PreAuthorize("hasAnyAuthority('" + Constants.VIEW_ROLES_PRIVILEGE + "', '" + Constants.UPSERT_ROLES_PRIVILEGE + "', '" + Constants.DELETE_ROLES_PRIVILEGE + "')")
	@GetMapping
	public String allRoles(Model model) {
		Iterable<Role> roles = roleRepo.findAll();
		model.addAttribute("roles", roles);
		return "private/administration/roles/rolelist";
	}
	
	@PreAuthorize("hasAuthority('" + Constants.UPSERT_ROLES_PRIVILEGE + "'")
	@GetMapping("/create")
	public String newRole(Model model) {
		Iterable<Privilege> privileges = privilegeRepo.findAll();
		
		model.addAttribute("role", new Role());
		model.addAttribute("privileges", privileges);
		return "private/administration/roles/createrole";
	}

	@PreAuthorize("hasAuthority('" + Constants.UPSERT_ROLES_PRIVILEGE + "'")
	@PostMapping("/create")
	public String createRole(@Valid Role role, Errors errors, Model model) {
		if (errors.hasErrors()) {
			Iterable<Privilege> privileges = privilegeRepo.findAll();
			model.addAttribute("privileges", privileges);
			
			return "private/administration/roles/createrole";
		}
		
		this.roleRepo.save(role);
		return "redirect:/roles";
	}
	
	@PreAuthorize("hasAuthority('" + Constants.UPSERT_ROLES_PRIVILEGE + "'")
	@GetMapping("/{id}/edit")
	public String editRole() {
		return "private/administration/roles/editrole";
	}
	
	@PreAuthorize("hasAuthority('" + Constants.UPSERT_ROLES_PRIVILEGE + "'")
	@PutMapping("/{id}")
	public String updateRole() {
		return "redirect:private/administration/roles/rolelist";
	}
	
	@PreAuthorize("hasAuthority('" + Constants.DELETE_ROLES_PRIVILEGE + "'")
	@DeleteMapping("/{id}")
	public String deleteRole() {
		return "redirect:private/administration/roles/rolelist";
	}
}
