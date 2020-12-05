package com.scalea.entities;

import java.util.Collection;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@Table(name="areas")
public class Area {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	// @Unique
	@NotNull
	private String name;
	
	@NotNull
	private boolean enabled;

	@NotNull
	private int capacity;
	
	@OneToMany(mappedBy="area")
    private Collection<Process> processes;
	
	@OneToMany(mappedBy="area")
	private Collection<Vacancy> vacancies;
}
