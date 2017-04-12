package de.dema.pd3.validation;

import java.time.LocalDate;
import java.time.Period;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import de.dema.pd3.Clock;

/**
 * Validates birthday
 */
public class AgeValidator implements ConstraintValidator<Age, String> {

    private int minAge;
    private int maxAge;

    @Override
    public void initialize(Age age) {
        minAge = age.minAge();
        maxAge = age.maxAge() > -1 ? age.maxAge() : Integer.MAX_VALUE;
    }

    @Override
    public boolean isValid(String date, ConstraintValidatorContext constraintValidatorContext) {
        try {
            LocalDate localDate = LocalDate.parse(date);
            int age = Period.between(localDate, Clock.today()).getYears();
            return age >= minAge && age <= maxAge;
        } catch (Exception e) {
            // ignore, format check via pattern...
            return true;
        }
    }
}