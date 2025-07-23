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
        Relation inheritance = new Relation(clazz.getSimpleName(), clazz.getSuperclass().getSimpleName(), "Inheritance");
        classInfo.addRelation(inheritance);
    }

    // Interfaces implémentées
    for (Class<?> iface : clazz.getInterfaces()) {
        classInfo.addInterface(iface.getSimpleName());
        Relation implementsRel = new Relation(clazz.getSimpleName(), iface.getSimpleName(), "Implements");
        classInfo.addRelation(implementsRel);
    }

    // Attributs (champs) → Composition / Agrégation / Association
    for (Field field : clazz.getDeclaredFields()) {
        String fieldTypeName = field.getType().getSimpleName();
        String fullTypeName = field.getType().getName();

        // Ignorer types primitifs et types système
        if (field.getType().isPrimitive() || fullTypeName.startsWith("java.lang")) {
            classInfo.addField(new FieldModel(field.getName(), fieldTypeName));
            continue;
        }

        // Agrégation (Collection<T>)
        if (Collection.class.isAssignableFrom(field.getType())) {
            Type genericType = field.getGenericType();
            if (genericType instanceof ParameterizedType) {
                ParameterizedType pt = (ParameterizedType) genericType;
                Type[] argTypes = pt.getActualTypeArguments();
                if (argTypes.length > 0 && argTypes[0] instanceof Class<?>) {
                    Class<?> genericClass = (Class<?>) argTypes[0];
                    Relation aggregation = new Relation(clazz.getSimpleName(), genericClass.getSimpleName(), "Aggregation");
                    classInfo.addRelation(aggregation);
                }
            }
        } else {
            // Composition ou association
            Relation relation = new Relation(clazz.getSimpleName(), fieldTypeName, "Association");

            // Tentative simple de distinguer composition
            if (!Modifier.isStatic(field.getModifiers())) {
                relation = new Relation(clazz.getSimpleName(), fieldTypeName, "Composition");
            }

            classInfo.addRelation(relation);
        }

        classInfo.addField(new FieldModel(field.getName(), fieldTypeName));
    }

    // Méthodes → Utilisation en paramètre ou retour
    for (Method method : clazz.getDeclaredMethods()) {
        MethodInfo methodInfo = new MethodInfo(method.getName(), method.getReturnType().getSimpleName());

        // Paramètres
        for (Parameter param : method.getParameters()) {
            String paramType = param.getType().getSimpleName();
            String fullParamType = param.getType().getName();

            methodInfo.addParameter(new ParameterInfo(
            	    param.isNamePresent() ? param.getName() : param.getType().getSimpleName().toLowerCase(),
            	    param.getType().getSimpleName()));

            if (!param.getType().isPrimitive() && !fullParamType.startsWith("java.")) {
                Relation useRelation = new Relation(clazz.getSimpleName(), paramType, "Uses");
                classInfo.addRelation(useRelation);
            }
        }

        // Type de retour
        Class<?> returnType = method.getReturnType();
        if (!returnType.isPrimitive() && !returnType.getName().startsWith("java.")) {
            Relation returnRelation = new Relation(clazz.getSimpleName(), returnType.getSimpleName(), "Uses");
            classInfo.addRelation(returnRelation);
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
