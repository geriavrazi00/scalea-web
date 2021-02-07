package com.scalea.entities;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.springframework.format.annotation.DateTimeFormat;

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
	
	@NotNull(message="{messages.name.required}")
	@NotBlank(message="{messages.name.required}")
	@Size(max=255, message="{messages.name.max}")
	@Column(name="firstname")
	private String firstName;
	
	@NotNull(message="{messages.lastname.required}")
	@NotBlank(message="{messages.lastname.required}")
	@Size(max=255, message="{messages.lastname.max}")
	@Column(name="lastname")
	private String lastName;
	
	@Size(max=255, message="{messages.accountnumber.max}")
	@Column(name="account_number")
	private String accountNumber;
	
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	@Temporal(TemporalType.DATE)
	private Date birthday;
	
	@NotNull(message="{messages.personalnumber.required}")
	@NotBlank(message="{messages.personalnumber.required}")
	@Size(max=255, message="{messages.personalnumber.max}")
	@Column(name="personal_number")
	private String personalNumber;
	
	@Size(max=255, message="{messages.email.max}")
	@Email(message="{messages.invalid.email.format}")
	private String email;
	
	@Size(max=50, message="{messages.phonenumber.max}")
	@Column(name="phone_number")
	private String phoneNumber;
	
	private boolean enabled = true;
	
	private boolean supervisor = false;
	
	@Transient
	private boolean detach = false;
	
	@OneToOne(mappedBy = "employee")
	private Vacancy vacancy;

	@Override
	public String toString() {
		return "Employee [id=" + id + ", firstName=" + firstName + ", lastName=" + lastName + ", accountNumber="
				+ accountNumber + ", birthday=" + birthday + ", personalNumber=" + personalNumber + ", email=" + email
				+ ", phoneNumber=" + phoneNumber + ", enabled=" + enabled + ", supervisor=" + supervisor + "]";
	}
}
