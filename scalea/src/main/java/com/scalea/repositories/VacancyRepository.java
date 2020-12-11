package com.scalea.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.scalea.entities.Area;
import com.scalea.entities.Vacancy;

public interface VacancyRepository extends CrudRepository<Vacancy, Long> {
	
	@Query("SELECT MAX(v.number) FROM Vacancy v WHERE v.area = ?1 ")
	int findMaxVacancyNumberOfArea(Area area);
	
	@Query("SELECT v FROM Vacancy v WHERE v.employee = NULL ")
	List<Vacancy> findUnassociatedVacancies();
}
