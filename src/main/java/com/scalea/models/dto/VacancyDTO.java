package com.scalea.models.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class VacancyDTO {
	private Long id;
	private int number;
	private String uuid;
	private Long areaId;
	private String employeeFirstName;
	private String employeeLastName;
	private boolean enabled = true;
}
