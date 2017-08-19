package com.oaksoft.util.string;

import static com.google.common.base.CaseFormat.LOWER_CAMEL;
import static com.google.common.base.CaseFormat.LOWER_UNDERSCORE;
import static com.google.common.base.CaseFormat.UPPER_UNDERSCORE;
import static com.google.common.base.CaseFormat.UPPER_CAMEL;

public class OakSoftStringUtils
{
	public static String lowerCamelCaseToLowerUnderscore(String inString)
	{
		String returnString =  UPPER_CAMEL.to(LOWER_UNDERSCORE, inString);

    	return returnString;
	}

	public static String lowerCamelCaseToLowerUnderscore(String inString, String trimString)
	{
		String returnString = lowerCamelCaseToLowerUnderscore(inString).replace(trimString, "");

		return returnString;
	}

	public static String lowerUnderscoreToLowerCamelCase(String inString)
	{
		String returnString =  LOWER_UNDERSCORE.to(LOWER_CAMEL, inString);

		return returnString;
	}

	public static String upperUnderscoreToUpperCamelCase(String inString)
	{
		String returnString =  UPPER_UNDERSCORE.to(UPPER_CAMEL, inString);

		return returnString;
	}
}
