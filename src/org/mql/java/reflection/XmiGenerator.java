package org.mql.java.reflection;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import org.mql.java.models.*;

public class XmiGenerator {

    public static void generateXmi(Project project, String outputPath) throws IOException {
        File outputFile = new File(outputPath);

        // Vérifier si le fichier existe et le créer si nécessaire
        if (!outputFile.exists()) {
            outputFile.getParentFile().mkdirs();
            outputFile.createNewFile();
        }

        // Écrire le contenu XMI dans le fichier
        try (FileWriter writer = new FileWriter(outputFile)) {
            writer.write(generateXmiContent(project));
            System.out.println("Fichier XMI généré avec succès : " + outputFile.getAbsolutePath());
        }
    }

    private static String generateXmiContent(Project project) {
        StringBuilder xmiContent = new StringBuilder();

        // En-tête XMI
        xmiContent.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        xmiContent.append("<XMI xmi.version=\"2.1\" xmlns:xmi=\"http://www.omg.org/XMI\" xmlns:uml=\"http://www.omg.org/spec/UML/20090901\">\n");

        // Parcourir les packages
        for (PackageInfo pkg : project.getPackages()) {
            xmiContent.append("  <uml:Package name=\"").append(pkg.getName()).append("\">\n");

            // Parcourir les classes
            for (ClassInfo cls : pkg.getClasses()) {
                xmiContent.append("<uml:Class name=\"").append(cls.getName()).append("\">\n");

                // Ajouter les attributs
                for (FieldModel attr : cls.getFields()) {
                    xmiContent.append("<uml:Property name=\"").append(attr.getName())
                              .append("\" type=\"").append(attr.getType()).append("\"/>\n");
                }

                // Ajouter les méthodes
                for (MethodInfo method : cls.getMethods()) {
                    xmiContent.append("<uml:Operation name=\"").append(method.getName()).append("\">\n");

                    // Ajouter les paramètres de méthode
                    for (ParameterInfo param : method.getParameters()) {
                        xmiContent.append("<uml:Parameter name=\"").append(param.getName())
                                  .append("\" type=\"").append(param.getType()).append("\"/>\n");
                    }

                    xmiContent.append("</uml:Operation>\n");
                }

                xmiContent.append("</uml:Class>\n");
            }

            // Ajouter les relations
            for (Relation rel : pkg.getRelations()) {
                xmiContent.append("<uml:Association name=\"").append(rel.getType()).append("\">\n");
                xmiContent.append("<uml:MemberEnd name=\"").append(rel.getSource()).append("\"/>\n");
                xmiContent.append("<uml:MemberEnd name=\"").append(rel.getTarget()).append("\"/>\n");

                if (rel.getLabel() != null && !rel.getLabel().isEmpty()) {
                    xmiContent.append("<uml:Label value=\"").append(rel.getLabel()).append("\"/>\n");
                }

                xmiContent.append("</uml:Association>\n");
            }

            xmiContent.append("</uml:Package>\n");
        }

        xmiContent.append("</XMI>");
        return xmiContent.toString();
    }
}
