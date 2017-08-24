package com.oaksoft.net.nif;

import static java.lang.System.out;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;

public class NifUtils {

	public static void displayNetworkInterfaces() throws SocketException {
		Enumeration<NetworkInterface> nets = NetworkInterface.getNetworkInterfaces();

		for (NetworkInterface netIf : Collections.list(nets)) {
			out.printf("Display name: %s\n", netIf.getDisplayName());
			out.printf("Name: %s\n", netIf.getName());
			Enumeration<InetAddress> addresses = netIf.getInetAddresses();
			while (addresses.hasMoreElements()) {
				out.printf("Address: %s\n", addresses.nextElement().getHostAddress());
			}
			displaySubInterfaces(netIf);
			out.printf("Up? %s\n", netIf.isUp());
			out.printf("Loopback? %s\n", netIf.isLoopback());
			out.printf("PointToPoint? %s\n", netIf.isPointToPoint());
			out.printf("Supports multicast? %s\n", netIf.supportsMulticast());
			out.printf("Virtual? %s\n", netIf.isVirtual());
			out.printf("Hardware address: %s\n", Arrays.toString(netIf.getHardwareAddress()));
			out.printf("MTU: %s\n", netIf.getMTU());
			out.printf("\n");
			out.printf("\n");
		}
	}

	private static void displaySubInterfaces(NetworkInterface netIf) throws SocketException {
		Enumeration<NetworkInterface> subIfs = netIf.getSubInterfaces();

		for (NetworkInterface subIf : Collections.list(subIfs)) {
			out.printf("\tSub Interface Display name: %s\n", subIf.getDisplayName());
			out.printf("\tSub Interface Name: %s\n", subIf.getName());
		}
	}

	public static void main(String args[]) throws SocketException {
		displayNetworkInterfaces();
	}
}
