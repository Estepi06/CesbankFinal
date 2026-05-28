package cesde.persistence.mapper;

import cesde.domain.Transaccion;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Mapeador de filas SQL de la tabla 'transacciones' a objetos de la clase Transaccion.
 */
public class TransaccionRowMapper implements RowMapper<Transaccion> {

    @Override
    public Transaccion mapRow(ResultSet rs) throws SQLException {
        Transaccion t = new Transaccion();
        t.setId(rs.getInt("id"));
        t.setNumeroCuenta(rs.getString("numero_cuenta"));
        t.setTipo(rs.getString("tipo"));
        t.setMonto(rs.getDouble("monto"));
        
        java.sql.Date fechaSql = rs.getDate("fecha");
        if (fechaSql != null) {
            t.setFecha(fechaSql.toLocalDate());
        }
        
        t.setDescripcion(rs.getString("descripcion"));
        return t;
    }
}
