package de.dema.pd3.validation;


import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDate;
import java.time.Period;

/**
 * Validates brithday
 * Created by Ronny on 22.03.2017.
 */
public class AgeValidator implements ConstraintValidator<Age, LocalDate> {

    private int minAge;
    private int maxAge;

    @Override
    public void initialize(Age age) {
        minAge = age.minAge();
        maxAge = age.maxAge() > -1 ? age.maxAge() : Integer.MAX_VALUE;
    }

    @Override
    public boolean isValid(LocalDate date, ConstraintValidatorContext constraintValidatorContext) {
        if (date == null) {
            return false;
        } else {
            int age = Period.between(date, LocalDate.now()).getYears();
            return age >= minAge && age <= maxAge;
        }
    }
}