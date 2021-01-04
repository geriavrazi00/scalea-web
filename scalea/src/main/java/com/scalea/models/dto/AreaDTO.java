package com.scalea.models.dto;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.scalea.entities.Area;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class AreaDTO {
	
	private Long id;
	
	@NotNull
	@NotBlank(message="{messages.name.required}")
	@Size(max=255, message="{messages.name.max}")
	private String name;

	@Min(value=1, message="{messages.value.at.least.one}")
	@Max(value=5000, message="{messages.value.max.threshold}")
	@NotNull(message="{messages.capacity.required}")
	private Integer capacity;
	
	public void toDTO(Area area) {
		this.id = area.getId();
		this.name = area.getName();
		this.capacity = area.getCapacity();
	}
	
	public void mergeWithExistingArea(Area area) {
		area.setName(name);
		area.setCapacity(capacity);
	}	
}
