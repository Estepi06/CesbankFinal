package cesde.persistence.repository;

import cesde.domain.TarjetaCredito;
import cesde.service.portoutput.TarjetaPersistencePort;

import java.util.HashMap;
import java.util.Map;

/**
 * Implementación en memoria del puerto de persistencia de tarjetas (Modo Simulación).
 */
public class TarjetaRepositoryMemory implements TarjetaPersistencePort {

    private static final Map<String, TarjetaCredito> tarjetasPorNumero = new HashMap<>();
    private static final Map<Integer, TarjetaCredito> tarjetasPorCliente = new HashMap<>();

    public TarjetaRepositoryMemory() {
        // Tarjeta por defecto para el cliente "prueba" (ID: 12345)
        TarjetaCredito tcPrueba = new TarjetaCredito("T900800700", 5000000.0, 0.0);
        crearTarjeta(tcPrueba, 12345);
    }

    @Override
    public TarjetaCredito buscarTarjetaPorCliente(int clienteId) {
        return tarjetasPorCliente.get(clienteId);
    }

    @Override
    public void crearTarjeta(TarjetaCredito tarjeta, int clienteId) {
        if (tarjeta == null) return;
        tarjetasPorNumero.put(tarjeta.getNumeroCuenta(), tarjeta);
        tarjetasPorCliente.put(clienteId, tarjeta);
    }

    @Override
    public void actualizarCupoYDeuda(String numeroTarjeta, double nuevoCupo, double nuevaDeuda) {
        TarjetaCredito tc = tarjetasPorNumero.get(numeroTarjeta);
        if (tc != null) {
            tc.setCupoDisponible(nuevoCupo);
            tc.setDeudaActual(nuevaDeuda);
        }
    }

    @Override
    public boolean existeNumeroTarjeta(String numeroTarjeta) {
        return tarjetasPorNumero.containsKey(numeroTarjeta);
    }
}
