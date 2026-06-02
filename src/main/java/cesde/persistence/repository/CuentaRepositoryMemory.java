package cesde.persistence.repository;

import cesde.domain.Cuenta;
import cesde.domain.CuentaAhorros;
import cesde.domain.CuentaCorriente;
import cesde.service.portoutput.CuentaPersistencePort;

import java.util.HashMap;
import java.util.Map;

/**
 * Implementación en memoria del puerto de persistencia de cuentas (Modo Simulación).
 */
public class CuentaRepositoryMemory implements CuentaPersistencePort {

    private static final Map<String, Cuenta> cuentasPorNumero = new HashMap<>();
    private static final Map<String, Cuenta> cuentasPorClienteYTipo = new HashMap<>();

    public CuentaRepositoryMemory() {
        // Cuentas por defecto para el cliente "prueba" (ID: 12345)
        CuentaAhorros ahorroPrueba = new CuentaAhorros("C100200300", 500000.0);
        CuentaCorriente corrientePrueba = new CuentaCorriente("C400500600", 1000000.0);
        
        crearCuenta(ahorroPrueba, 12345, "AHORROS");
        crearCuenta(corrientePrueba, 12345, "CORRIENTE");
    }

    @Override
    public Cuenta buscarCuentaPorClienteYTipo(int clienteId, String tipoCuenta) {
        return cuentasPorClienteYTipo.get(clienteId + "_" + tipoCuenta.toUpperCase());
    }

    @Override
    public void crearCuenta(Cuenta cuenta, int clienteId, String tipoCuenta) {
        if (cuenta == null) return;
        cuentasPorNumero.put(cuenta.getNumeroCuenta(), cuenta);
        cuentasPorClienteYTipo.put(clienteId + "_" + tipoCuenta.toUpperCase(), cuenta);
    }

    @Override
    public void actualizarSaldo(String numeroCuenta, double nuevoSaldo) {
        Cuenta c = cuentasPorNumero.get(numeroCuenta);
        if (c != null) {
            c.setSaldo(nuevoSaldo);
        }
    }

    @Override
    public boolean existeNumeroCuenta(String numeroCuenta) {
        return cuentasPorNumero.containsKey(numeroCuenta);
    }

    @Override
    public Cuenta buscarCuentaPorNumero(String numeroCuenta) {
        return cuentasPorNumero.get(numeroCuenta);
    }
}
