package de.dema.pd3.validation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;
import javax.validation.ReportAsSingleViolation;

@Target({ElementType.TYPE, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = MatchingFieldsValidator.class)
@ReportAsSingleViolation
@Documented
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
