package com.scalea.repositories;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.scalea.entities.Activity;
import com.scalea.entities.Vacancy;
import com.scalea.models.dto.ActivityAggregatorDTO;

public interface ActivityRepository extends CrudRepository<Activity, Long> {
	
	@Query("SELECT new com.scalea.models.dto.ActivityAggregatorDTO(SUM(a.weight * a.product.price), a.employee) "
	  + "FROM Activity a "
	  + "WHERE MONTH(a.createdAt) = ?1 "
	  + "AND YEAR(a.createdAt) = ?2 "
	  + "GROUP BY a.employee ")
	Page<ActivityAggregatorDTO> findSumOfWeightForProductAndVacancy(int month, int year, Pageable pageable);
	
	@Query("SELECT new com.scalea.models.dto.ActivityAggregatorDTO(SUM(a.weight * a.product.price), a.employee) "
	  + "FROM Activity a "
	  + "WHERE MONTH(a.createdAt) = ?1 "
	  + "AND YEAR(a.createdAt) = ?2 "
	  + "AND a.employee.id = ?3 "
	  + "GROUP BY a.employee ")
	Optional<ActivityAggregatorDTO> findActivityAggByIds(Integer month, Integer year, Long employeeId);
	
	@Query("SELECT DISTINCT YEAR(a.createdAt) FROM Activity a")
	Iterable<Integer> findYearsWithData();
	
	boolean existsByVacancyAndWeightAndTimestamp(Vacancy vacancy, double weight, String timestamp);
}
