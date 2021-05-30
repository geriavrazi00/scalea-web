package com.scalea.controllers;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.scalea.configurations.Messages;
import com.scalea.entities.Privilege;
import com.scalea.entities.Role;
import com.scalea.enums.ApplicationRoles;
import com.scalea.exceptions.GenericException;
import com.scalea.exceptions.RoleCannotBeDeletedException;
import com.scalea.exceptions.RoleNotFoundException;
import com.scalea.exceptions.UniqueRoleNameViolationException;
import com.scalea.models.dto.RoleDTO;
import com.scalea.repositories.PrivilegeRepository;
import com.scalea.repositories.RoleRepository;
import com.scalea.utils.Constants;
import com.scalea.utils.RoleUtil;
import com.scalea.utils.Utils;

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
		Iterable<Privilege> privileges = privilegeRepo.findAll();
		Map<String, List<Privilege>> groupedPrivileges = RoleUtil.groupPrivileges(privileges, this.messages);
		
		if (request.isUserInRole(Constants.ROLE_ADMIN)) {
			roles = roleRepo.findAllByOrderByName(PageRequest.of(currentPage - 1, DEFAULT_SIZE));
		} else {
			roles = roleRepo.findByNameNotInOrderByName(new String[] {Constants.ROLE_ADMIN, Constants.ROLE_USER}, 
				PageRequest.of(currentPage - 1, DEFAULT_SIZE));
		}
		
		model.addAttribute("roles", roles);
		model.addAttribute("privileges", groupedPrivileges);
		if (!model.containsAttribute("role")) model.addAttribute("role", new Role());
		if (!model.containsAttribute("roleDTO")) model.addAttribute("roleDTO", new RoleDTO());
		model.addAttribute("pageNumbers", Utils.getPageNumbersList(roles.getTotalPages()));
		return "private/administration/roles/rolelist";
	}

	@PreAuthorize("hasAuthority('" + Constants.UPSERT_ROLES_PRIVILEGE + "'")
	@PostMapping("/create")
	public String createRole(@Valid Role role, Errors errors, Model model, RedirectAttributes redirectAttributes, HttpServletRequest request, 
			@RequestParam("page") Optional<Integer> page) throws UniqueRoleNameViolationException {
		log.info("Method createRole()");
		
		if (errors.hasErrors()) return this.allRoles(request, model, page);
		
		if (roleRepo.existsByName(role.getName())) {
			model.addAttribute("message", messages.get("messages.role.unique.name", role.getName()));
			model.addAttribute("alertClass", "alert-danger");
		    return this.allRoles(request, model, page);
		}
		
		this.roleRepo.save(role);
		
		redirectAttributes.addFlashAttribute("message", this.messages.get("messages.role.created"));
	    redirectAttributes.addFlashAttribute("alertClass", "alert-success");
		return "redirect:/roles" + paginationParameters(page);
	}
	
	@PreAuthorize("hasAnyAuthority('" + Constants.UPSERT_ROLES_PRIVILEGE + "')")
	@GetMapping("/{id}")
	public @ResponseBody RoleDTO getRole(@PathVariable("id") Long id) throws Exception {
		log.info("Method getRole()");
		
		Optional<Role> role = roleRepo.findById(id);
		if (!role.isPresent()) throw new GenericException(messages.get("messages.role.not.found"));
		
		RoleDTO newRole = new RoleDTO();
		newRole.toDTO(role.get());
		return newRole;
	}
	
	@PreAuthorize("hasAuthority('" + Constants.UPSERT_ROLES_PRIVILEGE + "'")
	@PostMapping("/update")
	public String updateRole(@Valid RoleDTO roleDTO, Errors errors, Model model, RedirectAttributes redirectAttributes, HttpServletRequest request, 
			@RequestParam("page") Optional<Integer> page) throws RoleNotFoundException {
		log.info("Method updateRole()");
		
		if (errors.hasErrors()) return this.allRoles(request, model, page);
		Optional<Role> optionalRole = roleRepo.findById(roleDTO.getId());
		if (!optionalRole.isPresent()) throw new RoleNotFoundException(messages.get("messages.role.not.found"));
		Role existingRole = optionalRole.get();
		
		if (roleRepo.existsByNameAndIdNot(roleDTO.getName(), existingRole.getId())) {
			model.addAttribute("message", messages.get("messages.role.unique.name", roleDTO.getName()));
			model.addAttribute("alertClass", "alert-danger");
		    return this.allRoles(request, model, page);
		}
		
		redirectAttributes.addFlashAttribute("message", this.messages.get("messages.role.updated"));
	    redirectAttributes.addFlashAttribute("alertClass", "alert-success");
		if (ApplicationRoles.getRoleByName(roleDTO.getName()) != null) roleDTO.setDeletable(false);
		roleDTO.toRole(existingRole);
	    
		this.roleRepo.save(existingRole);
		return "redirect:/roles" + paginationParameters(page);
	}
	
	@PreAuthorize("hasAuthority('" + Constants.DELETE_ROLES_PRIVILEGE + "'")
	@PostMapping("/delete/{id}")
	public String deleteRole(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) throws RoleCannotBeDeletedException, RoleNotFoundException {
		log.info("Method deleteRole()");
		
		Optional<Role> role = roleRepo.findById(id);
		
		if (!role.isPresent()) throw new RoleNotFoundException(messages.get("messages.role.not.found"));
		Role foundRole = role.get();
		if (!foundRole.isDeletable()) throw new RoleCannotBeDeletedException(messages.get("messages.role.not.deletable"));
		if (foundRole.getUsers() != null && foundRole.getUsers().size() > 0) throw new RoleCannotBeDeletedException(messages.get("messages.role.not.deletable"));
			
		foundRole.setPrivileges(null);
		this.roleRepo.delete(foundRole);
		
		redirectAttributes.addFlashAttribute("message", this.messages.get("messages.role.deleted"));
	    redirectAttributes.addFlashAttribute("alertClass", "alert-success");
		
		return "redirect:/roles";
	}
	
	private String paginationParameters(Optional<Integer> page) {
		return "?page=" + page.orElse(DEFAULT_PAGE);
	}
}
