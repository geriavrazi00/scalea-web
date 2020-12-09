package com.scalea.entities;

import java.util.Collection;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.scalea.annotations.UniqueAreaName;
import com.scalea.validators.groups.OnCreate;
import com.scalea.validators.groups.OnUpdate;

import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@Table(name="areas")
@UniqueAreaName.List({
	@UniqueAreaName(message="{messages.area.exists}", id="id", name="name")
})
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
	private int capacity;
	
	@OneToMany(mappedBy="area")
    private Collection<Process> processes;
	
	@OneToMany(mappedBy="area")
	private Collection<Vacancy> vacancies;
}
