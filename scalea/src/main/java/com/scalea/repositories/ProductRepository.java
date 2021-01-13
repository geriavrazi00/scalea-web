package com.scalea.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;

import com.scalea.entities.Product;

public interface ProductRepository extends CrudRepository<Product, Long> {
	Iterable<Product> findByEnabled(boolean enabled);
	Page<Product> findByFatherProductIsNullAndEnabledIsTrueOrderByName(Pageable pageable);
	Iterable<Product> findByEnabledIsTrueAndWithSubProductsIsFalse();
}
