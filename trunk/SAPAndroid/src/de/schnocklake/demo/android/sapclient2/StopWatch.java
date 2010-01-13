package de.schnocklake.demo.android.sapclient2;

import java.util.Date;

public class StopWatch {
	public static Date startTimestamp = null;
	
	public static void start() {
		startTimestamp = new Date();
	}
	
	public static long getTime() {
		return new Date().getTime() - startTimestamp.getTime();
	}	
}
