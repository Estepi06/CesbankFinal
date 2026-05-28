package cesde.util;

import java.util.InputMismatchException;
import java.util.Scanner;
import java.util.function.Predicate;

/**
 * Utilidad para la lectura y validación de tipos de datos por consola.
 * Evita excepciones por entrada incorrecta y limpia el búfer del Scanner de forma segura.
 * Incluye sobrecargas para validación basada en reglas de negocio (Predicates).
 */
public class TypeValidator {

    private static final Scanner sc = new Scanner(System.in);

    public static int validateInt(String prompt) {
        while (true) {
            try {
                if (!prompt.isEmpty()) {
                    System.out.println(prompt);
                }
                int value = sc.nextInt();
                sc.nextLine(); // Limpiar el salto de línea
                return value;
            } catch (InputMismatchException e) {
                System.out.println("Error: Ingrese un número entero válido.");
                sc.nextLine(); // Limpiar búfer en caso de error
            }
        }
    }

    public static int validateInt(String prompt, Predicate<Integer> rule, String errorMessage) {
        while (true) {
            int value = validateInt(prompt);
            if (rule.test(value)) {
                return value;
            }
            System.out.println(errorMessage);
        }
    }

    public static double validateDouble(String prompt) {
        while (true) {
            try {
                if (!prompt.isEmpty()) {
                    System.out.println(prompt);
                }
                double value = sc.nextDouble();
                sc.nextLine(); // Limpiar el salto de línea
                return value;
            } catch (InputMismatchException e) {
                System.out.println("Error: Ingrese un número decimal válido.");
                sc.nextLine(); // Limpiar búfer en caso de error
            }
        }
    }

    public static double validateDouble(String prompt, Predicate<Double> rule, String errorMessage) {
        while (true) {
            double value = validateDouble(prompt);
            if (rule.test(value)) {
                return value;
            }
            System.out.println(errorMessage);
        }
    }

    public static String validateString(String prompt) {
        while (true) {
            if (!prompt.isEmpty()) {
                System.out.println(prompt);
            }
            String value = sc.nextLine().trim();
            if (!value.isEmpty()) {
                return value;
            }
            System.out.println("Error: El campo no puede estar vacío.");
        }
    }

    public static String validateString(String prompt, Predicate<String> rule, String errorMessage) {
        while (true) {
            String value = validateString(prompt);
            if (rule.test(value)) {
                return value;
            }
            System.out.println(errorMessage);
        }
    }
}
