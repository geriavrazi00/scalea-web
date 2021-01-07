package com.scalea.services;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
	
	public Page<Area> findPaginatedByEnabledOrderByName(Pageable pageable) {
        return areaRepo.findByEnabledOrderByName(true, pageable);
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
	
	public boolean existsByName(String name) {
		return areaRepo.existsByName(name);
	}
	
	public Iterable<Area> findByEnabled(boolean enabled) {
		return areaRepo.findByEnabled(true);
	}
	
	public Area save(Area area) {
		return areaRepo.save(area);
	}
	
	public Optional<Area> findById(long id) {
		return areaRepo.findById(id);
	}
	
	public void delete(Area area) {
		areaRepo.delete(area);
	}
}
