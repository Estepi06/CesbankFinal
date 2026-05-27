package cesde.util.date;

import cesde.util.TypeValidator;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

public class DateValidator {

    private DateValidator() {
    }

    public static LocalDate readDate(String prompt) {
        return readDate(prompt, DateFormats.DEFAULT);
    }

    public static LocalDate readDate(String prompt, List<String> patterns) {
        String hint = String.join("|", patterns);

        while (true) {
            System.out.printf("%s (%s): ", prompt, hint);
            String input = TypeValidator.validateString("");

            try {
                return DateParser.parse(input, patterns);
            } catch (DateTimeParseException e) {
                System.out.println("Error: Formato de fecha inválido. Intente nuevamente.");
            }
        }
    }
}
