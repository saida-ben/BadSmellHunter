package org.mql.java.xml;

import org.mql.java.models.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.*;

public class XMLParser {

    public Project parse(String filePath) {
        try {
            // Préparer le parseur DOM
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(filePath);

            // Récupérer l'élément racine (le projet)
            Element root = document.getDocumentElement();
            Project project = new Project(root.getElementsByTagName("ProjectName").item(0).getTextContent());

            // Récupérer les packages
            NodeList packageNodes = root.getElementsByTagName("Package");
            for (int i = 0; i < packageNodes.getLength(); i++) {
                Element packageElement = (Element) packageNodes.item(i);
                PackageInfo packageInfo = parsePackage(packageElement);
                project.addPackage(packageInfo);
            }

            // Afficher le modèle chargé
            displayModel(project);

            return project;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private PackageInfo parsePackage(Element packageElement) {
        String packageName = packageElement.getAttribute("name");
        PackageInfo packageInfo = new PackageInfo(packageName);

        // Récupérer les classes du package
        NodeList classNodes = packageElement.getElementsByTagName("Class");
        for (int i = 0; i < classNodes.getLength(); i++) {
            Element classElement = (Element) classNodes.item(i);
            ClassInfo classInfo = parseClass(classElement);
            packageInfo.addClass(classInfo);
        }

        return packageInfo;
    }

    private ClassInfo parseClass(Element classElement) {
        String className = classElement.getAttribute("name");
        ClassInfo classInfo = new ClassInfo(className, false, false);

        // Récupérer la classe parente
        String superclass = classElement.getAttribute("superclass");
        if (!superclass.isEmpty()) {
            classInfo.setSuperclass(superclass);
        }

        // Récupérer les interfaces
        NodeList interfaceNodes = classElement.getElementsByTagName("Interface");
        for (int i = 0; i < interfaceNodes.getLength(); i++) {
            Element interfaceElement = (Element) interfaceNodes.item(i);
            classInfo.addInterface(interfaceElement.getTextContent());
        }

        // Récupérer les méthodes
        NodeList methodNodes = classElement.getElementsByTagName("Method");
        for (int i = 0; i < methodNodes.getLength(); i++) {
            Element methodElement = (Element) methodNodes.item(i);
            MethodInfo methodInfo = parseMethod(methodElement);
            classInfo.addMethod(methodInfo);
        }

        // Récupérer les relations
        NodeList relationNodes = classElement.getElementsByTagName("Relation");
        for (int i = 0; i < relationNodes.getLength(); i++) {
            Element relationElement = (Element) relationNodes.item(i);
            String relationType = relationElement.getAttribute("type");
            String relatedClass = relationElement.getAttribute("relatedClass");
            classInfo.addRelation(new Relation(classInfo.getName(), relatedClass, relationType));
        }

        return classInfo;
    }

    private MethodInfo parseMethod(Element methodElement) {
        String methodName = methodElement.getAttribute("name");
        String returnType = methodElement.getTextContent();
        MethodInfo methodInfo = new MethodInfo(methodName, returnType);

        return methodInfo;
    }

    // Afficher le modèle chargé en mémoire
    public void displayModel(Project project) {
        System.out.println("Project: " + project.getName());
        for (PackageInfo packageInfo : project.getPackages()) {
            System.out.println("  Package: " + packageInfo.getName());
            for (ClassInfo classInfo : packageInfo.getClasses()) {
                System.out.println("    Class: " + classInfo.getName());
                System.out.println("      Superclass: " + classInfo.getSuperclass());
                System.out.print("      Interfaces: ");
                for (String iface : classInfo.getInterfaces()) {
                    System.out.print(iface + " ");
                }
                System.out.println();

                System.out.println("      Methods: ");
                for (MethodInfo method : classInfo.getMethods()) {
                    System.out.println("        " + method.getName() + " : " + method.getReturnType());
                }

                System.out.println("      Relations: ");
                for (Relation relation : classInfo.getRelations()) {
                    System.out.println("        " + relation);
                }
            }
        }
    }
}