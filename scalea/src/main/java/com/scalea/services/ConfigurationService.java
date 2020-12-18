package com.scalea.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.scalea.entities.Configuration;
import com.scalea.repositories.ConfigurationRepository;

@Service
public class ConfigurationService {

	private ConfigurationRepository configRepo;

	@Autowired
	public ConfigurationService(ConfigurationRepository configRepo) {
		this.configRepo = configRepo;
	}

	public String findValueByName(String name) {
		List<Configuration> config = this.configRepo.findByName(name);
		
		if (config != null && config.size() == 1) {
			return config.get(0).getValue();
		}
		
		return null;
	}

}
