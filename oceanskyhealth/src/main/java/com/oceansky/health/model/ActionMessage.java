package com.oceansky.health.model;

public class ActionMessage {
	private final String messageKey;
	private final ActionStatus actionStatus;
	
	private static final ActionMessage SUCCESS = new ActionMessage("Sucess!", ActionStatus.success());
	
	private ActionMessage(String messageKey, ActionStatus actionStatus) {
		super();
		this.messageKey = messageKey;
		this.actionStatus = actionStatus;
	}

	public String getMessageKey() {
		return messageKey;
	}
	
	public boolean isSuccessful(){
		return actionStatus == ActionStatus.SUCCESS;
	}
	
	public static ActionMessage sucess(){
		return SUCCESS;
	}
	public static ActionMessage sucess(String messageKey){
		return new ActionMessage(messageKey, ActionStatus.SUCCESS);
	}
	public static ActionMessage error(String messageKey){
		return new ActionMessage(messageKey, ActionStatus.FAILED);
	}
}
