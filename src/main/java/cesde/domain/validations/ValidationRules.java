package cesde.domain.validations;

import java.util.function.Predicate;

public class ValidationRules {

    private ValidationRules() {
    }

    // IDs y números
    public static final Predicate<Integer> VALID_ID = val -> val > 0 && String.valueOf(val).length() >= 5;
    public static final Predicate<Double> POSITIVE_AMOUNT = val -> val >= 0;

    // Texto
    public static final Predicate<String> MIN_LENGTH_3 = val -> val.trim().length() >= 3;
    public static final Predicate<String> NO_NUMBERS = val -> val.matches("[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]+");
    public static final Predicate<String> VALID_NAME = MIN_LENGTH_3.and(NO_NUMBERS);

    // Celular (exactamente 10 dígitos)
    public static final Predicate<String> VALID_PHONE = val -> val.matches("^\\d{10}$");

    // Usuario (mínimo 4 caracteres)
    public static final Predicate<String> VALID_USERNAME = val -> val.trim().length() >= 4;

    // Contraseña (mínimo 4 caracteres)
    public static final Predicate<String> VALID_PASSWORD = val -> val.length() >= 4;
}
