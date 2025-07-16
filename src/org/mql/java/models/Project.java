package org.mql.java.models;

import java.util.ArrayList;
import java.util.List;

public class Project {
    private String name;
    private List<PackageInfo> packages;
    private List<ClassInfo> classes;

    public Project(String name) {
        this.name = name;
        this.packages = new ArrayList<>();
    }

    public void addPackage(PackageInfo pkg) {
        packages.add(pkg);
    }

    public String getName() {
        return name;
    }

    public List<PackageInfo> getPackages() {
        return packages;
    }
    public void printRelations() {
        for (ClassInfo classInfo : classes) {
            System.out.println("Relations pour la classe " + classInfo.getClass().getName() + ": ");
            for (MethodInfo method : classInfo.getMethods()) {
                System.out.println("- " + method.getClass().getName());
            }
        }
    }
}
