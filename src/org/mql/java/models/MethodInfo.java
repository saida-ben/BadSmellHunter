package org.mql.java.models;

import java.util.ArrayList;
import java.util.List;

public class MethodInfo {
    private String name;
    private String returnType;
    private List<ParameterInfo> parameters;

    private String sourceCode;

    public MethodInfo(String name, String returnType) {
        this.name = name;
        this.returnType = returnType;
        this.parameters = new ArrayList<>();
        this.sourceCode = null;

    }

    public void addParameter(ParameterInfo parameterType) {
        parameters.add(parameterType);
    }

    public String getName() {
        return name;
    }

    public String getReturnType() {
        return returnType;
    }

    public List<ParameterInfo> getParameters() {
        return parameters;
    }
    
    public void setSourceCode(String sourceCode) {
        this.sourceCode = sourceCode;
    }

    public String getSourceCode() {
        return sourceCode;
    }
    
    public int countLines() {
    	
    	if(sourceCode == null || sourceCode.isEmpty()) {
    		return 0;
    	}
    	return sourceCode.split("\n").length;
    }
    
  
    
}
