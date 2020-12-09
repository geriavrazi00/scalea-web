package com.scalea.validators;

import java.util.Optional;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.hibernate.validator.constraintvalidation.HibernateConstraintValidatorContext;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.factory.annotation.Autowired;

import com.scalea.annotations.UniqueAreaName;
import com.scalea.entities.Area;
import com.scalea.repositories.AreaRepository;

public class UniqueAreaNameValidator implements ConstraintValidator<UniqueAreaName, Object> {
	
	@Autowired
    private AreaRepository areaRepo;
	
	private String id;
	private String name;
	
	public void initialize(UniqueAreaName constraintAnnotation) {
		this.id = constraintAnnotation.id();
        this.name = constraintAnnotation.name();
    }
	
	/*
	 * When the areaId is null, it means we are creating a new area. If it has a value, it means an existing area is being updated.
	 */
	@Override
	public boolean isValid(Object value, ConstraintValidatorContext context) {
		Object areaId = new BeanWrapperImpl(value).getPropertyValue(id);
		Object areaName = new BeanWrapperImpl(value).getPropertyValue(name);
		boolean isValid = false;
		
		if (areaId == null) {
			if (areaRepo != null && areaName != null && areaRepo.existsByName((String) areaName)) {
				isValid = false;
			} else {
				isValid = true;
			}
		} else {
			if (!(areaId instanceof Long)) isValid = false;
			else {
				if (areaRepo != null) {
					Long id = (Long) areaId;
					Optional<Area> area = areaRepo.findById(id);
					
					if (area.isPresent() && areaName != null) {
						if (area.get().getName().equals((String) areaName)) {
							isValid = true;
						} else if (!areaRepo.existsByName((String) areaName)) {
							isValid = true;
						} else {
							isValid = false;
						}
					} else {
						isValid = false;
					}
				}
			}
		}
		
		if(!isValid) {
        	context.disableDefaultConstraintViolation();
        	context.buildConstraintViolationWithTemplate(context.getDefaultConstraintMessageTemplate()).addPropertyNode("name" ).addConstraintViolation();
       }
		
		return isValid;
	}

}
