package org.mql.java.models;

import java.util.ArrayList;
import java.util.List;

public class ClassInfo {
    private String name;
    private String superclass;
    private boolean isInterface;
    private boolean isEnum;
    private List<String> interfaces;
    private List<FieldModel> fields;
    private List<MethodInfo> methods;
    private List<AnnotationInfo> annotations;
    private List<Relation> relations; 
 
    public ClassInfo(String name, boolean isInterface, boolean isEnum) {
        this.name = name;
        this.isInterface = isInterface;
        this.isEnum = isEnum;
        this.interfaces = new ArrayList<>();
        this.fields = new ArrayList<>();
        this.methods = new ArrayList<>();
        this.annotations = new ArrayList<>();
        this.relations = new ArrayList<>(); 

    }
  
    public void setSuperclass(String superclass) {
        this.superclass = superclass;
    }

    public void addInterface(String interfaceName) {
        interfaces.add(interfaceName);
    }

    public void addField(FieldModel field) {
        fields.add(field);
    }

    public void addMethod(MethodInfo method) {
        methods.add(method);
    }

    public void addAnnotation(AnnotationInfo annotation) {
        annotations.add(annotation);
    }

    public void addRelation(Relation relation) {
        if (this.relations == null) {
            this.relations = new ArrayList<>();
        }
        this.relations.add(relation);
    }


    public String getName() {
        return name;
    }

    public String getSuperclass() {
        return superclass;
    }

    public List<String> getInterfaces() {
        return interfaces;
    }

    public List<FieldModel> getFields() {
        return fields;
    }

    public List<MethodInfo> getMethods() {
        return methods;
    }

    public List<AnnotationInfo> getAnnotations() {
        return annotations;
    }

    public List<Relation> getRelations() { 
        return relations;
    }
}
