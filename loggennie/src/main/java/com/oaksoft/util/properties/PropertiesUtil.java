package com.oaksoft.util.properties;

import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PropertiesUtil
{
	public static Properties getProperties(String configFileName)
	{
		Properties properties = new Properties();

		InputStream inputStream = ClassLoader.getSystemResourceAsStream(configFileName);

		try
		{
			properties.load(inputStream);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}

    	return properties;
	}

	public static Properties getProperties(String configFileName, String prefix)
	{
		Properties properties = new Properties();

		Properties returnProperties = new Properties();

		Pattern propertyPattern = Pattern.compile(prefix);

		InputStream inputStream = ClassLoader.getSystemResourceAsStream(configFileName);

		try
		{
			properties.load(inputStream);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}

		for (Enumeration<?> e = properties.propertyNames(); e.hasMoreElements();)
		{
			String propertyName = (String) e.nextElement();

			Matcher propertyMatcher = propertyPattern.matcher(propertyName);

			Boolean matchFound = propertyMatcher.find();

			if (matchFound)
			{
				returnProperties.put(propertyName, properties.get(propertyName));
			}
		}

    	return returnProperties;
	}
}
