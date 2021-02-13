package com.scalea.models.dto;

import java.util.Collection;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

import com.scalea.entities.Privilege;
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
 
    @NotEmpty(message="{messages.at.least.one.privilege}")
    private Collection<Privilege> privileges;
    
    private boolean deletable = true;
	
	public void toDTO(Role role) {
		this.id = role.getId();
		this.name = role.getName();
		this.privileges = role.getPrivileges();
	}
	
	public void toRole(Role role) {
		role.setId(this.id);
		role.setName(this.name);
		role.setPrivileges(this.privileges);
		role.setDeletable(this.deletable);
	}
}
