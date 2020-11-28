package com.scalea.annotations;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.springframework.beans.factory.annotation.Autowired;

import com.scalea.repositories.RoleRepository;

public class UserUniqueValidator implements ConstraintValidator<Unique, String> {

    @Autowired
    private RoleRepository roleRepo;

    @Override
    public void initialize(Unique unique) {
        unique.message();
    }

    @Override
    public boolean isValid(String name, ConstraintValidatorContext context) {
        if (roleRepo != null && roleRepo.existsByName(name)) {
            return false;
        }
        return true;
    }
}