package org.mql.java.analysis;

import java.util.List;
import java.util.Vector;

import org.mql.java.models.ClassInfo;
import org.mql.java.models.MethodInfo;
import org.mql.java.models.PackageInfo;
import org.mql.java.models.Project;

public class BadSmellDetector {

	public BadSmellDetector() {}
	
	public static List<String> detect (Project project){
		
		List<String> smells = new Vector<>();
		
		for(PackageInfo pkg : project.getPackages()) {
			
			for(ClassInfo clsInfo : pkg.getClasses()) {
				
                // Détecter Large Class
				if(clsInfo.getMethods().size()> 20 || clsInfo.getFields().size()> 15) {
					smells.add("Large class : " + clsInfo.getName());
				}
				
				
                // Détecter Data Class (beaucoup d'attributs, peu de méthodes)

				if(clsInfo.getFields().size()>= 5 && clsInfo.getMethods().isEmpty() ) {
					smells.add("Data class" + clsInfo.getName());
					System.out.println("Classe analysée : " + clsInfo.getName() + " - Méthodes : " + clsInfo.getMethods().size() + " - Champs : " + clsInfo.getFields().size());

				}
				
				
                // Détecter God Class (par exemple si elle a trop de relations + méthodes)
				
				if(clsInfo.getMethods().size() >= 20 && clsInfo.getRelations().size() >= 5) {
					smells.add("God class" + clsInfo.getName());

				}
				
				for(MethodInfo meth : clsInfo.getMethods()) {
				    // Critère 1 : trop de lignes
				    boolean tooLong = meth.countLines() > 40;

				    // Critère 2 : trop de paramètres
				    boolean tooManyParams = meth.getParameters().size() > 5;

				   
				    if(tooLong || tooManyParams ) {
				        String reason = "";
				        if(tooLong) reason += "Too many lines; ";
				        if(tooManyParams) reason += "Too many parameters; ";

				        smells.add("Long method: " + clsInfo.getName() + "." + meth.getName() + " [" + reason + "]");
				    }
				}
				
				
			}
		}
		
		
		
		
		return smells;
		
	}
	
}
