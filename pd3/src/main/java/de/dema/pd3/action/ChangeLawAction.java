package de.dema.pd3.action;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Ronny on 23.03.2017.
 */
public class ChangeLawAction implements Action {

    @Override
    public String shortDescritpion() {
        return "Eine Gesetzesänderung anstreben";
    }

    @Override
    public String description() {
        return "Wählen Sie diese Aktion um eine Gesetzesänderung vorzuschlagen";
    }

    @Override
    public Map<String, Object> getParameters() {
        return new HashMap<>();
    }

    @Override
    public Outcome doAction(Map<String, Object> parameter) {
        return Outcome.success("Gesetzesänderung eingeleitet!");
    }
}
