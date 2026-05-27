package cesde.util.date;

import java.util.List;

public class DateFormats {

    private DateFormats() {
    }

    public static final List<String> DEFAULT = List.of(
            "dd/MM/yyyy",    // Colombia estándar
            "d/M/yyyy",      // sin ceros: 5/9/2025
            "yyyy-MM-dd",    // ISO 8601
            "dd-MM-yyyy",    // con guiones
            "ddMMyyyy"       // sin separador
    );
}
