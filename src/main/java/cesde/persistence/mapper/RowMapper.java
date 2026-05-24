package cesde.persistence.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Interfaz genérica para mapear filas de un ResultSet a objetos del dominio.
 */
public interface RowMapper<T> {
    T mapRow(ResultSet rs) throws SQLException;
}
