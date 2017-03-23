package de.dema.pd3.action;

import java.io.Serializable;
import java.util.Map;

/**
 * Created by Ronny on 23.03.2017.
 */
public interface Action extends Serializable {
    String shortDescritpion();

    String description();

    Map<String, Object> getParameters();

    Outcome doAction(Map<String, Object> parameter);
}
