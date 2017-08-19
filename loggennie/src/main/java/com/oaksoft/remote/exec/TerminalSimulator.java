package com.oaksoft.remote.exec;

import java.io.IOException;
import java.util.Scanner;

import org.apache.commons.lang3.StringUtils;

public class TerminalSimulator {
	private final RemoteCommander commander;
	
	private final String hostname;
	private final String username;
	public TerminalSimulator(final String hostname, final String username, final String password) throws IOException{
		this.hostname = hostname;
		this.username = username;
		this.commander = new RemoteCommander();
		commander.connect(hostname, username, password);
	}
	
	public void simulate(){
		String command = null;
        Scanner scan = new Scanner(System.in);
		while(!"Exist".equalsIgnoreCase(command)){
			System.out.printf("[%s@%s] >", username, StringUtils.substringBefore(hostname, ".")); 
	        command = scan.nextLine();
	        try {
				commander.execute(command);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		scan.close();
		commander.close();
	}
	
	public static void main(String[] args) {
		try {
			new TerminalSimulator("mylab-d5.oaksoft", "admin", "admin").simulate();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}
}
