package cesde.service.portoutput;

import cesde.domain.Cuenta;

/**
 * Puerto de persistencia de salida para las operaciones de Cuenta en base de datos.
 */
public interface CuentaPersistencePort {
    Cuenta buscarCuentaPorClienteYTipo(int clienteId, String tipoCuenta);
    void crearCuenta(Cuenta cuenta, int clienteId, String tipoCuenta);
    void actualizarSaldo(String numeroCuenta, double nuevoSaldo);
    boolean existeNumeroCuenta(String numeroCuenta);
}
