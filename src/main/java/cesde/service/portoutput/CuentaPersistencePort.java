package cesde.service.portoutput;

import cesde.domain.Cuenta;

/**
 * Puerto de persistencia de salida para las operaciones de Cuenta en base de datos.
 */
public interface CuentaPersistencePort {
    Cuenta buscarCuentaPorClienteYTipo(int clienteId, String tipoCuenta);
    /**
     * Busca una cuenta por su número único en la tabla 'cuentas'.
     * @param numeroCuenta Número de la cuenta (ej. C123456789)
     * @return La instancia de Cuenta (CuentaAhorros o CuentaCorriente) o null si no existe.
     */
    Cuenta buscarCuentaPorNumero(String numeroCuenta);
    void crearCuenta(Cuenta cuenta, int clienteId, String tipoCuenta);
    void actualizarSaldo(String numeroCuenta, double nuevoSaldo);
    boolean existeNumeroCuenta(String numeroCuenta);
}
