package com.scalea.services;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.scalea.entities.Area;
import com.scalea.exceptions.GenericException;
import com.scalea.repositories.AreaRepository;

@Service
public class AreaService {
	
	private AreaRepository areaRepo;

	@Autowired
	public AreaService(AreaRepository areaRepo) {
		this.areaRepo = areaRepo;
	}

	public Area updateById(long id, Area editedArea) throws GenericException {
		Optional<Area> optionalArea = areaRepo.findById(id);
		
		if (!optionalArea.isPresent()) throw new GenericException("Area not found");
		Area area = optionalArea.get();
		area.setName(editedArea.getName());
		area.setCapacity(editedArea.getCapacity());
		area.setEnabled(editedArea.isEnabled());
		areaRepo.save(area);
		
		return area;
	}
}
