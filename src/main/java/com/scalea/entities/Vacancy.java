package com.scalea.entities;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@Table(name="vacancies")
public class Vacancy {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	private int number;
	
	private String uuid;
	
	@NotNull(message="{messages.area.required}")
	@ManyToOne
	@JoinColumn(name="area_id", nullable=false)
	private Area area;
	
	@OneToOne
	@JoinColumn(name="employee_id", nullable=true)
	private Employee employee;
	
	private boolean enabled = true;
	
	@Transient
	private boolean detach = false;

	@Override
	public String toString() {
		return "Vacancy [number=" + number + "]";
	}
}
