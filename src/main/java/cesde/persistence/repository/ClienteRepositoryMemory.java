package cesde.persistence.repository;

import cesde.domain.Cliente;
import cesde.service.portoutput.ClientePersistencePort;

import java.util.HashMap;
import java.util.Map;

/**
 * Implementación en memoria del puerto de persistencia de clientes (Modo Simulación).
 */
public class ClienteRepositoryMemory implements ClientePersistencePort {

    private static final Map<String, Cliente> clientesPorUsuario = new HashMap<>();
    private static final Map<Integer, Cliente> clientesPorId = new HashMap<>();

    public ClienteRepositoryMemory() {
        // Registrar un cliente de prueba por defecto
        Cliente prueba = new Cliente(
            12345, "Usuario de Prueba", "3001234567", "prueba", "1234", 0, false
        );
        crearCliente(prueba);
    }

    @Override
    public Cliente buscarPorUsuario(String usuario) {
        return clientesPorUsuario.get(usuario);
    }

    @Override
    public void crearCliente(Cliente cliente) {
        if (cliente == null) return;
        clientesPorUsuario.put(cliente.getUsuario(), cliente);
        clientesPorId.put(cliente.getId(), cliente);
    }

    @Override
    public void actualizarIntentosFallidos(int id, int intentos) {
        Cliente c = clientesPorId.get(id);
        if (c != null) {
            c.setIntentosFallidos(intentos);
        }
    }

    @Override
    public void bloquearCliente(int id) {
        Cliente c = clientesPorId.get(id);
        if (c != null) {
            c.setBloqueado(true);
        }
    }

    @Override
    public void resetearIntentosFallidos(int id) {
        Cliente c = clientesPorId.get(id);
        if (c != null) {
            c.setIntentosFallidos(0);
            c.setBloqueado(false);
        }
    }
}
