package com.oaksoft.logging;

import java.io.IOException;
import java.io.ObjectInputStream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.net.server.ObjectInputStreamLogEventBridge;
import org.apache.logging.log4j.core.net.server.TcpSocketServer;

public class LogReceiver 
{
	private final static Logger logger = LogManager.getLogger(LogReceiver.class);
	
    public static void main(String[] args)
    {
    	TcpSocketServer<ObjectInputStream> tcpSocketServer = null;
    	
    	try 
    	{
			tcpSocketServer = new TcpSocketServer<ObjectInputStream>(5514, new ObjectInputStreamLogEventBridge());
		}
    	catch (IOException e)
    	{
			e.printStackTrace();
			
			logger.error(e);
		}

    	tcpSocketServer.run();
    }
}
