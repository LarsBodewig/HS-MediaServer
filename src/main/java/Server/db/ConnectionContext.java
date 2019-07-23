package Server.db;

import java.sql.DriverManager;
import java.sql.SQLException;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

@WebListener
public class ConnectionContext extends Database implements ServletContextListener {

	public void contextInitialized(ServletContextEvent event) {
		try {
			Class.forName(DRIVER_CLASS_NAME);
			connection = DriverManager.getConnection(CONNECTION_STRING, USER, PASSWORD);
			connection.setSchema(DB_SCHEMA);
		} catch (SQLException | ClassNotFoundException e) {
			log(e);
		}
	}

	public void contextDestroyed(ServletContextEvent event) {
		if (connection != null) {
			try {
				if (!connection.isClosed()) {
					try {
						if (!connection.getAutoCommit()) {
							connection.rollback();
						}
					} catch (SQLException e) {
						log(e);
					}
				}
			} catch (SQLException e) {
				log(e);
			} finally {
				try {
					connection.close();
				} catch (SQLException e) {
					log(e);
				}
			}
		}
	}
}
