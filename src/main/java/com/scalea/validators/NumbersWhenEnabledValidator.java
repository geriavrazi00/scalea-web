package com.scalea.validators;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.springframework.beans.BeanWrapperImpl;

import com.scalea.annotations.NumbersWhenEnabled;

public class NumbersWhenEnabledValidator implements ConstraintValidator<NumbersWhenEnabled, Object> {

	private String enabled;
    private String number;
    
    public void initialize(NumbersWhenEnabled constraintAnnotation) {
        this.enabled = constraintAnnotation.enabled();
        this.number = constraintAnnotation.number();
    }
    
	@Override
	public boolean isValid(Object value, ConstraintValidatorContext context) {
		boolean enabled = (boolean) new BeanWrapperImpl(value).getPropertyValue(this.enabled);
		double number = (double) new BeanWrapperImpl(value).getPropertyValue(this.number);
		
		if (!enabled) {
			if (number <= 0) {
				context.disableDefaultConstraintViolation();
	        	context.buildConstraintViolationWithTemplate(context.getDefaultConstraintMessageTemplate())
	            	.addPropertyNode("price" ).addConstraintViolation();
				
				return false;
			}
		}
		
		return true;
	}

}
