package com.scalea.models.dto;

import com.scalea.entities.Employee;
import com.scalea.entities.Vacancy;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class VacancyDTO {
	private Long id;
	private Employee employee;
	private boolean detach = false;
	
	public void toDTO(Vacancy vacancy) {
		this.id = vacancy.getId();
		this.employee = vacancy.getEmployee();
		this.detach = vacancy.isDetach();
	}
}
