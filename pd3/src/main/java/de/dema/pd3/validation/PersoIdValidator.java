package de.dema.pd3.validation;


import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Validate Personalausweisnummer
 * Created by Ronny on 22.03.2017.
 */
public class PersoIdValidator implements ConstraintValidator<PersoId, String> {

    private static final int[] CHECKSUM_MULITPLICATORS = new int[] {7,3,1,7,3,1,7,3,1};

    // group(1) -> Behörde, group(2) -> id, group(3) -> prüfsumme, D (optional, alter ausweis)
    // Prüfziffer multiplikatoren: 7 3 1 7 3 1 7 3 1
    private static final Pattern PERSO_NUMMER_PATTERN =
            Pattern.compile("([0-9A-Z]{4})([0-9A-Z]{5})(\\d{1})D*");

    @Override
    public void initialize(PersoId persoId) {
        // no op
    }

    @Override
    public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {
        Matcher m = null;
        if ((m = PERSO_NUMMER_PATTERN.matcher(s)).find()) {
            String checkSum = m.group(3);
            return checkSum.equals(calculateChecksum(m.group(1),  m.group(2)));
        }
        return false;
    }

    private String calculateChecksum(String behoerde, String id) {
        int value = 0;
        String chkStr = behoerde + id;
        for (int i = 0; i < chkStr.length(); i++) {
            value += transformDecAsciiToNumber(chkStr.charAt(i)) * CHECKSUM_MULITPLICATORS[i];
        }
        String result = String.valueOf(value);
        return "" + result.charAt((result.length() - 1));
    }

    private int transformDecAsciiToNumber(int in) {
        if (in > 64) {
            // ( "A" (dec: 65) - 55 = 10) ...
            return (in - 55);
        } else {
            // ( "1" (dec: 49) - 48 = 1) ...
            return (in - 48);
        }
    }

}