package de.dema.pd3.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by Ronny on 22.03.2017.
 */
@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = PLZValidator.class)
public @interface PLZ {
    String message() default "PLZ not valid";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
