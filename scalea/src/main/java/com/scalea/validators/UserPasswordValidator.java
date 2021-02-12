package com.scalea.validators;

import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import com.scalea.entities.User;

public class UserPasswordValidator implements Validator {

	@Override
	public boolean supports(Class<?> clazz) {
		return User.class.equals(clazz);
	}

	@Override
	public void validate(Object target, Errors errors) {
		// ValidationUtils.rejectIfEmpty(e, "name", "name.empty");
		User u = (User) target;
		
        if (u.getConfirmPassword() == null || u.getConfirmPassword().isEmpty()) {
        	ValidationUtils.rejectIfEmpty(errors, "confirmPassword", "confirmPassword.empty");
        } 
        
        if (!u.getConfirmPassword().equals(u.getPassword())) {
        	errors.rejectValue("confirmPassword", "passwords.do.not.match");
        }
	}

}
