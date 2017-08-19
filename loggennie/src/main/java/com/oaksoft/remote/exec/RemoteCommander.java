package com.oaksoft.remote.exec;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import ch.ethz.ssh2.Connection;
import ch.ethz.ssh2.StreamGobbler;

public class RemoteCommander {	
	
	private Connection conn;
    private ch.ethz.ssh2.Session sess;

	public void connect(String remoteHost, String username,String password) throws IOException{
		System.out.printf("Connecting to remote host [%s]\n", remoteHost);
       
        conn = new Connection(remoteHost);
        conn.connect();
        boolean isAuthenticated = conn.authenticateWithPassword(username, password);
        if (isAuthenticated == false){
            throw new IOException("Authentication failed for user: " + username);   
        }
	}
	
	public void execute(String command) throws IOException{
        sess = conn.openSession();                   
		InputStream stdout = new StreamGobbler(sess.getStdout());
        InputStream stderr = new StreamGobbler(sess.getStderr());

        BufferedReader br = new BufferedReader(new InputStreamReader(stdout));
        BufferedReader brerr = new BufferedReader(new InputStreamReader(stderr));
        
        sess.execCommand(command);  

        while (true)
        {
            String line = br.readLine();
            if (line == null)
                break;
            System.out.println(line);
        }
       
		 while (true)
	     {
	         String line = brerr.readLine();
	         if (line == null)
	             break;
	         System.out.println(line);
	     }		
		 
		 br.close();
		 brerr.close();
		 sess.close();
    }
	
	public void close(){
         conn.close();
	}
 
}
