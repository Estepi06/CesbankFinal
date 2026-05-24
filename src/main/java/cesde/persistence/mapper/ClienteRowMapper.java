package cesde.persistence.mapper;

import cesde.domain.Cliente;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Mapeador de filas SQL de la tabla 'clientes' a objetos de la clase Cliente.
 */
public class ClienteRowMapper implements RowMapper<Cliente> {

    @Override
    public Cliente mapRow(ResultSet rs) throws SQLException {
        Cliente cliente = new Cliente();
        cliente.setId(rs.getInt("id"));
        cliente.setNombreCompleto(rs.getString("nombre_completo"));
        cliente.setCelular(rs.getString("celular"));
        cliente.setUsuario(rs.getString("usuario"));
        cliente.setClave(rs.getString("clave"));
        cliente.setIntentosFallidos(rs.getInt("intentos_fallidos"));
        cliente.setBloqueado(rs.getByte("bloqueado") == 1);
        return cliente;
    }
}
