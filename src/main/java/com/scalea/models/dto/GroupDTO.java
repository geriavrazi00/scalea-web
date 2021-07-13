package com.scalea.models.dto;

import java.util.ArrayList;
import java.util.Collection;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.scalea.entities.Group;
import com.scalea.entities.Vacancy;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GroupDTO {
	private Long id;
	
	@NotNull(message="{messages.name.required}")
	@NotBlank(message="{messages.name.required}")
	@Size(max=255, message="{messages.name.max}")
	private String name;
	
	@JsonIgnore
	private Collection<Vacancy> vacancies;
	
	private Collection<VacancyDTO> vacancyDTOs;
	
	public Group toGroup() {
		Group group = new Group();
		group.setId(this.id);
		group.setDefaultGroup(false);
		group.setName(this.name);
		group.setVacancies(this.vacancies);
		
		return group;
	}
	
	public void toDTO(Group group) {
		this.id = group.getId();
		this.name = group.getName();
		this.vacancies = group.getVacancies();
		this.populateVacancyDTOs();
	}
	
	public void populateVacancyDTOs() {
		vacancyDTOs = new ArrayList<>();
		
		for (Vacancy vacancy : vacancies) {
			VacancyDTO vacancyDTO = new VacancyDTO();
			vacancyDTO.setId(vacancy.getId());
			vacancyDTO.setAreaId(vacancy.getArea().getId());
			vacancyDTO.setNumber(vacancy.getNumber());
			vacancyDTO.setUuid(vacancy.getUuid());
			vacancyDTO.setEnabled(vacancy.isEnabled());
			vacancyDTO.setEmployeeFirstName(vacancy.getEmployee() != null ? vacancy.getEmployee().getFirstName() : null);
			vacancyDTO.setEmployeeLastName(vacancy.getEmployee() != null ? vacancy.getEmployee().getLastName() : null);
			
			vacancyDTOs.add(vacancyDTO);
		}		
	}
	
	@Override
	public String toString() {
		return "Group [id=" + id + ", name=" + name + "]";
	}
}
