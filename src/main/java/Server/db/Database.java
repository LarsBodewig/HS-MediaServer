package Server.db;

import java.io.PrintStream;
import java.sql.Connection;
import java.sql.SQLException;

import Server.Server;

public abstract class Database {

	private static final String PROTOCOL = "jdbc:mysql";
	private static final String HOST = "localhost";
	private static final String PORT = "3306";
	private static final String TIMEZONE_PARAMS = "useLegacyDatetimeCode=false&serverTimezone=Europe/Berlin";

	protected static final String DB_SCHEMA = "server";
	protected static final String DRIVER_CLASS_NAME = "com.mysql.cj.jdbc.Driver";
	protected static final String CONNECTION_STRING = PROTOCOL + "://" + HOST + ":" + PORT + "/" + DB_SCHEMA + "?"
			+ TIMEZONE_PARAMS;
	protected static final String USER = "server";
	protected static final String PASSWORD = "server";

	protected static Connection connection = null;

	public static Connection getConnection() {
		return connection;
	}

	public static boolean hasConnection() {
		try {
			return connection != null && connection.isValid(1);
		} catch (SQLException e) {
			log(e);
			return false;
		}
	}

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
