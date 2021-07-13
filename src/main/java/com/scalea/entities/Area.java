package com.scalea.entities;

import java.util.Collection;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

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
	
	@NotNull
	@NotBlank(message="{messages.name.required}")
	@Size(max=255, message="{messages.name.max}")
	private String name;
	
	private boolean enabled;

	@Min(value=1, message="{messages.value.at.least.one}")
	@Max(value=5000, message="{messages.value.max.threshold}")
	@NotNull(message="{messages.capacity.required}")
	private Integer capacity;
	
	@OneToMany(mappedBy="area")
    private Collection<Process> processes;
	
	@OneToMany(mappedBy="area")
	private Collection<Vacancy> vacancies;
	
	@ManyToOne
	@JoinColumn(name="user_id")
	private User user;
	
	@Transient
	private Process activeProcess;
	
	@Transient
	private int employeeNumber;
	
	private String uuid;
	
	@OneToMany(mappedBy="area")
	private Collection<Group> groups;
	
	public void calculateEmployeeNumber() {
		for (Vacancy vacancy: this.getVacancies()) {
			if (vacancy.getEmployee() != null) {
				employeeNumber++;
			}
		}
	}

	@Override
	public String toString() {
		return "Area [name=" + name + ", enabled=" + enabled + ", capacity=" + capacity + "]";
	}
}
