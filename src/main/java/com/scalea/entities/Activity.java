package com.scalea.entities;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.springframework.format.annotation.DateTimeFormat;

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
	
	@DateTimeFormat(pattern = "yyyy-MM-dd h:i:s")
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="created_at")
	private Date createdAt;
	
	private double weight;
	
	@ManyToOne
    @JoinColumn(name="product_id", nullable=false)
    private Product product;
	
	@ManyToOne
    @JoinColumn(name="vacancy_id", nullable=false)
    private Vacancy vacancy;
	
	@ManyToOne
    @JoinColumn(name="employee_id", nullable=false)
    private Employee employee;
	
	@DateTimeFormat(pattern = "yyyy-MM-dd h:i:s")
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="date")
	private Date date;
	
	@ManyToOne
    @JoinColumn(name="area_id", nullable=false)
    private Area area;
	
	@ManyToOne
    @JoinColumn(name="user_id", nullable=false)
    private User user;
	
	@ManyToOne
	@JoinColumn(name="group_id")
	private Group group;
	
	@PrePersist
	void preInsert() {
	   if (this.createdAt == null) this.createdAt = new Date();
	}
}
