package com.oceansky.health.model;

public enum Tag {
	NEW("New Arrival"),
	HOT("Hot"),
	SPECIAL("Specials");
	
	private String desc;
	
	Tag(final String desc){
		this.desc = desc;
	}
}
