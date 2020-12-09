package com.scalea.repositories;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.scalea.entities.Area;
import com.scalea.entities.Vacancy;

public interface VacancyRepository extends CrudRepository<Vacancy, Long> {
	
	@Query("SELECT MAX(v.number) FROM Vacancy v WHERE v.area = ?1 ")
	int findMaxVacancyNumberOfArea(Area area);
}
