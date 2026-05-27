package cesde.domain.validations;

import java.time.LocalDate;

public class DateValidationRules {

    private DateValidationRules() {
    }

    public static void validateNotFuture(LocalDate date, String fieldName) {
        if (date.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException(fieldName + " no puede ser una fecha futura.");
        }
    }

    public static void validateMinAge(LocalDate birthDate, int minAge, String fieldName) {
        if (birthDate.isAfter(LocalDate.now().minusYears(minAge))) {
            throw new IllegalArgumentException(fieldName + " debe indicar que es mayor de " + minAge + " años para abrir productos bancarios.");
        }
    }

    public static void validateNotOlderThan(LocalDate date, int days, String fieldName) {
        if (date.isBefore(LocalDate.now().minusDays(days))) {
            throw new IllegalArgumentException(fieldName + " no puede ser una fecha anterior a " + days + " días.");
        }
    }

    public static void validateRange(LocalDate date, LocalDate from, LocalDate to, String fieldName) {
        if (date.isBefore(from) || date.isAfter(to)) {
            throw new IllegalArgumentException(fieldName + " debe estar entre " + from + " y " + to + ".");
        }
    }
}
