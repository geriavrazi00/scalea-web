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
	@NotNull(message="{messages.name.required}")
	@NotBlank(message="{messages.name.required}")
	@Size(max=255, message="{messages.name.max}")
	private String name;
	
	@Positive(message="{messages.price.positive}")
	private double price;
	
	private String image;
	
	@NotNull(message="{messages.no.product.for.subproduct}")
    private Product fatherProduct;
	
	public Product toProduct() {
		Product product = new Product();
		product.setName(name);
		product.setPrice(price);
		product.setEnabled(true);
		product.setWithSubProducts(false);
		product.setImage(image);
		product.setFatherProduct(fatherProduct);
		
		return product;
	}
}
