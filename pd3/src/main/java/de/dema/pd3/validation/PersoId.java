package de.dema.pd3.validation;


import javax.validation.Constraint;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

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