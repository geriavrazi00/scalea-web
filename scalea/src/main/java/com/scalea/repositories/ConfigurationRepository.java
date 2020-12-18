package com.scalea.repositories;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.scalea.entities.Configuration;

public interface ConfigurationRepository extends CrudRepository<Configuration, Long> {
	List<Configuration> findByName(String name);
}
