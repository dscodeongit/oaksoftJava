package com.oaksoft.logging;

public interface DataCollector {
	void start();
	void reStart();
	void stop();
	//boolean isActive();
	boolean needsRestart();
}
