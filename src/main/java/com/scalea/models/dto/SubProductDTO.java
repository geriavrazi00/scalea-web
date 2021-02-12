package com.scalea.models.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;

import com.scalea.entities.Product;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class SubProductDTO {
	
	private Long id;
	
	@NotNull(message="{messages.name.required}")
	@NotBlank(message="{messages.name.required}")
	@Size(max=255, message="{messages.name.max}")
	private String name;
	
	@NotNull(message="{messages.price.required}")
	@Positive(message="{messages.price.positive}")
	private Double price;
	
	private String image;
	
	@NotNull(message="{messages.no.product.for.subproduct}")
    private Product fatherProduct;
	
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
		product.setWithSubProducts(false);
		product.setImage(image);
		product.setFatherProduct(fatherProduct);
	}
	
	public void toDTO(Product product) {
		this.id = product.getId();
		this.name = product.getName();
		this.price = product.getPrice();
		this.image = product.getImage();
		this.fatherProduct = product.getFatherProduct();
	}
}
