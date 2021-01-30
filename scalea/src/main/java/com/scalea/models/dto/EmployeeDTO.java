package com.scalea.models.dto;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.springframework.format.annotation.DateTimeFormat;

import com.scalea.entities.Employee;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeDTO {
	
	private Long id;
	
	@NotNull(message="{messages.name.required}")
	@NotBlank(message="{messages.name.required}")
	@Size(max=255, message="{messages.name.max}")
	private String firstName;
	
	@NotNull(message="{messages.lastname.required}")
	@NotBlank(message="{messages.lastname.required}")
	@Size(max=255, message="{messages.lastname.max}")
	private String lastName;
	
	@Size(max=255, message="{messages.accountnumber.max}")
	private String accountNumber;
	
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	@Temporal(TemporalType.DATE)
	private Date birthday;
	
	@NotNull(message="{messages.personalnumber.required}")
	@NotBlank(message="{messages.personalnumber.required}")
	@Size(max=255, message="{messages.personalnumber.max}")
	private String personalNumber;
	
	@Size(max=255, message="{messages.email.max}")
	@Email(message="{messages.invalid.email.format}")
	private String email;
	
	@Size(max=50, message="{messages.phonenumber.max}")
	@Column(name="phone_number")
	private String phoneNumber;
	
	boolean supervisor;
	
	public void toDTO(Employee employee) {
		this.id = employee.getId();
		this.firstName = employee.getFirstName();
		this.lastName = employee.getLastName();
		this.accountNumber = employee.getAccountNumber();
		this.birthday = employee.getBirthday();
		this.personalNumber = employee.getPersonalNumber();
		this.email = employee.getEmail();
		this.phoneNumber = employee.getPhoneNumber();
		this.supervisor = employee.isSupervisor();
	}
	
	public void mergeWithExistingEmployee(Employee employee) {
		employee.setFirstName(firstName);
		employee.setLastName(lastName);
		employee.setAccountNumber(accountNumber);
		employee.setBirthday(birthday);
		employee.setPersonalNumber(personalNumber);
		employee.setEmail(email);
		employee.setPhoneNumber(phoneNumber);
		employee.setSupervisor(supervisor);
	}	
}
