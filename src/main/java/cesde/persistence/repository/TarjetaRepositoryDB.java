package cesde.persistence.repository;

import cesde.domain.TarjetaCredito;
import cesde.persistence.mapper.RowMapper;
import cesde.service.portoutput.TarjetaPersistencePort;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Adaptador de persistencia para la tabla 'tarjetas' en la base de datos MySQL.
 */
public class TarjetaRepositoryDB implements TarjetaPersistencePort {

    private final Connection connection;
    private final RowMapper<TarjetaCredito> rowMapper;

    public TarjetaRepositoryDB(Connection connection, RowMapper<TarjetaCredito> rowMapper) {
        this.connection = connection;
        this.rowMapper = rowMapper;
    }

    @Override
    public TarjetaCredito buscarTarjetaPorCliente(int clienteId) {
        String sql = "SELECT * FROM tarjetas WHERE cliente_id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, clienteId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rowMapper.mapRow(rs);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al buscar tarjeta de crédito del cliente", e);
        }
        return null;
    }

    @Override
    public void crearTarjeta(TarjetaCredito tarjeta, int clienteId) {
        String sql = "INSERT INTO tarjetas (numero_tarjeta, cliente_id, cupo_disponible, deuda_actual) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, tarjeta.getNumeroCuenta());
            ps.setInt(2, clienteId);
            ps.setDouble(3, tarjeta.getCupoDisponible());
            ps.setDouble(4, tarjeta.getDeudaActual());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error al registrar tarjeta de crédito en la base de datos", e);
        }
    }

    @Override
    public void actualizarCupoYDeuda(String numeroTarjeta, double nuevoCupo, double nuevaDeuda) {
        String sql = "UPDATE tarjetas SET cupo_disponible = ?, deuda_actual = ? WHERE numero_tarjeta = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setDouble(1, nuevoCupo);
            ps.setDouble(2, nuevaDeuda);
            ps.setString(3, numeroTarjeta);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error al actualizar la tarjeta de crédito", e);
        }
    }

    @Override
    public boolean existeNumeroTarjeta(String numeroTarjeta) {
        // Consulta la base de datos para verificar si el número de tarjeta ya está registrado
        String sql = "SELECT COUNT(*) FROM tarjetas WHERE numero_tarjeta = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, numeroTarjeta);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0; // Si COUNT(*) > 0, el número ya existe
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al verificar número de tarjeta", e);
        }
        return false;
    }
}
