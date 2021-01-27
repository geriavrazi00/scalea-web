package com.scalea.repositories;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import com.scalea.entities.Configuration;

public interface ConfigurationRepository extends CrudRepository<Configuration, Long> {
	Optional<Configuration> findByName(String name);
}
