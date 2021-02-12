package com.scalea.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.scalea.entities.Area;
import com.scalea.entities.Vacancy;
import com.scalea.repositories.VacancyRepository;

@Service
@Transactional
public class VacancyService {
	private VacancyRepository vacancyRepo;

	@Autowired
	public VacancyService(VacancyRepository vacancyRepo) {
		this.vacancyRepo = vacancyRepo;
	}
	
	public int findMaxVacancyNumberOfArea(Area area) {
		return this.vacancyRepo.findMaxVacancyNumberOfArea(area);
	}
	
	public List<Vacancy> findUnassociatedVacancies() {
		return this.vacancyRepo.findUnassociatedVacancies();
	}
	
	public Iterable<Vacancy> findByEnabled(boolean enabled) {
		return this.vacancyRepo.findByEnabled(enabled);
	}
	
	public Optional<Vacancy> findByUuid(String uuid) {
		return this.vacancyRepo.findByUuid(uuid);
	}
	
	public Page<Vacancy> findByAreaAndEnabled(Area area, boolean enabled, Pageable pageable) {
		return this.vacancyRepo.findByAreaAndEnabledOrderByNumber(area, enabled, pageable);
	}
	
	public Vacancy save(Vacancy vacancy) {
		return this.vacancyRepo.save(vacancy);
	}
	
	public void delete(Vacancy vacancy) {
		this.vacancyRepo.save(vacancy);
	}
	
	public Optional<Vacancy> findById(Long id) {
		return this.vacancyRepo.findById(id);
	}
	
	public boolean existsByIdAndArea(Long id, Area area) {
		return this.vacancyRepo.existsByIdAndArea(id, area);
	}
}
