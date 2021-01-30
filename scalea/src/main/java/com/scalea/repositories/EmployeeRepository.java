package com.scalea.repositories;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;

import com.scalea.entities.Employee;

public interface EmployeeRepository extends CrudRepository<Employee, Long> {
	boolean existsByPersonalNumber(String personalNumber);
	Page<Employee> findByEnabledOrderByFirstName(boolean enabled, Pageable pageable);
	List<Employee> findByVacancyIsNullAndEnabledIsTrue();
}
