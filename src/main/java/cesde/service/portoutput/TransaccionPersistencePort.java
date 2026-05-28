package cesde.service.portoutput;

import cesde.domain.Transaccion;
import java.util.List;

/**
 * Puerto de persistencia de salida para las operaciones de transacciones en la base de datos.
 */
public interface TransaccionPersistencePort {
    void registrarTransaccion(Transaccion transaccion);
    List<Transaccion> obtenerHistorialPorCuenta(String numeroCuenta);
}
