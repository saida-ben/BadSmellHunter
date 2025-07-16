package org.mql.java.xml;

import org.mql.java.models.ClassInfo;
import org.mql.java.models.MethodInfo;
import org.mql.java.models.PackageInfo;
import org.mql.java.models.Project;
import org.w3c.dom.*;
import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;

public class XMLWriter {

	public static void writeProjectToXML(Project project, String filePath) {
	    try {
	        // Création du document XML
	        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
	        DocumentBuilder builder = factory.newDocumentBuilder();
	        Document doc = builder.newDocument();

	        // Élément racine
	        Element root = doc.createElement("Project");
	        doc.appendChild(root);

	        // Informations sur le projet
	        Element projectName = doc.createElement("ProjectName");
	        projectName.appendChild(doc.createTextNode(project.getName()));
	        root.appendChild(projectName);

	        // Informations sur les packages
	        Element packagesElement = doc.createElement("Packages");
	        root.appendChild(packagesElement);

	        for (PackageInfo pkg : project.getPackages()) {
	            Element packageElement = doc.createElement("Package");
	            packageElement.setAttribute("name", pkg.getName());
	            packagesElement.appendChild(packageElement);

	            for (ClassInfo cls : pkg.getClasses()) {
	                Element classElement = doc.createElement("Class");
	                classElement.setAttribute("name", cls.getName());
	                packageElement.appendChild(classElement);

	                Element superclass = doc.createElement("Superclass");
	                superclass.appendChild(doc.createTextNode(cls.getSuperclass()));
	                classElement.appendChild(superclass);

	                Element methods = doc.createElement("Methods");
	                classElement.appendChild(methods);

	                for (MethodInfo m : cls.getMethods()) {
	                    Element method = doc.createElement("Method");
	                    method.setAttribute("name", m.getName());
	                    method.appendChild(doc.createTextNode(m.getReturnType()));
	                    methods.appendChild(method);
	                }
	            }
	        }

	        // Écriture dans un fichier XML
	        TransformerFactory transformerFactory = TransformerFactory.newInstance();
	        Transformer transformer = transformerFactory.newTransformer();
	        transformer.setOutputProperty(OutputKeys.INDENT, "yes");

	        DOMSource source = new DOMSource(doc);
	        StreamResult result = new StreamResult(new File(filePath));

	        transformer.transform(source, result);

	        System.out.println("Fichier XML créé avec succès : " + filePath);
	    } catch (Exception e) {
	        System.err.println("Erreur lors de la création du fichier XML : " + e.getMessage());
	        e.printStackTrace(); 
	    }
	}

}
