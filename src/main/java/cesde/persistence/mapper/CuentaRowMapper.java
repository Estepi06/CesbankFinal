package cesde.persistence.mapper;

import cesde.domain.Cuenta;
import cesde.domain.CuentaAhorros;
import cesde.domain.CuentaCorriente;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Mapeador de filas SQL de la tabla 'cuentas' que decide polimórficamente
 * si instanciar una CuentaAhorros o una CuentaCorriente basándose en 'tipo_cuenta'.
 * También recupera y setea la fecha de apertura.
 */
public class CuentaRowMapper implements RowMapper<Cuenta> {

    @Override
    public Cuenta mapRow(ResultSet rs) throws SQLException {
        String tipoCuenta = rs.getString("tipo_cuenta");
        Cuenta cuenta;

        if ("AHORROS".equalsIgnoreCase(tipoCuenta)) {
            cuenta = new CuentaAhorros();
        } else if ("CORRIENTE".equalsIgnoreCase(tipoCuenta)) {
            cuenta = new CuentaCorriente();
        } else {
            cuenta = new Cuenta();
        }

        cuenta.setNumeroCuenta(rs.getString("numero_cuenta"));
        cuenta.setSaldo(rs.getDouble("saldo"));
        
        java.sql.Date fAp = rs.getDate("fecha_apertura");
        if (fAp != null) {
            cuenta.setFechaApertura(fAp.toLocalDate());
        }
        
        return cuenta;
    }
}
