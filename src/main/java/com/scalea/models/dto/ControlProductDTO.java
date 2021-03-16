package com.scalea.models.dto;

import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ControlProductDTO {
	@NotNull(message="{messages.area.required}")
	private Long areaId;
	
	@NotNull(message="{messages.product.required}")
	private Long productId;
}
