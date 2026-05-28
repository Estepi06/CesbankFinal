package cesde.persistence.mapper;

import cesde.domain.TarjetaCredito;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Mapeador de filas SQL de la tabla 'tarjetas' a objetos de la clase TarjetaCredito.
 * También recupera y setea la fecha de expedición.
 */
public class TarjetaRowMapper implements RowMapper<TarjetaCredito> {

    @Override
    public TarjetaCredito mapRow(ResultSet rs) throws SQLException {
        TarjetaCredito tarjeta = new TarjetaCredito();
        tarjeta.setNumeroCuenta(rs.getString("numero_tarjeta"));
        tarjeta.setCupoDisponible(rs.getDouble("cupo_disponible"));
        tarjeta.setDeudaActual(rs.getDouble("deuda_actual"));
        
        java.sql.Date fExp = rs.getDate("fecha_expedicion");
        if (fExp != null) {
            tarjeta.setFechaExpedicion(fExp.toLocalDate());
        }
        
        return tarjeta;
    }
}
