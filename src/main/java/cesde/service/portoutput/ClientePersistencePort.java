package cesde.service.portoutput;

import cesde.domain.Cliente;

/**
 * Puerto de persistencia de salida para las operaciones de Cliente en base de datos.
 */
public interface ClientePersistencePort {
    Cliente buscarPorUsuario(String usuario);
    void crearCliente(Cliente cliente);
    void actualizarIntentosFallidos(int id, int intentos);
    void bloquearCliente(int id);
    void resetearIntentosFallidos(int id);
}
