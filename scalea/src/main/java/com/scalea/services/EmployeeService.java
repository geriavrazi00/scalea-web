package com.scalea.services;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.scalea.entities.Employee;
import com.scalea.repositories.EmployeeRepository;

@Service
public class EmployeeService {
	private EmployeeRepository employeeRepo;
	
	@Autowired
	public EmployeeService(EmployeeRepository employeeRepo) {
		this.employeeRepo = employeeRepo;
	}
	
	public Optional<Employee> findById(Long id) {
		return employeeRepo.findById(id);
	}
	
	public Page<Employee> findByEnabledOrderByFirstName(boolean enabled, Pageable pageable) {
		return employeeRepo.findByEnabledOrderByFirstName(enabled, pageable);
	}
	
	public boolean existsByPersonalNumber(String personalNumber) {
		return employeeRepo.existsByPersonalNumber(personalNumber);
	}
	
	public Employee save(Employee employee) {
		return employeeRepo.save(employee);
	}
	
	public Iterable<Employee> findByVacancyIsNullAndEnabledIsTrue() {
		return employeeRepo.findByVacancyIsNullAndEnabledIsTrue();
	}
}
