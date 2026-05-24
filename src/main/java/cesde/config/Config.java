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
 */
public class Config {

    public static MenuApp createMenuApp() {
        // 1. Obtener la conexión única (Singleton) a la base de datos MySQL
        Connection connection = DataBaseConnection.getInstance().getConnection();

        // 2. Instanciar los Mappers encargados de transformar ResultSets a Objetos de Dominio
        RowMapper<cesde.domain.Cliente> clienteMapper = new ClienteRowMapper();
        RowMapper<cesde.domain.Cuenta> cuentaMapper = new CuentaRowMapper();
        RowMapper<cesde.domain.TarjetaCredito> tarjetaMapper = new TarjetaRowMapper();

        // 3. Crear los adaptadores de persistencia (Repositories) que implementan los Puertos de Salida
        ClientePersistencePort clientePersistencePort = new ClienteRepositoryDB(connection, clienteMapper);
        CuentaPersistencePort cuentaPersistencePort = new CuentaRepositoryDB(connection, cuentaMapper);
        TarjetaPersistencePort tarjetaPersistencePort = new TarjetaRepositoryDB(connection, tarjetaMapper);

        // 4. Inyectar los repositorios en la implementación del Servicio (Capa de lógica)
        BankServiceImpl bankService = new BankServiceImpl(clientePersistencePort, cuentaPersistencePort, tarjetaPersistencePort);

        // 5. Inyectar el servicio en la Vista de Consola
        BankView bankView = new BankView(bankService);

        // 6. Inyectar la vista en el menú principal y retornarlo
        return new MenuApp(bankView);
    }
}
