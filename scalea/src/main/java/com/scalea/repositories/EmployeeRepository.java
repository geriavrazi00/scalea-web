package com.scalea.repositories;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.scalea.entities.Employee;

public interface EmployeeRepository extends CrudRepository<Employee, Long> {
	boolean existsByPersonalNumber(String personalNumber);
	Iterable<Employee> findByEnabled(boolean enabled);
	List<Employee> findByVacancyIsNullAndEnabledIsTrue();
}
