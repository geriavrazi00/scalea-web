package com.scalea.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

import com.scalea.validators.UserUniqueValidator;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Documented
@Target({ElementType.METHOD, ElementType.FIELD})
@Constraint(validatedBy = UserUniqueValidator.class)
@Retention(RUNTIME)
public @interface Unique {
    String message();
    Class<?>[] groups() default { };
    Class<? extends Payload>[] payload() default { };
}
