package com.oaksoft.logging;

import java.io.FileNotFoundException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LoggenieMain
{
	private static final Logger logger = LogManager.getLogger();
		
    public static void main(String[] args) throws FileNotFoundException
    {
    	logger.info("Loggenie starting ....");    	
    	
	}
}
