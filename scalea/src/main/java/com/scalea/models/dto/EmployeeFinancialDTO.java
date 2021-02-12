package com.scalea.models.dto;

import com.scalea.entities.Employee;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeFinancialDTO {
	private Long id;
	private String firstName;
	private String lastName;
	private String accountNumber;
	private String personalNumber;
	
	public void toDTO(Employee employee) {
		this.id = employee.getId();
		this.firstName = employee.getFirstName();
		this.lastName = employee.getLastName();
		this.accountNumber = employee.getAccountNumber();
		this.personalNumber = employee.getPersonalNumber();
	}
}
