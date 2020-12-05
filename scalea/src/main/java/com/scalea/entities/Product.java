package com.scalea.entities;

import java.util.Collection;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@Table(name="products")
public class Product {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@NotNull
	private String name;
	
	@NotNull
	@Column(name="price_per_kg")
	private double price;
	
	private String image;
	
	@ManyToMany(cascade = CascadeType.ALL)
    @JoinTable( 
        name = "products_hierarchy", 
        joinColumns = @JoinColumn(
          name = "father_id", referencedColumnName = "id"), 
        inverseJoinColumns = @JoinColumn(
          name = "children_id", referencedColumnName = "id"))
	private Collection<Product> childrenProducts;
	
	@ManyToMany(cascade = CascadeType.ALL)
    @JoinTable( 
        name = "products_hierarchy", 
        joinColumns = @JoinColumn(
          name = "children_id", referencedColumnName = "id"), 
        inverseJoinColumns = @JoinColumn(
          name = "father_id", referencedColumnName = "id"))
	private Collection<Product> fatherProducts;
}
