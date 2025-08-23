package org.mql.java.examples;

import java.io.IOException;
import java.util.List;

import javax.swing.SwingUtilities;

import org.mql.java.analysis.BadSmellDetector;
import org.mql.java.analysis.BadSmellDetector2;
import org.mql.java.models.*;
import org.mql.java.output.ConsoleDisplay;
import org.mql.java.output.JsonGenerator;
import org.mql.java.output.XMLParser;
import org.mql.java.output.XMLWriter;
import org.mql.java.output.XmiGenerator;
import org.mql.java.reflection.Extractor;
import org.mql.java.reflection.SourceCodeAnalyzer;
import org.mql.java.ui.UMLDiagramViewer1;
import org.mql.java.ui.UMLDiagramViewer2;



public class Client {
	
	public Client() {
		exp01();
	}
    	
	private void exp01() {
		String projectPath = "C:\\Users\\benza\\Documents\\eclipse_workspce\\prj_reflexion"; 
		String xmlFilePath = "C:\\Users\\benza\\Documents\\eclipse_workspce\\prj_reflexion\\ressources\\output.xml";
		String jsonFilePath = "C:\\Users\\benza\\Documents\\eclipse_workspce\\prj_reflexion\\ressources\\output.json";

		String outputXmiPath = "C:\\\\Users\\\\benza\\\\Documents\\\\eclipse_workspce\\\\prj_reflexion\\\\ressources\\\\output2.xmi";
	    XMLParser parser = new XMLParser();

	    Project xmlfile = null;
		Project project = Extractor.extractProject(projectPath); 
		ConsoleDisplay.displayProjectInfo(project);
		SourceCodeAnalyzer.enrichWithSourceCode(project, "C:\\Users\\benza\\Documents\\eclipse_workspce\\projet_test\\src");

		
		
		XMLWriter.writeProjectToXML(project, xmlFilePath); // Cela générera le fichier XML avec les informations extraites
		System.out.println("Fichier XML généré avec succès !");

		try {
			XmiGenerator.generateXmi(project, outputXmiPath);
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("Fichier XMI généré avec succès !");


    /*
     * 
		try {
			xmlfile = parser.parse("C:\\Users\\benza\\Documents\\eclipse_workspce\\prj_reflexion\\ressources\\output.xml");
		} catch (Exception e) {
			System.err.println("Erreur lors de l'analyse du fichier XML.");
			e.printStackTrace();
			return;
		}

		if (xmlfile == null) {
			System.err.println("Le projet est nul. Vérifiez le fichier XML ou le parser.");
			return;
		}
     */

		//parser.displayModel(xmlfile);
    
		List<BadSmell> smells = BadSmellDetector2.detect(project);
    
		for(BadSmell smell : smells) {
			System.out.println("smells = " + smell);
		}
		JsonGenerator.exportBadSmellsToJson(smells, jsonFilePath);
		
		SwingUtilities.invokeLater(() -> new UMLDiagramViewer2(project));		 

}

		




	public static void main(String[] args) {
        new Client();
   }
	
}
