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

import com.scalea.utils.Utils;

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
	
	@Transient
	private String base64Image;
	
	@ManyToOne
	@JoinColumn(name="group_id", nullable=false)
	private Group group;
	
	public String getBase64Image() {
		return Utils.getBarCodeImage(this.uuid, 450, 80, this.getUuid());
	}

	@Override
	public String toString() {
		return "Vacancy [number=" + number + "]";
	}
}
