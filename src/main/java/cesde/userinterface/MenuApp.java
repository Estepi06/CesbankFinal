package cesde.userinterface;

import cesde.domain.Cliente;
import cesde.util.TypeValidator;
import cesde.view.BankView;

/**
 * Menú principal interactivo de la aplicación por consola utilizando Scanner.
 * Actualizado con la opción 7 para consultar el historial de transacciones.
 */
public class MenuApp {

    private final BankView bankView;

    public MenuApp(BankView bankView) {
        this.bankView = bankView;
    }

    /**
     * Menú principal de inicio del cajero automático.
     */
    public void mainMenu() {
        System.out.println("=================================");
        System.out.println("   BIENVENIDO A CESBANK CAJERO  ");
        System.out.println("=================================");

        int opcion = -1;
        while (opcion != 3) {
            System.out.println("\nSeleccione una opción:");
            System.out.println("1. Registrarse (Nuevo Cliente)");
            System.out.println("2. Iniciar Sesión");
            System.out.println("3. Salir del Sistema");
            
            opcion = TypeValidator.validateInt("Opción: ");

            switch (opcion) {
                case 1:
                    bankView.registrarNuevoCliente();
                    break;
                case 2:
                    Cliente cliente = bankView.login();
                    if (cliente != null) {
                        if (cliente.isBloqueado()) {
                            System.out.println("\n[Alerta] No se puede acceder. El usuario está bloqueado.");
                        } else {
                            menuApplication(cliente);
                        }
                    } else {
                        System.out.println("\n[Error] Inicio de sesión fallido. Clave incorrecta o usuario no encontrado.");
                    }
                    break;
                case 3:
                    System.out.println("\nGracias por utilizar los servicios de CesBank. ¡Hasta pronto!");
                    break;
                default:
                    System.out.println("\nError: Ingrese una opción válida.");
            }
        }
    }

    /**
     * Menú del cliente autenticado.
     * @param cliente El cliente con sesión activa.
     */
    private void menuApplication(Cliente cliente) {
        int opcion = -1;

        while (opcion != 8) {
            System.out.println("\n=================================");
            System.out.println("            MENÚ CESBANK         ");
            System.out.println("=================================");
            System.out.println("Hola, " + cliente.getNombreCompleto());
            System.out.println("---------------------------------");
            System.out.println("1. Ver Saldos / Cupos");
            System.out.println("2. Consignar (Ahorros / Corriente)");
            System.out.println("3. Retirar Dinero (Ahorros / Corriente)");
            System.out.println("4. Transferir entre Cuentas");
            System.out.println("5. Realizar Compra con Tarjeta");
            System.out.println("6. Pagar Tarjeta de Crédito");
            System.out.println("7. Consultar Historial de Transacciones");
            System.out.println("8. Cerrar Sesión");
            System.out.println("=================================");
            
            opcion = TypeValidator.validateInt("Seleccione una opción:");

            switch (opcion) {
                case 1:
                    bankView.mostrarSaldos(cliente);
                    break;
                case 2:
                    bankView.realizarConsignacion(cliente);
                    break;
                case 3:
                    bankView.realizarRetiro(cliente);
                    break;
                case 4:
                    bankView.realizarTransferencia(cliente);
                    break;
                case 5:
                    bankView.realizarCompraTarjeta(cliente);
                    break;
                case 6:
                    bankView.realizarPagoTarjeta(cliente);
                    break;
                case 7:
                    bankView.mostrarHistorialTransacciones(cliente);
                    break;
                case 8:
                    System.out.println("\nCerrando sesión de " + cliente.getNombreCompleto() + "...");
                    break;
                default:
                    System.out.println("\nError: Opción inválida.");
            }
        }
    }
}
