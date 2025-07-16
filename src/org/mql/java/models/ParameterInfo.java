package org.mql.java.models;


public class ParameterInfo {
    private String name;
    private String type;

    public ParameterInfo(String name, String type) {
        this.name = name;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }
}

