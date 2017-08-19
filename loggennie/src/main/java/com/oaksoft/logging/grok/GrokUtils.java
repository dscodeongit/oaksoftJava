package com.oaksoft.logging.grok;

import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.oaksoft.logging.regex.RegexUtils;

import static com.google.common.base.CaseFormat.LOWER_CAMEL;
import static com.google.common.base.CaseFormat.LOWER_UNDERSCORE;
import static com.oaksoft.logging.util.LoggingUtils.EOT_DELIMITER;
import static com.oaksoft.logging.util.LoggingUtils.ETX_DELIMITER;
import static com.oaksoft.logging.util.LoggingUtils.SOH_DELIMITER;
import static com.oaksoft.logging.util.LoggingUtils.STX_DELIMITER;

public class GrokUtils
{
	public static String buildGrokPattern(String patternString)
	{
		HashSet<String> regexReservedHashSet = new HashSet<>();

		Pattern regexReservedPattern = Pattern.compile(RegexUtils.getRegexReservedCharactersAsPatternString());

		Matcher regexReservedMatcher = regexReservedPattern.matcher(patternString);

		while (regexReservedMatcher.find())
		{
			regexReservedHashSet.add(regexReservedMatcher.group());
		}

		for (String regexReservedCharacter: regexReservedHashSet)
		{
			patternString = patternString.replace(regexReservedCharacter, "\\" + regexReservedCharacter);
		}

		patternString = patternString.replace(" ", "\\s*");

		String[] patterns = patternString.split(String.valueOf(EOT_DELIMITER));

		for (int i=0; i < patterns.length; i++)
		{
			String[] fieldPatterns = patterns[i].split(String.valueOf(SOH_DELIMITER));

			String fieldName = null;

			if (fieldPatterns.length > 1)
			{
				fieldName = fieldPatterns[1].split(STX_DELIMITER + "|" + ETX_DELIMITER)[0];
			}
			else
			{
				fieldName = fieldPatterns[0];
			}

			switch (fieldName)
			{
				case "TIMESTAMP":
					String[] specifierPatterns = fieldPatterns[1].split(String.valueOf(ETX_DELIMITER));

					if (specifierPatterns.length > 1)
					{
						patternString = patternString.replace(getDelimitedField(patterns[i]), wrapFieldInNamedGroup(fieldName, getSpecifier(specifierPatterns)));
					}
					else
					{
						patternString = patternString.replace(getDelimitedField(patterns[i]), wrapFieldInNamedGroup(fieldName));
					}

					break;

				default:
					if (fieldPatterns.length > 1)
					{
						patternString = patternString.replace(getDelimitedField(patterns[i]), wrapFieldInNamedGroup(fieldName));
					}
			}
		}

		return patternString;
	}

	private static String wrapFieldInNamedGroup(String fieldName, String specifierPattern)
	{
		return "(?<" + LOWER_UNDERSCORE.to(LOWER_CAMEL, fieldName) + ">%{" + fieldName + specifierPattern + "})";
	}

	private static String wrapFieldInNamedGroup(String fieldName)
	{
		return "(?<" + LOWER_UNDERSCORE.to(LOWER_CAMEL, fieldName) + ">%{" + fieldName + "})";
	}

	private static String getDelimitedField(String patternString)
	{
		return SOH_DELIMITER + patternString.split(SOH_DELIMITER + "")[1] + EOT_DELIMITER;
	}

	private static String getSpecifier(String[] specifierPatterns)
	{
		return "_" + specifierPatterns[1];
	}
}
