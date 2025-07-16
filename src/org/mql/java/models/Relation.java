package org.mql.java.models;

public class Relation {
    private String source;
    private String target;
    private String type;
    private String label;


    public Relation(String source, String target, String type) {
        this.source = source;
        this.target = target;
        this.type = type;
        this.label = "";
    }

    public String getSource() {
        return source;
    }

    public String getTarget() {
        return target;
    }

    public String getType() {
        return type;
    }

    public String getLabel() {
        return label; 
    }

    @Override  
    public String toString() {
        return source + " -> " + target + " (" + type + ")";
    }
}
