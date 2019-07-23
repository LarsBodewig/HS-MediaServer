package Server;

import java.io.PrintStream;

public abstract class Server {

	private static final PrintStream logStream = System.out;
	
	public static PrintStream getLogStream() {
		return logStream;
	}
}
