package org.mql.java.models;

import java.util.List;

public class EnumInfo {
	private String name;
    List<String> values;
    
    public EnumInfo() {}

    
	public EnumInfo(String name, List<String> values) {
		super();
		this.name = name;
		this.values = values;
	}


	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<String> getValues() {
		return values;
	}

	public void setValues(List<String> values) {
		this.values = values;
	}

	

}
