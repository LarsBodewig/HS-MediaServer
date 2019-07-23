package Server.api;

import java.io.PrintStream;

import Server.Server;

public abstract class Api {

	public static void log(String... lines) {
		PrintStream logStream = Server.getLogStream();
		for (String line : lines) {
			logStream.println(line);
		}
	}

	public static void log(Exception exception) {
		exception.printStackTrace(Server.getLogStream());
	}
}