package cesde.persistence.repository;

import cesde.domain.Transaccion;
import cesde.service.portoutput.TransaccionPersistencePort;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Implementación en memoria del puerto de persistencia de transacciones (Modo Simulación).
 */
public class TransaccionRepositoryMemory implements TransaccionPersistencePort {

    private static final Map<String, List<Transaccion>> transaccionesPorCuenta = new HashMap<>();

    @Override
    public void registrarTransaccion(Transaccion transaccion) {
        if (transaccion == null) return;
        
        List<Transaccion> list = transaccionesPorCuenta.computeIfAbsent(
            transaccion.getNumeroCuenta(), k -> new ArrayList<>()
        );
        
        // Agregar al inicio para ordenar por id descendente simulado
        list.add(0, transaccion);
    }

    @Override
    public List<Transaccion> obtenerHistorialPorCuenta(String numeroCuenta) {
        return transaccionesPorCuenta.getOrDefault(numeroCuenta, new ArrayList<>());
    }
}
