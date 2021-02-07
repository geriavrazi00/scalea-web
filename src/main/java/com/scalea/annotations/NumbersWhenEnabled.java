package com.scalea.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

import com.scalea.validators.NumbersWhenEnabledValidator;

@Constraint(validatedBy = NumbersWhenEnabledValidator.class)
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface NumbersWhenEnabled {
	String message();
	 
	String enabled();
 
	String number();
    
    Class<?>[] groups() default { };
    Class<? extends Payload>[] payload() default { };
 
    @Target({ ElementType.TYPE })
    @Retention(RetentionPolicy.RUNTIME)
    @interface List {
    	NumbersWhenEnabled[] value();
    }
}
