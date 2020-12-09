package com.scalea.repositories;

import org.springframework.data.repository.CrudRepository;

import com.scalea.entities.Employee;

public interface EmployeeRepository extends CrudRepository<Employee, Long> {

}
