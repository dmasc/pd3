package de.dema.pd3.validation;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

/**
 * Validate Postleitzahl
 * Created by Ronny on 22.03.2017.
 */
@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = PersoIdValidator.class)
public @interface PersoId {
    String message() default "ID not valid";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}