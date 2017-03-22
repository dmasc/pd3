package de.dema.pd3.validation;


import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Validate Postleitzahl
 * Created by Ronny on 22.03.2017.
 */
public class PersoIdValidator implements ConstraintValidator<PersoId, String> {

    // group(1) -> Behörde, group(2) -> id, group(3) -> prüfsumme
    private static final Pattern PERSO_NUMMER_PATTERN =
            Pattern.compile("(\\[0-9A-Z]{4})(\\[0-9A-Z]{5})(\\d{1})");

    @Override
    public void initialize(PersoId persoId) {
        // no op
    }

    @Override
    public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {
        Matcher m = null;
        if (!(m = PERSO_NUMMER_PATTERN.matcher(s)).find()) {
            // TODO: calculate and check the checksum!
        }
        return false;
    }
}