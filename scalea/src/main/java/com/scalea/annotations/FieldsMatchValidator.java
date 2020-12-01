package com.scalea.annotations;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.springframework.beans.BeanWrapperImpl;

public class FieldsMatchValidator implements ConstraintValidator<FieldsMatch, Object> {
	
	private String field;
    private String fieldVerify;
    
    public void initialize(FieldsMatch constraintAnnotation) {
        this.field = constraintAnnotation.field();
        this.fieldVerify = constraintAnnotation.fieldVerify();
    }
    
	@Override
	public boolean isValid(Object value, ConstraintValidatorContext context) {
		Object fieldValue = new BeanWrapperImpl(value).getPropertyValue(field);
		Object fieldMatchValue = new BeanWrapperImpl(value).getPropertyValue(fieldVerify);
		boolean isValid = false;
		        
        if (fieldValue != null) {
        	isValid = fieldValue.equals(fieldMatchValue);
        } else {
        	isValid = fieldMatchValue == null;
        }
        
        if(!isValid) {
        	context.disableDefaultConstraintViolation();
        	context.buildConstraintViolationWithTemplate(context.getDefaultConstraintMessageTemplate())
            	.addPropertyNode( "confirmPassword" ).addConstraintViolation();
       }
        
        return isValid;
	}
}
