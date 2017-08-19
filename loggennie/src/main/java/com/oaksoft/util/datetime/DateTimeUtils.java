package com.oaksoft.util.datetime;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

public class DateTimeUtils
{
	public static DateTime getJodaDateTimeUTC()
	{
		DateTime timeStamp = DateTime.now(DateTimeZone.UTC);

		return timeStamp;
	}

	public static DateTime getJodaDateTimeUTC(String dateString)
	{
		dateString = dateString.replace(" ", "T");
		return getJodaDateTime(dateString, DateTimeZone.UTC);
	}

	public static DateTime getJodaDateTime(String dateString, DateTimeZone tz)
	{
		DateTime dateTime = new DateTime(dateString).toDateTime(tz);

		return dateTime;
	}
}
