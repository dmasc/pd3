package de.dema.pd3.action;

/**
 * Created by Ronny on 23.03.2017.
 */
public enum TypeOfAction {

    PARTEI_AKTION("Aktion",
            "Eine Partei Aktion starten",
            "Wählen Sie diese Aktion um eine bespielsweise eine Demonstration vorzuschlagen"),
    SATZUNG("Satzung",
            "Satzung oder Program ändern",
            "Wählen Sie diese Aktion um eine Änderung der Satzung oder des Parteiprogramms vorzuschlagen");
 //Lieber als jeweilige ActionDefinition anlegen und per reflection detectieren (Annotation nutzen?)
    private String id;
    private String shortDescritpion;
    private String description;

    TypeOfAction(String id, String shortDescritpion, String description) {
        this.id = id;
        this.shortDescritpion = shortDescritpion;
        this.description = description;
    }

    @Override
    public String toString() {
        return shortDescritpion;
    }

    public String getId() {
        return id;
    }

    public String getShortDescritpion() {
        return shortDescritpion;
    }

    public String getDescription() {
        return description;
    }
}
