package cesde.util.date;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

public class DateParser {

    private DateParser() {
    }

    public static LocalDate parse(String input, List<String> patterns) {
        for (String pattern : patterns) {
            try {
                return LocalDate.parse(input.trim(), DateTimeFormatter.ofPattern(pattern));
            } catch (DateTimeParseException ignored) { }
        }
        throw new DateTimeParseException(
                "No se pudo parsear " + input + " con los formatos " + patterns, input, 0
        );
    }

    public static LocalDate parse(String input) {
        return parse(input, DateFormats.DEFAULT);
    }
}
