package org.mql.java.examples;

import java.io.IOException;
import java.util.List;
import javax.swing.SwingUtilities;
import org.mql.java.models.*;
import org.mql.java.output.ConsoleDisplay;
import org.mql.java.output.JsonGenerator;
import org.mql.java.output.XMLParser;
import org.mql.java.output.XMLWriter;
import org.mql.java.output.XmiGenerator;
import org.mql.java.reflection.Extractor;
import org.mql.java.reflection.SourceCodeAnalyzer;
import org.mql.java.ui.UMLDiagramViewer2;
import org.mql.java.analysis.BadSmellDetector2;

public class Client {

    public Client() {
        // Option 1: Lancer le viewer avec interface de recherche
        launchInteractiveViewer();
        
        // Option 2: Lancer le traitement automatique (commenté pour l'exemple)
        // exp01();
    }

    private void launchInteractiveViewer() {
        SwingUtilities.invokeLater(() -> {
            new UMLDiagramViewer2(); // Lance le viewer vide avec interface de recherche
        });
    }

    private void exp01() {
        String projectPath = "C:\\Users\\benza\\Documents\\eclipse_workspce\\p01-revisison";
        String xmlFilePath = "C:\\Users\\benza\\Documents\\eclipse_workspce\\prj_reflexion\\ressources\\output.xml";
        String jsonFilePath = "C:\\Users\\benza\\Documents\\eclipse_workspce\\prj_reflexion\\ressources\\output.json";
        String outputXmiPath = "C:\\\\Users\\\\benza\\\\Documents\\\\eclipse_workspce\\\\prj_reflexion\\\\ressources\\\\output2.xmi";
        
        XMLParser parser = new XMLParser();
        Project xmlfile = null;

        // Extraire le projet
        Project project = Extractor.extractProject(projectPath);
        ConsoleDisplay.displayProjectInfo(project);
        
        // Enrichir avec le code source
        SourceCodeAnalyzer.enrichWithSourceCode(project, "C:\\Users\\benza\\Documents\\eclipse_workspce\\projet_test\\src");

        // Générer les fichiers de sortie
        XMLWriter.writeProjectToXML(project, xmlFilePath);
        System.out.println("Fichier XML généré avec succès !");

        try {
            XmiGenerator.generateXmi(project, outputXmiPath);
            System.out.println("Fichier XMI généré avec succès !");
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Détecter les bad smells
        List<BadSmell> smells = BadSmellDetector2.detect(project);
        for(BadSmell smell : smells) {
            System.out.println("smells = " + smell);
        }
        JsonGenerator.exportBadSmellsToJson(smells, jsonFilePath);

        // Lancer le viewer avec le projet chargé
        SwingUtilities.invokeLater(() -> new UMLDiagramViewer2(project));
    }

    public static void main(String[] args) {
        new Client();
    }
}