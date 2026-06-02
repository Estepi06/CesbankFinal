package cesde.config;

import cesde.persistence.db.DataBaseConnection;
import cesde.persistence.mapper.*;
import cesde.persistence.repository.*;
import cesde.service.BankServiceImpl;
import cesde.service.portoutput.*;
import cesde.userinterface.MenuApp;
import cesde.view.BankView;

import java.sql.Connection;

/**
 * Contenedor de configuración encargado de realizar la Inyección de Dependencias
 * de forma manual para todas las capas del backend (siguiendo el patrón Ports & Adapters).
 * Soporta de manera transparente un MODO SIMULACIÓN EN MEMORIA si el servidor MySQL local está apagado.
 */
public class Config {

    public static MenuApp createMenuApp() {
        Connection connection = null;
        boolean offlineMode = false;

        try {
            // 1. Obtener la conexión única (Singleton) a la base de datos MySQL
            connection = DataBaseConnection.getInstance().getConnection();
        } catch (Exception e) {
            System.out.println("\n=====================================================================");
            System.out.println("                     [AVISO DE CESBANK CAJERO]                       ");
            System.out.println("=====================================================================");
            System.out.println(" No se pudo conectar a la base de datos MySQL en localhost:3306.");
            System.out.println(" Causa: ConnectException (Conexión rechazada / Puerto cerrado).");
            System.out.println("---------------------------------------------------------------------");
            System.out.println(" Para usar la base de datos MySQL permanente:");
            System.out.println(" 1. Abre el panel de control de XAMPP o tu administrador de MySQL.");
            System.out.println(" 2. Haz clic en 'Start' en la línea del módulo 'MySQL'.");
            System.out.println(" 3. Asegúrate de que el esquema 'cesbank' esté creado.");
            System.out.println("---------------------------------------------------------------------");
            System.out.println(" MODO ACTUAL: Iniciando en MODO SIMULACIÓN (EN MEMORIA) para pruebas.");
            System.out.println(" (Puedes crear clientes, cuentas, transacciones y todo funcionará)");
            System.out.println("=====================================================================\n");
            offlineMode = true;
        }

        ClientePersistencePort clientePersistencePort;
        CuentaPersistencePort cuentaPersistencePort;
        TarjetaPersistencePort tarjetaPersistencePort;
        TransaccionPersistencePort transaccionPersistencePort;

        if (offlineMode) {
            // Instanciar adaptadores en memoria
            clientePersistencePort = new ClienteRepositoryMemory();
            cuentaPersistencePort = new CuentaRepositoryMemory();
            tarjetaPersistencePort = new TarjetaRepositoryMemory();
            transaccionPersistencePort = new TransaccionRepositoryMemory();
        } else {
            // 2. Instanciar los Mappers encargados de transformar ResultSets a Objetos de Dominio
            RowMapper<cesde.domain.Cliente> clienteMapper = new ClienteRowMapper();
            RowMapper<cesde.domain.Cuenta> cuentaMapper = new CuentaRowMapper();
            RowMapper<cesde.domain.TarjetaCredito> tarjetaMapper = new TarjetaRowMapper();
            RowMapper<cesde.domain.Transaccion> transaccionMapper = new TransaccionRowMapper();

            // 3. Crear los adaptadores de persistencia (Repositories) que implementan los Puertos de Salida
            clientePersistencePort = new ClienteRepositoryDB(connection, clienteMapper);
            cuentaPersistencePort = new CuentaRepositoryDB(connection, cuentaMapper);
            tarjetaPersistencePort = new TarjetaRepositoryDB(connection, tarjetaMapper);
            transaccionPersistencePort = new TransaccionRepositoryDB(connection, transaccionMapper);
        }

        // 4. Inyectar los repositorios en la implementación del Servicio (Capa de lógica)
        BankServiceImpl bankService = new BankServiceImpl(
            clientePersistencePort, cuentaPersistencePort, tarjetaPersistencePort, transaccionPersistencePort
        );

        // 5. Inyectar el servicio en la Vista de Consola
        BankView bankView = new BankView(bankService);

        // 6. Inyectar la vista en el menú principal y retornarlo
        return new MenuApp(bankView);
    }
}
