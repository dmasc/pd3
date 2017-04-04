package de.dema.pd3.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import javax.validation.constraints.Pattern;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Validates that user is at least 16
 * Created by Ronny on 22.03.2017.
 */
@Pattern(message = "{register_user_model.birthday.format}", regexp = "\\d{2}\\.\\d{2}\\.\\d{4}")
@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = AgeValidator.class)
public @interface Age {
    int minAge() default 16;
    int maxAge() default -1;
    String message() default "Age not valid";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
