package com.gravypod.wrapper.server;

public class ConfigEntry {
	
	private String name, comment, value;
	
	public ConfigEntry(String name, String value, String comment) {
		this.name = name;
		this.comment = comment;
		this.value = value;
	}
	
	public String getComment() {
		return comment;
	}
	
	public String getName() {
		return name;
	}
	
	public String getValue() {
		return value;
	}
	
	public void setValue(Object value) {
		this.value = String.valueOf(value);
	}
	
	@Override
	public String toString() {
		return getName() + " = " + getValue() + " " + comment;
	}

}
