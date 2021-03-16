package com.scalea.models.dto;

import com.scalea.entities.Process;
import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ControlSubProductDTO {
	@NotNull(message="{messages.area.required}")
	private Long areaId;
	
	@NotNull(message="{messages.product.required}")
	private Long productId;
	private Long subProductId;
	
	private String areaName;
	private String productName;
	private String subProductName;
	private Process activeProcess;
}
