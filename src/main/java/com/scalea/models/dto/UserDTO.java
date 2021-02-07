package com.scalea.models.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.scalea.entities.User;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
	
	private Long id;
	
	private String username;
	
	@Size(max=255, message="{messages.name.max}")
	@NotBlank(message="{messages.name.required}")
	private String firstName;

	@Size(max=255, message="{messages.lastname.max}")
	@NotBlank(message="{messages.lastname.required}")
	private String lastName;
	
	@Size(max=50, message="{messages.phonenumber.max}")
	@NotBlank(message="{messages.phonenumber.required}")
	private String phoneNumber;
	
	@NotNull(message="{messages.role.required}")
	private Long roleId;
	
	public void toDTO(User user) {
		this.id = user.getId();
		this.username = user.getUsername();
		this.firstName = user.getFirstName();
		this.lastName = user.getLastName();
		this.phoneNumber = user.getPhoneNumber();
		this.roleId = user.getRole().getId();
	}
	
	public void toUser(User user) {
		user.setFirstName(this.firstName);
		user.setLastName(this.lastName);
		user.setPhoneNumber(this.phoneNumber);
		user.setUsername(this.username);
	}
}
