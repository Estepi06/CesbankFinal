package cesde.persistence.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Singleton que gestiona la conexión JDBC a la base de datos MySQL 'cesbank'.
 * Ejecuta migraciones automáticas ligeras para las columnas de fechas agregadas en la refactorización.
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

            // Ejecutar migraciones de columnas de fecha de forma automática
            ejecutarMigraciones();

        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Error: No se encontró el driver de MySQL en el classpath", e);
        } catch (SQLException e) {
            throw new RuntimeException("Error: No se pudo conectar a la base de datos. Verifica que el servidor de MySQL esté encendido y que el esquema esté creado.", e);
        }
    }

    private void ejecutarMigraciones() {
        try (Statement stmt = connection.createStatement()) {
            // 1. Columnas en clientes
            try {
                stmt.execute("ALTER TABLE clientes ADD COLUMN fecha_nacimiento DATE DEFAULT NULL");
                System.out.println("[DB] Columna 'fecha_nacimiento' agregada a la tabla 'clientes'.");
            } catch (SQLException ignored) {}

            try {
                stmt.execute("ALTER TABLE clientes ADD COLUMN fecha_registro DATE DEFAULT NULL");
                System.out.println("[DB] Columna 'fecha_registro' agregada a la tabla 'clientes'.");
            } catch (SQLException ignored) {}

            // 2. Columna en cuentas
            try {
                stmt.execute("ALTER TABLE cuentas ADD COLUMN fecha_apertura DATE DEFAULT NULL");
                System.out.println("[DB] Columna 'fecha_apertura' agregada a la tabla 'cuentas'.");
            } catch (SQLException ignored) {}

            // 3. Columna en tarjetas
            try {
                stmt.execute("ALTER TABLE tarjetas ADD COLUMN fecha_expedicion DATE DEFAULT NULL");
                System.out.println("[DB] Columna 'fecha_expedicion' agregada a la tabla 'tarjetas'.");
            } catch (SQLException ignored) {}

        } catch (SQLException e) {
            System.out.println("[DB] Advertencia al ejecutar migraciones de columnas de fecha: " + e.getMessage());
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
