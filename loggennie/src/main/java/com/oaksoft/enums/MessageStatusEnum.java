package com.oaksoft.enums;

import java.util.HashMap;
import java.util.Map;

public enum MessageStatusEnum
{
	STATUS_ACTIVE("0"),
	STATUS_MARKED_ROLLBACK("1"),
	STATUS_PREPARED("2"),
	STATUS_COMMITTED("3"),
	STATUS_ROLLEDBACK("4"),
	STATUS_UNKNOWN("5"),
	STATUS_NO_TRANSACTION("6"),
	STATUS_PREPARING("7"),
	STATUS_COMMITTING("8"),
	STATUS_ROLLING_BACK("9"),
	STATUS_COMMITTED_ACK("10"),
	STATUS_ROLLEDBACK_ACK("11"),
	STATUS_COMMITTED_NACK("12"),
	STATUS_ROLLEDBACK_NACK("13"),
	STATUS_ACTIVE_NACK("14"),
	STATUS_REDELIVER("15");
	

	private static final Map<String, MessageStatusEnum> stringToEnum = new HashMap<String, MessageStatusEnum>();

	static // Initialize map from code to enum constant
	{
		for (MessageStatusEnum code : values())
		{
			stringToEnum.put(code.getMessageStatusEnum(), code);
		}
	}

	// Returns MessageStatusEnum for character code, or null if code is invalid
	public static MessageStatusEnum fromCode(String code)
	{
		return stringToEnum.get(code);
	}

	private final String code;

	MessageStatusEnum(String code)
	{
		this.code = code;
	}

	public String getMessageStatusEnum()
	{
		return code;
	}
}