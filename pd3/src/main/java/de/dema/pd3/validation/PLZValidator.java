package de.dema.pd3.validation;


import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * Validate Postleitzahl
 * Created by Ronny on 22.03.2017.
 */
public class PLZValidator implements ConstraintValidator<PLZ, String> {


    @Override
    public void initialize(PLZ plz) {
        // no op
    }

    @Override
    public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {
        if (s != null && s.trim().length() == 5) {
            try {
                Integer.parseInt(s);
                return true;
            } catch (NumberFormatException nfe) {
                // fall through to return false
            }

        }
        return false;
    }
}