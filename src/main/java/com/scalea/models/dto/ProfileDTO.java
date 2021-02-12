package com.scalea.models.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import com.scalea.entities.User;
import com.sun.istack.NotNull;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProfileDTO {

	private String username;
	
	@NotNull
	@Size(max=255, message="{messages.name.max}")
	@NotBlank(message="{messages.name.required}")
	private String firstName;
	
	@NotNull
	@Size(max=255, message="{messages.lastname.max}")
	@NotBlank(message="{messages.lastname.required}")
	private String lastName;
	
	@Size(max=50, message="{messages.phonenumber.max}")
	@NotBlank(message="{messages.phonenumber.required}")
	private String phoneNumber;
	
	public void toDTO(User user) {
		this.username = user.getUsername();
		this.firstName = user.getFirstName();
		this.lastName = user.getLastName();
		this.phoneNumber = user.getPhoneNumber();
	}
	
	public void toUser(User user) {
		user.setFirstName(this.firstName);
		user.setLastName(this.lastName);
		user.setPhoneNumber(this.phoneNumber);
	}
}
