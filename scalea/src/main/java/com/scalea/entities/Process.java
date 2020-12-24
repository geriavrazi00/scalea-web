package com.scalea.entities;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@Table(name="processes")
public class Process {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@NotNull
	private int status;
	
	@NotNull
	@ManyToOne
    @JoinColumn(name="user_id", nullable=false)
	private User user;
	
	@NotNull
	@ManyToOne
    @JoinColumn(name="area_id", nullable=false)
	private Area area;
	
	@NotNull
	@ManyToOne
    @JoinColumn(name="product_id", nullable=false)
	private Product product;

	@Override
	public String toString() {
		return "Process [status=" + status + "]";
	}
}
