package org.mql.java.models;

public class FieldModel  {
	// Ajouter ces méthodes à votre classe FieldModel

		private String name;
	    private String type;
	    private String visibility = "private"; // par défaut
	    private boolean isStatic = false;
	    private boolean isFinal = false;
	    
	    
	    // Constructeurs existants...
	    public FieldModel(String name, String type) {
	        this.name = name;
	        this.type = type;
	    }
	    
	    // Méthodes pour la visibilité
	    public String getVisibility() {
	        return visibility;
	    }

	    public void setVisibility(String visibility) {
	        this.visibility = visibility;
	    }

	    // Méthodes pour les modificateurs
	    public boolean isStatic() {
	        return isStatic;
	    }

	    public void setStatic(boolean isStatic) {
	        this.isStatic = isStatic;
	    }

	    public boolean isFinal() {
	        return isFinal;
	    }

	    public void setFinal(boolean isFinal) {
	        this.isFinal = isFinal;
	    }
	    
	    // Méthodes existantes getName() et getType()...
	    public String getName() {
	        return name;
	    }
	    
	    public String getType() {
	        return type;
	    }
	}
