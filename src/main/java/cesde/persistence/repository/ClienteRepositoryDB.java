package cesde.persistence.repository;

import cesde.domain.Cliente;
import cesde.domain.CuentaAhorros;
import cesde.domain.CuentaCorriente;
import cesde.domain.TarjetaCredito;
import cesde.persistence.mapper.RowMapper;
import cesde.service.portoutput.ClientePersistencePort;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Adaptador de persistencia para la tabla 'clientes' en la base de datos MySQL.
 * Utiliza consultas JOIN optimizadas estilo Lucia Store para evitar N+1 queries.
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
        // SQL optimizado con LEFT JOINs para obtener toda la información y productos del cliente en un solo viaje
        String sql = "SELECT cl.id, cl.nombre_completo, cl.celular, cl.usuario, cl.clave, cl.intentos_fallidos, cl.bloqueado, cl.fecha_registro, cl.fecha_nacimiento, " +
                     "cu.numero_cuenta, cu.saldo, cu.tipo_cuenta, cu.fecha_apertura, " +
                     "t.numero_tarjeta, t.cupo_disponible, t.deuda_actual, t.fecha_expedicion " +
                     "FROM clientes cl " +
                     "LEFT JOIN cuentas cu ON cl.id = cu.cliente_id " +
                     "LEFT JOIN tarjetas t ON cl.id = t.cliente_id " +
                     "WHERE cl.usuario = ?";
                     
        Cliente cliente = null;
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, usuario);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    if (cliente == null) {
                        cliente = new Cliente();
                        cliente.setId(rs.getInt("id"));
                        cliente.setNombreCompleto(rs.getString("nombre_completo"));
                        cliente.setCelular(rs.getString("celular"));
                        cliente.setUsuario(rs.getString("usuario"));
                        cliente.setClave(rs.getString("clave"));
                        cliente.setIntentosFallidos(rs.getInt("intentos_fallidos"));
                        cliente.setBloqueado(rs.getByte("bloqueado") == 1);
                        
                        java.sql.Date fReg = rs.getDate("fecha_registro");
                        if (fReg != null) {
                            cliente.setFechaRegistro(fReg.toLocalDate());
                        }
                        java.sql.Date fNac = rs.getDate("fecha_nacimiento");
                        if (fNac != null) {
                            cliente.setFechaNacimiento(fNac.toLocalDate());
                        }
                    }
                    
                    // Mapear Cuenta si existe en esta fila del JOIN
                    String tipoCuenta = rs.getString("tipo_cuenta");
                    if (tipoCuenta != null) {
                        if ("AHORROS".equalsIgnoreCase(tipoCuenta)) {
                            CuentaAhorros ahorro = new CuentaAhorros();
                            ahorro.setNumeroCuenta(rs.getString("numero_cuenta"));
                            ahorro.setSaldo(rs.getDouble("saldo"));
                            java.sql.Date fAp = rs.getDate("fecha_apertura");
                            if (fAp != null) {
                                ahorro.setFechaApertura(fAp.toLocalDate());
                            }
                            cliente.setCuentaAhorros(ahorro);
                        } else if ("CORRIENTE".equalsIgnoreCase(tipoCuenta)) {
                            CuentaCorriente corriente = new CuentaCorriente();
                            corriente.setNumeroCuenta(rs.getString("numero_cuenta"));
                            corriente.setSaldo(rs.getDouble("saldo"));
                            java.sql.Date fAp = rs.getDate("fecha_apertura");
                            if (fAp != null) {
                                corriente.setFechaApertura(fAp.toLocalDate());
                            }
                            cliente.setCuentaCorriente(corriente);
                        }
                    }
                    
                    // Mapear Tarjeta si existe en esta fila del JOIN
                    String numeroTarjeta = rs.getString("numero_tarjeta");
                    if (numeroTarjeta != null) {
                        TarjetaCredito tc = new TarjetaCredito();
                        tc.setNumeroCuenta(numeroTarjeta);
                        tc.setCupoDisponible(rs.getDouble("cupo_disponible"));
                        tc.setDeudaActual(rs.getDouble("deuda_actual"));
                        java.sql.Date fExp = rs.getDate("fecha_expedicion");
                        if (fExp != null) {
                            tc.setFechaExpedicion(fExp.toLocalDate());
                        }
                        cliente.setTarjetaCredito(tc);
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al buscar cliente por usuario con JOIN", e);
        }
        return cliente;
    }

    @Override
    public void crearCliente(Cliente cliente) {
        String sql = "INSERT INTO clientes (id, nombre_completo, celular, usuario, clave, intentos_fallidos, bloqueado, fecha_registro, fecha_nacimiento) " +
                     "VALUES (?, ?, ?, ?, ?, 0, 0, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, cliente.getId());
            ps.setString(2, cliente.getNombreCompleto());
            ps.setString(3, cliente.getCelular());
            ps.setString(4, cliente.getUsuario());
            ps.setString(5, cliente.getClave());
            
            ps.setDate(6, java.sql.Date.valueOf(cliente.getFechaRegistro() != null ? cliente.getFechaRegistro() : java.time.LocalDate.now()));
            ps.setDate(7, cliente.getFechaNacimiento() != null ? java.sql.Date.valueOf(cliente.getFechaNacimiento()) : null);
            
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
