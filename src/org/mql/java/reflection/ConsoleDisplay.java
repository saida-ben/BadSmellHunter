package org.mql.java.reflection;

import org.mql.java.models.*;

public class ConsoleDisplay {

    public static void displayProjectInfo(Project project) {
        System.out.println("Project: " + project.getName());

        for (PackageInfo pkg : project.getPackages()) {
            System.out.println("Package: " + pkg.getName());

            for (ClassInfo cls : pkg.getClasses()) {
                System.out.println("Class: " + cls.getName());
                System.out.println("Superclass: " + (cls.getSuperclass() != null ? cls.getSuperclass() : "null"));
                System.out.println("Interfaces: " + (cls.getInterfaces().isEmpty() ? "[]" : cls.getInterfaces()));

                System.out.println("Fields: ");
                if (cls.getFields().isEmpty()) {
                    System.out.println("None");
                } else {
                    for (FieldModel f : cls.getFields()) {
                        System.out.println("" + f.getName() + ": " + f.getType());
                    }
                }

                System.out.println("Methods: ");
                if (cls.getMethods().isEmpty()) {
                    System.out.println("None");
                } else {
                    for (MethodInfo m : cls.getMethods()) {
                        String params = m.getParameters().isEmpty() ? "[]" : m.getParameters().toString();
                        System.out.println(" " + m.getName() + "(" + params + "): " + m.getReturnType());
                    }
                }
                System.out.println("Annotations: " + (cls.getAnnotations().isEmpty() ? "[]" : cls.getAnnotations()));

                System.out.println("Relations: ");
                if (cls.getRelations().isEmpty()) { 
                    System.out.println("None");
                } else {
                    for (Relation relation : cls.getRelations()) { 
                        System.out.println(" " + relation.toString()); 
                    }
                }
            }

            System.out.println();
        }
    }
}
