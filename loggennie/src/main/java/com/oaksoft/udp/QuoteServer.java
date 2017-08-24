package com.oaksoft.udp;

import java.io.IOException;

/*
 * Copyright (c) 1995, 2008, Oracle and/or its affiliates. All rights reserved.
 */

public class QuoteServer {
	public static void main(String[] args) throws IOException {
		new QuoteServerThread().start();
	}
}
