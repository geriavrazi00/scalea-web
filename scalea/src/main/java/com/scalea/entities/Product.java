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
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

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
	
	@NotNull(message="{messages.name.required}")
	@NotBlank(message="{messages.name.required}")
	@Size(max=255, message="{messages.name.max}")
	private String name;
	
	@Column(name="price_per_kg")
	private double price;
	
	private String image;
	
	private boolean enabled = true;
	
	@Column(name="with_sub_products")
	private boolean withSubProducts = true;
	
	@ManyToOne
    @JoinColumn(name="sub_product_id")
    private Product fatherProduct;
	
	@OneToMany(mappedBy="fatherProduct")
    private Collection<Product> childrenProducts;
}
