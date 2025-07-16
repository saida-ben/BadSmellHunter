package org.mql.java.reflection;

import java.io.File;
import java.lang.reflect.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.mql.java.models.*;

public class Extractor {

    public static Project extractProject(String projectPath) {
        Project project = new Project(projectPath);
        File projectDir = new File(projectPath);
        
        if (!projectDir.exists() || !projectDir.isDirectory()) {
            System.err.println("Invalid project path: " + projectPath);
            return project; 
        }

        File classDirectory = findClassDirectory(projectDir);
        if (classDirectory == null) {
            System.err.println("No class directory found in project path: " + projectPath);
            return project; 
        }

        Map<String, PackageInfo> packageMap = new HashMap<>();
        exploreDirectory(classDirectory, classDirectory.getAbsolutePath(), packageMap);

        for (PackageInfo packageInfo : packageMap.values()) {
            project.addPackage(packageInfo);
        }
        
        return project;
    }

    private static File findClassDirectory(File projectDir) {
        // Cherche un dossier 'bin' ou 'target/classes'
        File[] potentialDirs = projectDir.listFiles(File::isDirectory);
        for (File dir : potentialDirs) {
            if (dir.getName().equals("bin") || dir.getName().equals("target")) {
                // Vérifie si 'target' contient 'classes'
                File classesDir = new File(dir, "classes");
                if (classesDir.exists() && classesDir.isDirectory()) {
                    return classesDir;
                } else if (dir.getName().equals("bin")) {
                    return dir; 
                }
            }
        }
        return null; 
    }

   // Explore les répertoires pour trouver les fichiers .class
    private static void exploreDirectory(File directory, String baseDirectory, Map<String, PackageInfo> packageMap) {
        for (File file : directory.listFiles()) {
            if (file.isDirectory()) {
                exploreDirectory(file, baseDirectory, packageMap);
            } else if (file.getName().endsWith(".class")) {
                String qualifiedName = convertToQualifiedName(file.getAbsolutePath(), baseDirectory);
                System.out.println("Attempting to load class: " + qualifiedName);

                try {
                    File classDirectory = new File(baseDirectory);
                    URLClassLoader classLoader = new URLClassLoader(new URL[] { classDirectory.toURI().toURL() });

                    Class<?> clazz = classLoader.loadClass(qualifiedName);
                    ClassInfo classInfo = extractClass(clazz);

                    String packageName = clazz.getPackage().getName();
                    PackageInfo packageInfo = packageMap.getOrDefault(packageName, new PackageInfo(packageName));
                    packageInfo.addClass(classInfo);
                    packageMap.put(packageName, packageInfo);

                } catch (ClassNotFoundException e) {
                    System.err.println("Class not found: " + qualifiedName); 
                } catch (MalformedURLException e) {
                    System.err.println("URL error while loading class: " + qualifiedName); 
                }
            }
        }
    }


private static ClassInfo extractClass(Class<?> clazz) {
    ClassInfo classInfo = new ClassInfo(clazz.getSimpleName(), clazz.isInterface(), clazz.isEnum());

    // Héritage
    if (clazz.getSuperclass() != null && !clazz.getSuperclass().equals(Object.class)) {
        classInfo.setSuperclass(clazz.getSuperclass().getSimpleName());
        Relation inheritanceRelation = new Relation(clazz.getSimpleName(), clazz.getSuperclass().getSimpleName(), "Inheritance");
        classInfo.addRelation(inheritanceRelation);
    }

    // Implémentation d'interfaces
    for (Class<?> iface : clazz.getInterfaces()) {
        classInfo.addInterface(iface.getSimpleName());
        Relation interfaceRelation = new Relation(clazz.getSimpleName(), iface.getSimpleName(), "Implements");
        classInfo.addRelation(interfaceRelation);
    }

    // Champs pour association, agrégation et composition
    for (Field field : clazz.getDeclaredFields()) {
        String fieldType = field.getType().getSimpleName();

        // Vérifier si le champ est une collection (agrégation)
        if (Collection.class.isAssignableFrom(field.getType())) {
            // Agrégation
            ParameterizedType genericType = (ParameterizedType) field.getGenericType();
            Class<?> genericClass = (Class<?>) genericType.getActualTypeArguments()[0];
            Relation aggregationRelation = new Relation(clazz.getSimpleName(), genericClass.getSimpleName(), "Aggregation");
            classInfo.addRelation(aggregationRelation);
        } else if (!field.getType().isPrimitive() && !fieldType.equals("String")) {
            // Composition ou association simple
            if (field.getDeclaringClass().equals(clazz)) {
                // Si le champ est déclaré dans la classe actuelle, on considère cela comme une composition
                Relation compositionRelation = new Relation(clazz.getSimpleName(), fieldType, "Composition");
                classInfo.addRelation(compositionRelation);
            } else {
                // Sinon, c'est une association simple
                Relation associationRelation = new Relation(clazz.getSimpleName(), fieldType, "Association");
                classInfo.addRelation(associationRelation);
            }
        }

        classInfo.addField(new FieldModel(field.getName(), fieldType));
    }

    for (Method method : clazz.getDeclaredMethods()) {
        MethodInfo methodInfo = new MethodInfo(method.getName(), method.getReturnType().getSimpleName());
        for (Parameter param : method.getParameters()) {
            String paramName = param.getName();
            String paramType = param.getType().getSimpleName();
            methodInfo.addParameter(new ParameterInfo(paramName, paramType));
        }

        classInfo.addMethod(methodInfo);
    }

    return classInfo;
}




   private static String convertToQualifiedName(String filePath, String baseDirectory) {
	   
	    String relativePath = filePath.substring(baseDirectory.length() + 1);
	    // Remplace les séparateurs de fichier par des points et supprime l'extension .class
	    return relativePath.replace(File.separator, ".").replace(".class", "");
	    
	}

}
