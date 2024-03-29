package com.scalea.models.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.scalea.annotations.NumbersWhenEnabled;
import com.scalea.entities.Product;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@NumbersWhenEnabled.List({ 
    @NumbersWhenEnabled(
      enabled = "withSubProducts", 
      number = "price", 
      message = "{messages.price.positive}"
    )
})
public class ProductDTO {
	
	private Long id;
	
	@NotNull(message="{messages.name.required}")
	@NotBlank(message="{messages.name.required}")
	@Size(max=255, message="{messages.name.max}")
	private String name;
	
	private double price;
	
	private byte[] image;
	
	private boolean enabled = true;
	
	private boolean withSubProducts;

	private String base64Image;
	
	public Product toNewProduct() {
		Product product = new Product();
		this.toProduct(product);
		
		return product;
	}
	
	public Product toExistingProduct() {
		Product product = new Product();
		product.setId(id);
		this.toProduct(product);
		
		return product;
	}
	
	private void toProduct(Product product) {
		product.setName(name);
		product.setPrice(price);
		product.setEnabled(true);
		product.setWithSubProducts(withSubProducts);
		product.setImage(image);
	}
	
	public void toDTO(Product product) {
		this.id = product.getId();
		this.name = product.getName();
		this.price = product.getPrice();
		this.image = product.getImage();
		this.enabled = product.isEnabled();
		this.withSubProducts = product.isWithSubProducts();
	}
}
