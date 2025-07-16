package org.mql.java.models;

import java.util.ArrayList;
import java.util.List;

public class PackageInfo {
    private String name;
    private List<ClassInfo> classes; // List of class information
    private List<Relation> relations; // List of relations

    public PackageInfo(String name) {
        this.name = name;
        this.classes = classes != null ? classes : new ArrayList<>();
        this.relations = relations != null ? relations : new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public List<ClassInfo> getClasses() {
        return classes;
    }

    public void addClass(ClassInfo classInfo) {
        if (classes == null) {
            classes = new ArrayList<>();
        }
        classes.add(classInfo);
    }

    public List<Relation> getRelations() {
        return relations;
    }

    public void addRelation(Relation relation) {
        if (relations == null) {
            relations = new ArrayList<>();
        }
        relations.add(relation);
    }

    @Override
    public String toString() {
        return "PackageInfo{" +
               "name='" + name + '\'' +
               ", classes=" + classes +
               ", relations=" + relations +
               '}';
    }
}
