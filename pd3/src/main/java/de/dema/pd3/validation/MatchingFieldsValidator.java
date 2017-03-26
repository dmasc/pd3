package de.dema.pd3.validation;

import net.sf.ehcache.hibernate.management.impl.BeanUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * Created by Ronny on 23.03.2017.
 */
public class MatchingFieldsValidator implements ConstraintValidator<MatchingFields, Object> {

    private String firstFieldName;
    private String secondFieldName;

    public void initialize(final MatchingFields annot) {
        firstFieldName = annot.first();
        secondFieldName = annot.second();
    }

    public boolean isValid(final Object value, final ConstraintValidatorContext context) {
        final Object firstObj = BeanUtils.getBeanProperty(value, firstFieldName);
        final Object secondObj = BeanUtils.getBeanProperty(value, secondFieldName);
        return (firstObj == null || secondObj == null) || firstObj.equals(secondObj);
    }
}
