package com.scalea.repositories;

import org.springframework.data.repository.CrudRepository;

import com.scalea.entities.Activity;

public interface ActivityRepository extends CrudRepository<Activity, Long> {

}
