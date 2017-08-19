package com.oaksoft.logging.pattern.log4j;

import static com.oaksoft.logging.pattern.log4j.Log4jPatterns.CALLER_PATTERN;
import static com.oaksoft.logging.pattern.log4j.Log4jPatterns.CATEGORY_PATTERN;
import static com.oaksoft.logging.pattern.log4j.Log4jPatterns.DATE_PATTERN;
import static com.oaksoft.logging.pattern.log4j.Log4jPatterns.FILENAME_PATTERN;
import static com.oaksoft.logging.pattern.log4j.Log4jPatterns.LINE_NUMBER_PATTERN;
import static com.oaksoft.logging.pattern.log4j.Log4jPatterns.LINE_SEPARATOR_PATTERN;
import static com.oaksoft.logging.pattern.log4j.Log4jPatterns.LOCATION_PATTERN;
import static com.oaksoft.logging.pattern.log4j.Log4jPatterns.MDC_PATTERN;
import static com.oaksoft.logging.pattern.log4j.Log4jPatterns.MESSAGE_PATTERN;
import static com.oaksoft.logging.pattern.log4j.Log4jPatterns.METHOD_PATTERN;
import static com.oaksoft.logging.pattern.log4j.Log4jPatterns.MILLISECONDS_PATTERN;
import static com.oaksoft.logging.pattern.log4j.Log4jPatterns.NDC_PATTERN;
import static com.oaksoft.logging.pattern.log4j.Log4jPatterns.PERCENTAGE_SIGN_PATTERN;
import static com.oaksoft.logging.pattern.log4j.Log4jPatterns.PRIORITY_PATTERN;
import static com.oaksoft.logging.pattern.log4j.Log4jPatterns.THREAD_PATTERN;

import java.util.regex.Pattern;

public class Log4jGrokPatterns
{
	public static Pattern TIMESTAMP_PATTERN = DATE_PATTERN;
	public static Pattern LOG4J_CATEGORY_PATTERN = CATEGORY_PATTERN;
	public static Pattern LOG4J_CALLER_PATTERN = CALLER_PATTERN;
	public static Pattern LOG4J_FILENAME_PATTERN = FILENAME_PATTERN;
	public static Pattern LOG4J_LINE_NUMBER_PATTERN = LINE_NUMBER_PATTERN;
	public static Pattern LOG4J_LOCATION_PATTERN = LOCATION_PATTERN;
	public static Pattern LOG4J_MESSAGE_PATTERN = MESSAGE_PATTERN;
	public static Pattern LOG4J_METHOD_PATTERN = METHOD_PATTERN;
	public static Pattern LOG4J_LINE_SEPARATOR_PATTERN = LINE_SEPARATOR_PATTERN;
	public static Pattern LOGLEVEL_PATTERN = PRIORITY_PATTERN;
	public static Pattern LOG4J_MILLISECONDS_PATTERN = MILLISECONDS_PATTERN;
	public static Pattern LOG4J_THREAD_PATTERN = THREAD_PATTERN;
	public static Pattern LOG4J_NDC_PATTERN = NDC_PATTERN;
	public static Pattern LOG4J_MDC_PATTERN = MDC_PATTERN;
	public static Pattern LOG4J_PERCENTAGE_SIGN_PATTERN = PERCENTAGE_SIGN_PATTERN;
}
