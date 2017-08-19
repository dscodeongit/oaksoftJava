package com.oceansky.health.model;

public enum ActionStatus {
	SUCCESS,
	FAILED;
	
	public static ActionStatus success(){
		return SUCCESS;
	}
	
	public static ActionStatus failed(){
		return FAILED;
	}
}
