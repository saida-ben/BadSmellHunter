package org.mql.java.models;


public class BadSmell {
    private String type;
    private String location;
    private String reason;

    public BadSmell(String type, String location, String reason) {
        this.type = type;
        this.location = location;
        this.reason = reason;
    }

    public String getType() { return type; }
    public String getLocation() { return location; }
    public String getReason() { return reason; }

    public void setType(String type) { this.type = type; }
    public void setLocation(String location) { this.location = location; }
    public void setReason(String reason) { this.reason = reason; }

	@Override
	public String toString() {
		return "BadSmell [type=" + type + ", location=" + location + ", reason=" + reason + "]";
	}
    
    
}

