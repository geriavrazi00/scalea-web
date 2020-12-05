package com.scalea.entities;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@Table(name="activities")
public class Activity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	private double weight;
	
	@ManyToOne
    @JoinColumn(name="product_id", nullable=false)
    private Product product;
	
	@ManyToOne
    @JoinColumn(name="vacancy_id", nullable=false)
    private Vacancy vacancy;
}
