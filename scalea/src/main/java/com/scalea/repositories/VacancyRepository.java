package com.scalea.repositories;

import org.springframework.data.repository.CrudRepository;

import com.scalea.entities.Vacancy;

public interface VacancyRepository extends CrudRepository<Vacancy, Long> {

}
