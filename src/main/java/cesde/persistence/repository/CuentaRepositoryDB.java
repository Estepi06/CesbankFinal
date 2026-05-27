package cesde.persistence.repository;

import cesde.domain.Cuenta;
import cesde.persistence.mapper.RowMapper;
import cesde.service.portoutput.CuentaPersistencePort;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Adaptador de persistencia para la tabla 'cuentas' en la base de datos MySQL.
 */
public class CuentaRepositoryDB implements CuentaPersistencePort {

    private final Connection connection;
    private final RowMapper<Cuenta> rowMapper;

    public CuentaRepositoryDB(Connection connection, RowMapper<Cuenta> rowMapper) {
        this.connection = connection;
        this.rowMapper = rowMapper;
    }

    @Override
    public Cuenta buscarCuentaPorClienteYTipo(int clienteId, String tipoCuenta) {
        String sql = "SELECT * FROM cuentas WHERE cliente_id = ? AND tipo_cuenta = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, clienteId);
            ps.setString(2, tipoCuenta.toUpperCase());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rowMapper.mapRow(rs);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al buscar cuenta del cliente", e);
        }
        return null;
    }

    @Override
    public void crearCuenta(Cuenta cuenta, int clienteId, String tipoCuenta) {
        String sql = "INSERT INTO cuentas (numero_cuenta, cliente_id, saldo, tipo_cuenta, fecha_apertura) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, cuenta.getNumeroCuenta());
            ps.setInt(2, clienteId);
            ps.setDouble(3, cuenta.getSaldo());
            ps.setString(4, tipoCuenta.toUpperCase());
            ps.setDate(5, java.sql.Date.valueOf(cuenta.getFechaApertura() != null ? cuenta.getFechaApertura() : java.time.LocalDate.now()));
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error al registrar cuenta en la base de datos", e);
        }
    }

    @Override
    public void actualizarSaldo(String numeroCuenta, double nuevoSaldo) {
        String sql = "UPDATE cuentas SET saldo = ? WHERE numero_cuenta = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setDouble(1, nuevoSaldo);
            ps.setString(2, numeroCuenta);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error al actualizar saldo de la cuenta", e);
        }
    }

    @Override
    public boolean existeNumeroCuenta(String numeroCuenta) {
        String sql = "SELECT COUNT(*) FROM cuentas WHERE numero_cuenta = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, numeroCuenta);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al verificar número de cuenta", e);
        }
        return false;
    }

    @Override
    public Cuenta buscarCuentaPorNumero(String numeroCuenta) {
        String sql = "SELECT * FROM cuentas WHERE numero_cuenta = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, numeroCuenta);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rowMapper.mapRow(rs);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al buscar cuenta por número", e);
        }
        return null;
    }
}
