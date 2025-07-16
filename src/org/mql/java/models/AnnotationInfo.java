package org.mql.java.models;

public class AnnotationInfo {
	private String name;
	    public AnnotationInfo(String name) {
	        this.name = name;
	    }

	    @Override
	    public String toString() {
	        return "@" + name;
	    } 
	    
	   String getName() {
		return name;
	   }

	public void setName(String name) {
		this.name = name;
	}
	
	

}
