package cesde.persistence.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Singleton que gestiona la conexión JDBC a la base de datos MySQL 'cesbank'.
 */
public class DataBaseConnection {

    private static DataBaseConnection instance;
    private final Connection connection;

    private static final String URL = "jdbc:mysql://localhost:3306/cesbank?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "";

    private DataBaseConnection() {
        try {
            // Cargar explícitamente el driver
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
            System.out.println("\n[DB] Conexión a la base de datos 'cesbank' establecida correctamente.");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Error: No se encontró el driver de MySQL en el classpath", e);
        } catch (SQLException e) {
            throw new RuntimeException("Error: No se pudo conectar a la base de datos. Verifica que el servidor de MySQL esté encendido y que el esquema esté creado.", e);
        }
    }

    public static synchronized DataBaseConnection getInstance() {
        if (instance == null) {
            instance = new DataBaseConnection();
        }
        return instance;
    }

    public Connection getConnection() {
        return connection;
    }
}
