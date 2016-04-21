package com.pingan.jinke.infra.padis.util;

public class SleepUtils {

	private SleepUtils() {
	}

	public static void sleep(long timeout) {
		try {
			Thread.sleep(timeout);
		} catch (Throwable t) {
		}
	}
}
