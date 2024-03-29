package com.scalea.entities;

import java.util.Base64;
import java.util.Collection;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.annotations.Type;
import org.hibernate.annotations.Where;

import com.scalea.annotations.NumbersWhenEnabled;

import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@Table(name="products")
@NumbersWhenEnabled.List({ 
    @NumbersWhenEnabled(
      enabled = "withSubProducts", 
      number = "price", 
      message = "{messages.price.positive}"
    )
})
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
	
	@Type(type="org.hibernate.type.BinaryType")
	private byte[] image;
	
	private boolean enabled = true;
	
	@Column(name="with_sub_products")
	private boolean withSubProducts = true;
	
	@ManyToOne
    @JoinColumn(name="father_product_id")
    private Product fatherProduct;
	
	@Where(clause="enabled = true")
	@OneToMany(mappedBy="fatherProduct")
	@OrderBy("name")
    private Collection<Product> childrenProducts;
	
	@Transient
	private String base64Image;
	
	public String getBase64Image() {
		return "data:image/png;base64, " + Base64.getEncoder().encodeToString(this.image);
	}

	@Override
	public String toString() {
		return "Product [id=" + id + ", name=" + name + ", price=" + price + ", image=" + image + ", enabled=" + enabled
				+ ", withSubProducts=" + withSubProducts + "]";
	}
}
