package codemagic.util.client.common;

public abstract class EventCounter {

	private static int count = 0;
	
	public static int incrementAndGet() {
		return ++count;
	}
	
	public static String incrementAndGetAsStr() {
		return "[ Count: " + String.valueOf(incrementAndGet()) + "]";
	}
}
