package com.oaksoft.logging.util;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class LoggingUtils
{
	public static final char SOH_DELIMITER = '\u0001';
	public static final char STX_DELIMITER = '\u0002';
	public static final char ETX_DELIMITER = '\u0003';
	public static final char EOT_DELIMITER = '\u0004';

	public static String getHostName()
	{
        InetAddress ip;
        String hostname = "unknown";

        try
        {
            ip = InetAddress.getLocalHost();
            hostname = ip.getHostName();
        }
        catch (UnknownHostException e1)
        {
//            e1.printStackTrace();
        }

        return hostname;
	}
	
	
}
