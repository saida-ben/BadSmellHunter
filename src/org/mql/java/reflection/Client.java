package org.mql.java.reflection;

import java.io.IOException;
import java.util.List;

import javax.swing.SwingUtilities;

import org.mql.java.analysis.BadSmellDetector;
import org.mql.java.models.*;
import org.mql.java.ui.UMLDiagramViewer;
import org.mql.java.xml.XMLParser;
import org.mql.java.xml.XMLWriter;



public class Client {
	
	public Client() {
		exp01();
	}
    	
	private void exp01() {
		String projectPath = "C:\\Users\\benza\\Documents\\eclipse_workspce\\prj_reflexion"; 
		String xmlFilePath = "C:\\Users\\benza\\Documents\\eclipse_workspce\\prj_reflexion\\ressources\\output.xml";
		String outputXmiPath = "C:\\\\Users\\\\benza\\\\Documents\\\\eclipse_workspce\\\\prj_reflexion\\\\ressources\\\\output2.xmi";
	    XMLParser parser = new XMLParser();

	    Project xmlfile = null;
		Project project = Extractor.extractProject(projectPath); 
		//ConsoleDisplay.displayProjectInfo(project);
    
		
		
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
    
		List<String> smells = BadSmellDetector.detect(project);
    
		for(String smell : smells) {
			System.out.println("smells = " + smell);
		}

		/*
		 * 
		SwingUtilities.invokeLater(() -> {
			UMLDiagramViewer viewer = new UMLDiagramViewer(project);
			viewer.setVisible(true);
		});			 */

}

		




	public static void main(String[] args) {
        new Client();
   }
	
}
