package com.scalea.controllers;

import java.text.ParseException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

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
import com.scalea.entities.Area;
import com.scalea.entities.Group;
import com.scalea.entities.Vacancy;
import com.scalea.exceptions.GenericException;
import com.scalea.models.dto.GroupDTO;
import com.scalea.services.AreaService;
import com.scalea.services.GroupService;
import com.scalea.services.VacancyService;
import com.scalea.utils.Constants;
import com.scalea.utils.Utils;

@Controller
@RequestMapping("/areas/{id}/groups")
public class AreaGroupController {
	private AreaService areaService;
	private GroupService groupService;
	private VacancyService vacancyService;
	private Logger log;
	private Messages messages;
	
	@Autowired
	public AreaGroupController(AreaService areaService, GroupService groupService, VacancyService vacancyService, Messages messages) {
		this.areaService = areaService;
		this.groupService = groupService;
		this.vacancyService = vacancyService;
		this.log = LoggerFactory.getLogger(AreaGroupController.class);
		this.messages = messages;
	}
	
	@PreAuthorize("hasAnyAuthority('" + Constants.VIEW_GROUPS_PRIVILEGE + "')")
	@GetMapping
	public String showAreaGroups(Model model, @PathVariable("id") Long id, @RequestParam("page") Optional<Integer> page) throws GenericException, ParseException {
		log.info("Method showAreaGroups()");
		
		int currentPage = page.orElse(Constants.DEFAULT_PAGE);
        
        Optional<Area> optionalArea = areaService.findByIdAndEnabledIsTrue(id);
		if (!optionalArea.isPresent()) throw new GenericException(messages.get("messages.area.not.found"));
		Area area = optionalArea.get();
		
		Iterable<Vacancy> vacancies = vacancyService.findByAreaAndEnabled(area, true);
		Page<Group> groups = this.groupService.findByArea(area, PageRequest.of(currentPage - 1, Constants.DEFAULT_SIZE));
		
		model.addAttribute("area", area);
		model.addAttribute("groups", groups);
		model.addAttribute("vacancies", vacancies);
		if (model.getAttribute("group") == null) model.addAttribute("group", new Group());
		if (model.getAttribute("groupDTO") == null) model.addAttribute("groupDTO", new GroupDTO());
		model.addAttribute("pageNumbers", Utils.getPageNumbersList(groups.getTotalPages()));
		return "private/areas/groups/grouplist";
	}
	
	@PreAuthorize("hasAnyAuthority('" + Constants.UPSERT_VACANCIES_PRIVILEGE + "')")
	@GetMapping("/{groupId}")
	public @ResponseBody GroupDTO getGroup(@PathVariable("id") Long id, @PathVariable("groupId") Long groupId) throws GenericException {
		log.info("Method getGroup()");
		
		Optional<Area> area = areaService.findByIdAndEnabledIsTrue(id);
		if (area.isEmpty()) throw new GenericException(messages.get("messages.area.not.found"));
		
		Optional<Group> group = groupService.findById(groupId);
		if (group.isEmpty()) throw new GenericException(messages.get("messages.group.not.found"));
		
		GroupDTO groupDTO = new GroupDTO();
		groupDTO.toDTO(group.get());
		return groupDTO;
	}
	
	@PreAuthorize("hasAnyAuthority('" + Constants.UPSERT_VACANCIES_PRIVILEGE + "')")
	@PostMapping
	public String createGroup(Model model, @Valid Group group, Errors errors, @PathVariable("id") Long id, @RequestParam("page") Optional<Integer> page, 
			RedirectAttributes redirectAttributes) throws GenericException, ParseException {
		log.info("Method createGroup()");
		
		if (errors.hasErrors()) {
			return this.showAreaGroups(model, id, page);
		}
        
        Optional<Area> optionalArea = areaService.findByIdAndEnabledIsTrue(id);
		if (!optionalArea.isPresent()) throw new GenericException(messages.get("messages.area.not.found"));
		Area area = optionalArea.get();
		
		group.setId(null);
		group.setArea(area);
		group.setDefaultGroup(false);
		group = this.groupService.save(group);
		
		for (Vacancy vacancy : group.getVacancies()) {
			vacancy.setGroup(group);
			this.vacancyService.save(vacancy);
		}
		
		redirectAttributes.addFlashAttribute("message", this.messages.get("messages.group.created"));
	    redirectAttributes.addFlashAttribute("alertClass", "alert-success");
		return "redirect:/areas/" + id + "/groups" + paginationParameters(page);
	}
	
	@PreAuthorize("hasAuthority('" + Constants.UPSERT_GROUPS_PRIVILEGE + "'")
	@PostMapping("/update")
	public String updateGroup(@Valid GroupDTO groupDTO, Errors errors, @PathVariable("id") Long id, Model model, RedirectAttributes redirectAttributes, 
			@RequestParam("page") Optional<Integer> page) throws Exception {
		log.info("Method updateGroup()");
		
		if (errors.hasErrors()) {
			return this.showAreaGroups(model, id, page);
		}
		
		Optional<Area> optionalArea = areaService.findByIdAndEnabledIsTrue(id);
		if (optionalArea.isEmpty()) throw new GenericException(messages.get("messages.area.not.found"));
		Area area = optionalArea.get();
		
		Optional<Group> optionalOriginalGroup = this.groupService.findById(groupDTO.getId());
		if (optionalOriginalGroup.isEmpty()) throw new GenericException(messages.get("messages.group.not.found"));
		Group originalGroup = optionalOriginalGroup.get();
		
		Optional<Group> optionalDefaultGroup = this.groupService.findDefaultGroupByArea(area);
		if (optionalOriginalGroup.isEmpty()) throw new GenericException(messages.get("messages.default.group.not.found"));
		Group defaultGroup = optionalDefaultGroup.get();
		
		// To avoid leaving vacancies without groups, we set all the original group vacancies to the default group
		for (Vacancy vacancy : originalGroup.getVacancies()) {
			vacancy.setGroup(defaultGroup);
			this.vacancyService.save(vacancy);
		}
		
		Group group = groupDTO.toGroup();
		group.setArea(area);
		group = this.groupService.save(group);
		
		// Next we save the new vacancies by setting them to the new edited group
		for (Vacancy vacancy : groupDTO.getVacancies()) {
			vacancy.setGroup(group);
			this.vacancyService.save(vacancy);
		}
		
		redirectAttributes.addFlashAttribute("message", this.messages.get("messages.group.updated"));
	    redirectAttributes.addFlashAttribute("alertClass", "alert-success");
	    return "redirect:/areas/" + id + "/groups" + paginationParameters(page);
	}
	
	private String paginationParameters(Optional<Integer> page) {
		return "?page=" + page.orElse(Constants.DEFAULT_PAGE);
	}
}
