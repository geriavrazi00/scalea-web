package com.scalea.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.scalea.entities.Area;
import com.scalea.entities.Vacancy;

public interface VacancyRepository extends CrudRepository<Vacancy, Long> {
	
	@Query("SELECT MAX(v.number) FROM Vacancy v WHERE v.area = ?1 ")
	int findMaxVacancyNumberOfArea(Area area);
	
	@Query("SELECT v FROM Vacancy v WHERE v.employee = NULL AND v.area.enabled = true ORDER BY LENGTH(v.area.name), v.area.name, v.number")
	List<Vacancy> findUnassociatedVacancies();
	
	Iterable<Vacancy> findByEnabled(boolean enabled);
	
	Optional<Vacancy> findByUuid(String uuid);
	
	Page<Vacancy> findByAreaAndEnabledOrderByNumber(Area area, boolean enabled, Pageable pageable);
	
	boolean existsByIdAndArea(Long id, Area area);
	
	Iterable<Vacancy> findByAreaAndEnabledOrderByNumber(Area area, boolean enabled);
}
