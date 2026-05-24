package cesde.persistence.mapper;

import cesde.domain.TarjetaCredito;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Mapeador de filas SQL de la tabla 'tarjetas' a objetos de la clase TarjetaCredito.
 */
public class TarjetaRowMapper implements RowMapper<TarjetaCredito> {

    @Override
    public TarjetaCredito mapRow(ResultSet rs) throws SQLException {
        TarjetaCredito tarjeta = new TarjetaCredito();
        tarjeta.setNumeroCuenta(rs.getString("numero_tarjeta"));
        tarjeta.setCupoDisponible(rs.getDouble("cupo_disponible"));
        tarjeta.setDeudaActual(rs.getDouble("deuda_actual"));
        return tarjeta;
    }
}
