package com.oaksoft.tcp;

import static com.oaksoft.tcp.ServerState.*;

import java.util.Scanner;

public class ChatSocketProtocol {
	private ServerState state = WAITING;
	private Scanner scan = new Scanner(System.in);

	public String processInput(String theInput) {
		String theOutput = null;
        System.out.println("Client: " + theInput);

		if (state == WAITING) {
			theOutput = "Knock! Knock!";
			state = SERVING;
		} else if (state == SERVING) {
			theOutput = scan.nextLine();			
		} 
		return theOutput;
	}

}
