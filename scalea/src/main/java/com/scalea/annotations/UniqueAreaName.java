package com.scalea.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

import com.scalea.validators.UniqueAreaNameValidator;

@Documented
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = UniqueAreaNameValidator.class)
public @interface UniqueAreaName {
	String message();
	
	String id();
	
	String name();
	
    Class<?>[] groups() default { };
    Class<? extends Payload>[] payload() default { };
    
    @Target({ ElementType.TYPE })
    @Retention(RetentionPolicy.RUNTIME)
    @interface List {
    	UniqueAreaName[] value();
    }
}
