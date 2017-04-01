package de.dema.pd3.model.events;

/**
 * Created by Ronny on 26.03.2017.
 */
public enum EventTypes {

    USER_MESSAGE(1, "USR MSG");

    private Integer id;
    private String name;

    EventTypes(Integer id, String name) {
        this.id = id;
        this.name = name;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public static EventTypes fromId(Integer id) {
        for (EventTypes eventTypes : values()) {
            if (eventTypes.getId() == id) {
                return eventTypes;
            }
        }
        throw new IllegalArgumentException("Unknown EventTypes: " + id);
    }

}
