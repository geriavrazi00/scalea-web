package com.scalea.services;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.scalea.entities.Configuration;
import com.scalea.repositories.ConfigurationRepository;

@Service
@Transactional
public class ConfigurationService {

	private ConfigurationRepository configRepo;

	@Autowired
	public ConfigurationService(ConfigurationRepository configRepo) {
		this.configRepo = configRepo;
	}

	public String findValueByName(String name) throws Exception {
		Optional<Configuration> config = this.configRepo.findByName(name);
		
		if (config.isPresent()) {
			return config.get().getValue();
		}
		
		throw new Exception("There is a problem with the configuration of the project. The '" + name + "' parameter does not exit.");
	}

}
