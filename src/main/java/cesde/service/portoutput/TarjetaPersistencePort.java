package cesde.service.portoutput;

import cesde.domain.TarjetaCredito;

/**
 * Puerto de persistencia de salida para las operaciones de Tarjeta de Crédito en base de datos.
 */
public interface TarjetaPersistencePort {
    TarjetaCredito buscarTarjetaPorCliente(int clienteId);
    void crearTarjeta(TarjetaCredito tarjeta, int clienteId);
    void actualizarCupoYDeuda(String numeroTarjeta, double nuevoCupo, double nuevaDeuda);
    boolean existeNumeroTarjeta(String numeroTarjeta);
}
