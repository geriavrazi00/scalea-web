package com.scalea.repositories;

import java.util.Date;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.scalea.entities.Activity;
import com.scalea.entities.Area;
import com.scalea.entities.Employee;
import com.scalea.entities.Vacancy;
import com.scalea.models.dto.ActivityAggregatorDTO;
import com.scalea.models.dto.DailyActivityDTO;
import com.scalea.models.dto.DailyActivityDetailDTO;

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
	
	boolean existsByVacancyAndWeightAndDate(Vacancy vacancy, double weight, Date date);
	
	@Query("SELECT SUM(a.weight) FROM Activity a")
	Double findSumOfWeightedAmount();
	
	@Query("SELECT new com.scalea.models.dto.DailyActivityDTO(COUNT(a.id), SUM(a.weight), a.employee) "
		+ "FROM Activity a "
		+ "WHERE a.area = ?1 AND DATE(a.date) = ?2 "
		+ "GROUP BY a.employee")
	Iterable<DailyActivityDTO> findActivityByAreaAndDate(Area area, Date date);
	
	@Query("SELECT new com.scalea.models.dto.DailyActivityDetailDTO(a.employee.firstName, a.employee.lastName, a.vacancy.number, a.product.name, a.weight, a.date) "
		+ "FROM Activity a "
		+ "WHERE a.area = ?1 AND DATE(a.date) = ?2 AND a.employee = ?3 "
		+ "ORDER BY a.date")
	Iterable<DailyActivityDetailDTO> findActivityByAreaAndDateAndEmployee(Area area, Date date, Employee employee);
	
	@Query("SELECT a "
		+ "FROM Activity a "
		+ "WHERE a.area = ?1 AND DATE(a.date) = ?2 "
		+ "ORDER BY a.employee.firstName, a.employee.lastName, a.date")
	Iterable<Activity> findAllActivityByAreaAndDate(Area area, Date date);
}
