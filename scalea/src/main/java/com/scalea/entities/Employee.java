package com.scalea.entities;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@Table(name="employees")
public class Employee {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(name="firstname")
	@NotNull
	private String firstName;
	
	@NotNull
	@Column(name="lastname")
	private String lastName;
	
	@NotNull
	@Column(name="account_number")
	private String accountNumber;
	
	private Date birthday;
	
	@NotNull
	// @Unique
	@Column(name="personal_number")
	private String personalNumber;
	
	private String email;
	
	@Column(name="phone_number")
	private String phoneNumber;
	
	@NotNull
	private int status;
	
	@OneToOne(mappedBy = "employee")
	private Vacancy vacancy;
}
