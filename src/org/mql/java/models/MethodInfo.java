package org.mql.java.models;

import java.util.ArrayList;
import java.util.List;

public class MethodInfo {
    private String name;
    private String returnType;
    private List<ParameterInfo> parameters;
    private String sourceCode;
	// Ajouter ces méthodes à votre classe MethodInfo

	// Champ pour stocker la visibilité
	private String visibility = "public"; // par défaut

	// Champs pour les modificateurs
	private boolean isStatic = false;
	private boolean isAbstract = false;
	private boolean isFinal = false;
    // Pour Feature Envy : champs accédés + méthodes invoquées
    private List<String> accessedFields;
    private List<String> invokedMethods;

    public MethodInfo(String name, String returnType) {
        this.name = name;
        this.returnType = returnType;
        this.parameters = new ArrayList<>();
        this.accessedFields = new ArrayList<>();
        this.invokedMethods = new ArrayList<>();
    }

    public void addParameter(ParameterInfo parameter) {
        parameters.add(parameter);
    }

    public List<ParameterInfo> getParameters() {
        return parameters;
    }

    public String getName() {
        return name;
    }

    public String getReturnType() {
        return returnType;
    }

    public void setSourceCode(String sourceCode) {
        this.sourceCode = sourceCode;
    }

    public String getSourceCode() {
        return sourceCode;
    }

    public int countLines() {
        if (sourceCode == null || sourceCode.isEmpty()) {
            return 0;
        }
        return sourceCode.split("\n").length;
    }

    // Renvoie le corps nettoyé (utile pour Duplicate Code)
    public String getBody() {
        if (sourceCode == null) return "";
        return sourceCode.replaceAll("\\s+", "").replaceAll("//.*|/\\*(.|\\R)*?\\*/", "");
    }

    // Accès aux champs (pour Feature Envy)
    public void addAccessedField(String fieldQualifiedName) {
        accessedFields.add(fieldQualifiedName);
    }

    public List<String> getAccessedFields() {
        return accessedFields;
    }

    // Méthodes invoquées (pour Feature Envy)
    public void addInvokedMethod(String methodQualifiedName) {
        invokedMethods.add(methodQualifiedName);
    }

    public List<String> getInvokedMethods() {
        return invokedMethods;
    }

	@Override
	public String toString() {
		return "MethodInfo [name=" + name + ", returnType=" + returnType + ", parameters=" + parameters
				+ ", sourceCode=" + sourceCode + ", accessedFields=" + accessedFields + ", invokedMethods="
				+ invokedMethods + "]";
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

	public boolean isAbstract() {
	    return isAbstract;
	}

	public void setAbstract(boolean isAbstract) {
	    this.isAbstract = isAbstract;
	}

	public boolean isFinal() {
	    return isFinal;
	}

	public void setFinal(boolean isFinal) {
	    this.isFinal = isFinal;
	}

	// Méthode pour obtenir les paramètres sous forme de liste de types (String)
	public java.util.List<String> getParameterTypes() {
	    java.util.List<String> types = new java.util.ArrayList<>();
	    for (ParameterInfo param : parameters) {
	        types.add(param.getType());
	    }
	    return types;
	}
}
