package cesde.persistence.repository;

import cesde.domain.Cliente;
import cesde.persistence.mapper.RowMapper;
import cesde.service.portoutput.ClientePersistencePort;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Adaptador de persistencia para la tabla 'clientes' en la base de datos MySQL.
 */
public class ClienteRepositoryDB implements ClientePersistencePort {

    private final Connection connection;
    private final RowMapper<Cliente> rowMapper;

    public ClienteRepositoryDB(Connection connection, RowMapper<Cliente> rowMapper) {
        this.connection = connection;
        this.rowMapper = rowMapper;
    }

    @Override
    public Cliente buscarPorUsuario(String usuario) {
        String sql = "SELECT * FROM clientes WHERE usuario = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, usuario);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rowMapper.mapRow(rs);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al buscar cliente por usuario", e);
        }
        return null;
    }

    @Override
    public void crearCliente(Cliente cliente) {
        String sql = "INSERT INTO clientes (id, nombre_completo, celular, usuario, clave, intentos_fallidos, bloqueado) VALUES (?, ?, ?, ?, ?, 0, 0)";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, cliente.getId());
            ps.setString(2, cliente.getNombreCompleto());
            ps.setString(3, cliente.getCelular());
            ps.setString(4, cliente.getUsuario());
            ps.setString(5, cliente.getClave());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error al registrar el cliente en la base de datos", e);
        }
    }

    @Override
    public void actualizarIntentosFallidos(int id, int intentos) {
        String sql = "UPDATE clientes SET intentos_fallidos = ? WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, intentos);
            ps.setInt(2, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error al actualizar intentos fallidos", e);
        }
    }

    @Override
    public void bloquearCliente(int id) {
        String sql = "UPDATE clientes SET bloqueado = 1 WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error al bloquear cliente", e);
        }
    }

    @Override
    public void resetearIntentosFallidos(int id) {
        String sql = "UPDATE clientes SET intentos_fallidos = 0, bloqueado = 0 WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error al resetear intentos fallidos", e);
        }
    }
}
