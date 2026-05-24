package cesde.util;

import java.util.InputMismatchException;
import java.util.Scanner;

/**
 * Utilidad para la lectura y validación de tipos de datos por consola.
 * Evita excepciones por entrada incorrecta y limpia el búfer del Scanner de forma segura.
 */
public class TypeValidator {

    private static final Scanner sc = new Scanner(System.in);

    public static int validateInt(String prompt) {
        while (true) {
            try {
                System.out.println(prompt);
                int value = sc.nextInt();
                sc.nextLine(); // Limpiar el salto de línea
                return value;
            } catch (InputMismatchException e) {
                System.out.println("Error: Ingrese un número entero válido.");
                sc.nextLine(); // Limpiar búfer en caso de error
            }
        }
    }

    public static double validateDouble(String prompt) {
        while (true) {
            try {
                System.out.println(prompt);
                double value = sc.nextDouble();
                sc.nextLine(); // Limpiar el salto de línea
                return value;
            } catch (InputMismatchException e) {
                System.out.println("Error: Ingrese un número decimal válido.");
                sc.nextLine(); // Limpiar búfer en caso de error
            }
        }
    }

    public static String validateString(String prompt) {
        while (true) {
            System.out.println(prompt);
            String value = sc.nextLine().trim();
            if (!value.isEmpty()) {
                return value;
            }
            System.out.println("Error: El campo no puede estar vacío.");
        }
    }
}
