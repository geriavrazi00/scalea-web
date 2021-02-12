package com.scalea.models.dto;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class AlterVacancyDTO {
	
	@Min(value=1, message="{messages.value.at.least.one}")
	@Max(value=5000, message="{messages.value.max.threshold}")
	@NotNull(message="{messages.capacity.required}")
	private Integer capacity;
}
