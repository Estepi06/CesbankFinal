package cesde.persistence.repository;

import cesde.domain.Transaccion;
import cesde.persistence.mapper.RowMapper;
import cesde.service.portoutput.TransaccionPersistencePort;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Adaptador de persistencia para la tabla 'transacciones' en la base de datos MySQL.
 */
public class TransaccionRepositoryDB implements TransaccionPersistencePort {

    private final Connection connection;
    private final RowMapper<Transaccion> rowMapper;

    public TransaccionRepositoryDB(Connection connection, RowMapper<Transaccion> rowMapper) {
        this.connection = connection;
        this.rowMapper = rowMapper;
    }

    @Override
    public void registrarTransaccion(Transaccion transaccion) {
        String sql = "INSERT INTO transacciones (numero_cuenta, tipo, monto, fecha, descripcion) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, transaccion.getNumeroCuenta());
            ps.setString(2, transaccion.getTipo());
            ps.setDouble(3, transaccion.getMonto());
            ps.setDate(4, java.sql.Date.valueOf(transaccion.getFecha() != null ? transaccion.getFecha() : java.time.LocalDate.now()));
            ps.setString(5, transaccion.getDescripcion());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error al registrar la transacción en la base de datos", e);
        }
    }

    @Override
    public List<Transaccion> obtenerHistorialPorCuenta(String numeroCuenta) {
        List<Transaccion> transacciones = new ArrayList<>();
        String sql = "SELECT * FROM transacciones WHERE numero_cuenta = ? ORDER BY id DESC";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, numeroCuenta);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    transacciones.add(rowMapper.mapRow(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al obtener el historial de transacciones", e);
        }
        return transacciones;
    }
}
