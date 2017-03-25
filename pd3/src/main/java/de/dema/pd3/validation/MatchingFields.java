package de.dema.pd3.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import javax.validation.ReportAsSingleViolation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by Ronny on 23.03.2017.
 */
@Target({ElementType.TYPE, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = MatchingFieldsValidator.class)
@ReportAsSingleViolation
public @interface MatchingFields {

    String message() default "{fields.notequal}";

    /**
     * @return The first field
     */
    String first();

    /**
     * @return The second field
     */
    String second();

    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
