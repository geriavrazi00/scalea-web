package com.scalea.models.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import com.scalea.entities.Role;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RoleDTO {
	
    private Long id;
	
	@Size(max=255, message="{messages.name.max}")
	@NotBlank(message="{messages.name.required}")
    private String name;
	
	public void toDTO(Role role) {
		this.id = role.getId();
		this.name = role.getName();
	}
	
	public void toRole(Role role) {
		role.setId(this.id);
		role.setName(this.name);
	}
}
