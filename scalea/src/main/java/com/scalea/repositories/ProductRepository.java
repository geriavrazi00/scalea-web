package com.scalea.repositories;

import org.springframework.data.repository.CrudRepository;

import com.scalea.entities.Product;

public interface ProductRepository extends CrudRepository<Product, Long> {
	Iterable<Product> findByEnabled(boolean enabled);
	Iterable<Product> findByFatherProductIsNullAndEnabledIsTrue();
}
