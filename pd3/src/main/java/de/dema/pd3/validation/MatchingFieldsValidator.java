package de.dema.pd3.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import java.lang.reflect.Field;

public class MatchingFieldsValidator implements ConstraintValidator<MatchingFields, Object> {

    private String firstFieldName;
    private String secondFieldName;

    public void initialize(final MatchingFields annot) {
        firstFieldName = annot.first();
        secondFieldName = annot.second();
    }

    public boolean isValid(final Object value, final ConstraintValidatorContext context) {
        final Object firstObj = getBeanProperty(value, firstFieldName);
        final Object secondObj = getBeanProperty(value, secondFieldName);
        return (firstObj == null && secondObj == null) || (firstObj != null && firstObj.equals(secondObj));
    }

    private Object getBeanProperty(Object value, String fieldname) {
        try {
            Field field = value.getClass().getDeclaredField(fieldname);
            field.setAccessible(true);
            return field.get(value);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new IllegalArgumentException(e);
        }
    }
    
}
