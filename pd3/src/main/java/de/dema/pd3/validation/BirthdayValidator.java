package de.dema.pd3.validation;


import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDate;
import java.time.Period;

/**
 * Validates brithday
 * Created by Ronny on 22.03.2017.
 */
public class BirthdayValidator implements ConstraintValidator<Birthday, LocalDate> {


    @Override
    public void initialize(Birthday plz) {
        // no op
    }

    @Override
    public boolean isValid(LocalDate date, ConstraintValidatorContext constraintValidatorContext) {
        return date != null && Period.between(date, LocalDate.now()).getYears() >= 16;
    }
}