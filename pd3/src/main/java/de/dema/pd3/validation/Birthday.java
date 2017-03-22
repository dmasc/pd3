package de.dema.pd3.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Validates that user is at least 16
 * Created by Ronny on 22.03.2017.
 */
@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = BirthdayValidator.class)
public @interface Birthday {
    String message() default "Birthday not valid";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
