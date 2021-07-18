package com.scalea.entities;

import java.util.Collection;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@Table(name="groups")
public class Group {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@NotNull(message="{messages.name.required}")
	@NotBlank(message="{messages.name.required}")
	@Size(max=255, message="{messages.name.max}")
	@Column(name="name")
	private String name;
	
	@ManyToOne
    @JoinColumn(name="area_id")
	private Area area;
	
	@Column(name="default_group")
	private boolean defaultGroup;
	
	@OneToMany(mappedBy="group")
	private Collection<Vacancy> vacancies;
	
	@Transient
	private Process activeProcess;

	@Override
	public String toString() {
		return "Group [id=" + id + ", name=" + name + ", defaultGroup=" + defaultGroup + "]";
	}
}
