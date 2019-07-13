package Server.api;

import java.io.PrintStream;

public abstract class Api {

	private static final PrintStream logStream = System.out;

	public static void log(String... lines) {
		for (String line : lines) {
			logStream.println(line);
		}
	}

	public static void log(Exception exception) {
		exception.printStackTrace(logStream);
	}
}