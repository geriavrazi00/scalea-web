package com.scalea.repositories;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;

import com.scalea.entities.Product;

public interface ProductRepository extends CrudRepository<Product, Long> {
	Iterable<Product> findByEnabled(boolean enabled);
	Page<Product> findByFatherProductIsNullAndEnabledIsTrueOrderByName(Pageable pageable);
	Iterable<Product> findByEnabledIsTrueAndWithSubProductsIsFalseOrderByName();
	Iterable<Product> findByFatherProductIsNullAndEnabledIsTrueOrderByName();
	Optional<Product> findByIdAndFatherProductIsNullAndEnabledIsTrue(Long id);
	Optional<Product> findByIdAndFatherProductAndEnabledIsTrue(Long id, Product fatherProduct);
}
