package com.oaksoft.logging.regex;

import static com.oaksoft.logging.regex.RegexElement.REGEX_CAPTURING_GROUP_OPEN;
import static com.oaksoft.logging.regex.RegexElement.REGEX_CHARACTER_SET_CLOSE;
import static com.oaksoft.logging.regex.RegexElement.REGEX_CHARACTER_SET_OPEN;
import static com.oaksoft.logging.regex.RegexElement.REGEX_GROUP_CLOSE;
import static com.oaksoft.logging.regex.RegexElement.REGEX_GROUP_NAME_CLOSE;
import static com.oaksoft.logging.regex.RegexElement.REGEX_NAMED_CAPTURING_GROUP_OPEN;
import static com.oaksoft.logging.regex.RegexElement.REGEX_NON_CAPTURING_GROUP_OPEN;
import static com.oaksoft.logging.regex.RegexElement.REGEX_NON_GREEDY;

import java.lang.reflect.Field;
import java.util.ArrayList;

import org.apache.commons.lang3.StringUtils;

public class RegexUtils
{
	private static final char SOH_DELIMITER = '\u0001';
	private static final char EOT_DELIMITER = '\u0004';

	public static String delimitPattern(String pattern)
	{
		return SOH_DELIMITER + pattern + EOT_DELIMITER;
	}

	public static String regexCapturingGroup(String regexString)
	{
		return REGEX_CAPTURING_GROUP_OPEN + regexString + REGEX_GROUP_CLOSE;
	}

	public static String regexCapturingGroup(String regexString, String repeatType)
	{
		return regexCapturingGroup(regexString) + repeatType;
	}

	public static String regexNamedCapturingGroup(String regexString, String name)
	{
		return REGEX_NAMED_CAPTURING_GROUP_OPEN + name + REGEX_GROUP_NAME_CLOSE + regexString + REGEX_GROUP_CLOSE;
	}

	public static String regexNamedCapturingGroup(String regexString, String name, String repeatType)
	{
		return regexNamedCapturingGroup(regexString, name) + repeatType;
	}

	public static String regexNonCapturingGroup(String regexString)
	{
		return REGEX_NON_CAPTURING_GROUP_OPEN + regexString + REGEX_GROUP_CLOSE;
	}

	public static String regexNonCapturingGroup(String regexString, String repeatType)
	{
		return regexNonCapturingGroup(regexString) + repeatType;
	}

	public static String regexCharacterClass(String regexString)
	{
		return REGEX_CHARACTER_SET_OPEN + regexString + REGEX_CHARACTER_SET_CLOSE;
	}

	public static String regexCharacterClass(String regexString, String repeatType)
	{
		return regexCharacterClass(regexString) + repeatType;
	}

	public static String nonGreedy(String regexString)
	{
		return regexString + REGEX_NON_GREEDY;
	}

	public static String getRegexReservedCharactersAsPatternString()
	{
		Field[] fields = RegexElement.class.getDeclaredFields();

		ArrayList<Object> regexMetaCharacterArrayList = new ArrayList<>();

		for (Field field : fields)
		{
			if (field.getType().isAssignableFrom(String.class) && field.getName().contains("REGEX_RESERVED_"))
			{
				try
				{
					regexMetaCharacterArrayList.add("\\" + field.get(null));
				}
				catch (IllegalArgumentException | IllegalAccessException e)
				{
					e.printStackTrace();
				}
			}
		}

		return REGEX_CHARACTER_SET_OPEN + StringUtils.join(regexMetaCharacterArrayList, "|") + REGEX_CHARACTER_SET_CLOSE;
	}
}
