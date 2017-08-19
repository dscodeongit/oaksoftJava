package com.oaksoft.logging.pattern.log4j;

import static com.oaksoft.logging.regex.RegexElement.REGEX_COLON;
import static com.oaksoft.logging.regex.RegexElement.REGEX_COMMA;
import static com.oaksoft.logging.regex.RegexElement.REGEX_CURLY_BRACE_CLOSE;
import static com.oaksoft.logging.regex.RegexElement.REGEX_CURLY_BRACE_OPEN;
import static com.oaksoft.logging.regex.RegexElement.REGEX_LOWER_ALPHA_VALUES;
import static com.oaksoft.logging.regex.RegexElement.REGEX_MATCH_ALL;
import static com.oaksoft.logging.regex.RegexElement.REGEX_MINUS_SIGN;
import static com.oaksoft.logging.regex.RegexElement.REGEX_NUMERIC_VALUES;
import static com.oaksoft.logging.regex.RegexElement.REGEX_PERIOD;
import static com.oaksoft.logging.regex.RegexElement.REGEX_PLUS_SIGN;
import static com.oaksoft.logging.regex.RegexElement.REGEX_REPEAT_ONE_OR_MORE;
import static com.oaksoft.logging.regex.RegexElement.REGEX_REPEAT_ZERO_OR_MORE;
import static com.oaksoft.logging.regex.RegexElement.REGEX_REPEAT_ZERO_OR_ONE;
import static com.oaksoft.logging.regex.RegexElement.REGEX_SINGLE_QUOTE;
import static com.oaksoft.logging.regex.RegexElement.REGEX_SPACE;
import static com.oaksoft.logging.regex.RegexElement.REGEX_UNDERSCORE;
import static com.oaksoft.logging.regex.RegexElement.REGEX_UPPER_ALPHA_VALUES;

import java.util.regex.Pattern;

import com.oaksoft.logging.regex.RegexUtils;

public class Log4jPatterns
{
	private static final String LOG4J_CONVERSION_MARKER = "%";
	private static final String LOG4J_SPECIFIER = "specifier";
	private static final String LOG4J_FORMAT_MODIFIER = "formatModifier";

	public static final Pattern DATE_PATTERN = Pattern.compile(
		RegexUtils.regexNonCapturingGroup(
			LOG4J_CONVERSION_MARKER +
			regexJustify("d[ate]" + REGEX_REPEAT_ZERO_OR_ONE) +
			RegexUtils.regexNonCapturingGroup(
				REGEX_CURLY_BRACE_OPEN +
					RegexUtils.regexNamedCapturingGroup(
							RegexUtils.regexCharacterClass(
							REGEX_LOWER_ALPHA_VALUES +
							REGEX_UPPER_ALPHA_VALUES +
							REGEX_NUMERIC_VALUES +
							REGEX_PLUS_SIGN +
							REGEX_MINUS_SIGN +
							REGEX_UNDERSCORE +
							REGEX_COLON +
							REGEX_COMMA +
							REGEX_SPACE +
							REGEX_SINGLE_QUOTE
							, REGEX_REPEAT_ZERO_OR_MORE
						)
					, LOG4J_SPECIFIER) +
				REGEX_CURLY_BRACE_CLOSE
			, REGEX_REPEAT_ZERO_OR_MORE)
		)
	);

	public static final Pattern CATEGORY_PATTERN = Pattern.compile(
		RegexUtils.regexNonCapturingGroup(
			LOG4J_CONVERSION_MARKER +
			regexJustify("c") +
			RegexUtils.regexNonCapturingGroup(
				REGEX_CURLY_BRACE_OPEN +
					RegexUtils.regexNamedCapturingGroup(
							RegexUtils.nonGreedy(REGEX_MATCH_ALL)
						, LOG4J_SPECIFIER) +
				REGEX_CURLY_BRACE_CLOSE
			, REGEX_REPEAT_ZERO_OR_ONE)
		)
	);

	public static final Pattern CALLER_PATTERN = Pattern.compile(
		RegexUtils.regexNonCapturingGroup(
			LOG4J_CONVERSION_MARKER +
			regexJustify("C") +
			RegexUtils.regexNonCapturingGroup(
				REGEX_CURLY_BRACE_OPEN +
					RegexUtils.regexNamedCapturingGroup(
							RegexUtils.regexCharacterClass(
							REGEX_NUMERIC_VALUES
							, REGEX_REPEAT_ZERO_OR_MORE
						)
					, LOG4J_SPECIFIER) +
				REGEX_CURLY_BRACE_CLOSE
			, REGEX_REPEAT_ZERO_OR_ONE)
		)
	);

	public static final Pattern FILENAME_PATTERN = Pattern.compile(
		RegexUtils.regexNonCapturingGroup(
			LOG4J_CONVERSION_MARKER +
			regexJustify("F")
		)
	);

	public static final Pattern LOCATION_PATTERN = Pattern.compile(
		RegexUtils.regexNonCapturingGroup(
			LOG4J_CONVERSION_MARKER +
			regexJustify("l")
		)
	);

	public static final Pattern LINE_NUMBER_PATTERN = Pattern.compile(
		RegexUtils.regexNonCapturingGroup(
			LOG4J_CONVERSION_MARKER +
			regexJustify("L")
		)
	);

	public static final Pattern MESSAGE_PATTERN = Pattern.compile(
		RegexUtils.regexNonCapturingGroup(
			LOG4J_CONVERSION_MARKER +
			regexJustify("m")
		)
	);

	public static final Pattern METHOD_PATTERN = Pattern.compile(
		RegexUtils.regexNonCapturingGroup(
			LOG4J_CONVERSION_MARKER +
			regexJustify("M")
		)
	);

	public static final Pattern LINE_SEPARATOR_PATTERN = Pattern.compile(
		RegexUtils.regexNonCapturingGroup(
			LOG4J_CONVERSION_MARKER +
			regexJustify("n")
		)
	);

	public static final Pattern PRIORITY_PATTERN = Pattern.compile(
		RegexUtils.regexNonCapturingGroup(
			LOG4J_CONVERSION_MARKER +
			regexJustify("p")
		)
	);

	public static final Pattern MILLISECONDS_PATTERN = Pattern.compile(
		RegexUtils.regexNonCapturingGroup(
			LOG4J_CONVERSION_MARKER +
			regexJustify("r")
		)
	);

	public static final Pattern THREAD_PATTERN = Pattern.compile(
		RegexUtils.regexNonCapturingGroup(
			LOG4J_CONVERSION_MARKER +
			regexJustify("t")
		)
	);

	public static final Pattern NDC_PATTERN = Pattern.compile(
		RegexUtils.regexNonCapturingGroup(
			LOG4J_CONVERSION_MARKER +
			regexJustify("x")
		)
	);

	public static final Pattern MDC_PATTERN = Pattern.compile(
		RegexUtils.regexNonCapturingGroup(
			LOG4J_CONVERSION_MARKER +
			regexJustify("X") +
			RegexUtils.regexNonCapturingGroup(
				REGEX_CURLY_BRACE_OPEN +
				RegexUtils.regexNamedCapturingGroup(
						RegexUtils.regexCharacterClass(
							REGEX_LOWER_ALPHA_VALUES +
							REGEX_UPPER_ALPHA_VALUES +
							REGEX_NUMERIC_VALUES +
							REGEX_UNDERSCORE
							, REGEX_REPEAT_ZERO_OR_MORE
						)
					, LOG4J_SPECIFIER) +
				REGEX_CURLY_BRACE_CLOSE
			, REGEX_REPEAT_ZERO_OR_ONE)
		)
	);

	public static final Pattern PERCENTAGE_SIGN_PATTERN = Pattern.compile(
		RegexUtils.regexNonCapturingGroup(
			LOG4J_CONVERSION_MARKER +
			regexJustify(LOG4J_CONVERSION_MARKER)
		)
	);

	public static String regexJustify(String regexString)
	{
		return
			RegexUtils.nonGreedy(
				RegexUtils.regexNamedCapturingGroup(
						REGEX_MINUS_SIGN +
						REGEX_REPEAT_ZERO_OR_ONE +
					RegexUtils.regexCharacterClass(
						REGEX_NUMERIC_VALUES
						, REGEX_REPEAT_ONE_OR_MORE
					) +
					RegexUtils.regexNonCapturingGroup(
						REGEX_PERIOD +
						RegexUtils.regexCharacterClass(
							REGEX_NUMERIC_VALUES
							, REGEX_REPEAT_ONE_OR_MORE)
					, REGEX_REPEAT_ZERO_OR_ONE)
				, LOG4J_FORMAT_MODIFIER)
			) + regexString;
	}
}
