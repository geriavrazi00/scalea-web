package com.scalea.controllers;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.scalea.utils.Constants;

@Controller
@RequestMapping("/roles")
public class RoleController {

	@PreAuthorize("hasAnyAuthority('" + Constants.VIEW_ROLES_PRIVILEGE + "', '" + Constants.UPSERT_ROLES_PRIVILEGE + "', '" + Constants.DELETE_ROLES_PRIVILEGE + "')")
	@GetMapping
	public String allRoles() {
		return "private/administration/roles/rolelist";
	}
	
	@PreAuthorize("hasAuthority('" + Constants.UPSERT_ROLES_PRIVILEGE + "'")
	@GetMapping("/create")
	public String newRole() {
		return "private/administration/roles/createrole";
	}

	@PreAuthorize("hasAuthority('" + Constants.UPSERT_ROLES_PRIVILEGE + "'")
	@PostMapping
	public String createRole() {
		return "redirect:private/administration/roles/rolelist";
	}
	
	@PreAuthorize("hasAuthority('" + Constants.VIEW_ROLES_PRIVILEGE + "'")
	@GetMapping("/{id}")
	public String showRole() {
		return "private/administration/roles/showrole";
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
