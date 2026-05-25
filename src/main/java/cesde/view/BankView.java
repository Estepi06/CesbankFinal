package cesde.view;

import cesde.domain.Cliente;
import cesde.domain.CuentaAhorros;
import cesde.domain.CuentaCorriente;
import cesde.domain.TarjetaCredito;
import cesde.service.BankServiceImpl;
import cesde.util.TypeValidator;

/**
 * Capa de Presentación (View) que interactúa con el usuario a través de consola
 * y consume el servicio del banco.
 */
public class BankView {

    private final BankServiceImpl bankServiceImpl;

    public BankView(BankServiceImpl bankServiceImpl) {
        this.bankServiceImpl = bankServiceImpl;
    }

    /**
     * Registra un nuevo cliente pidiendo solo los datos personales.
     * Los números de cuenta y tarjeta son AUTO-GENERADOS por el banco (no los ingresa el cliente).
     */
    public void registrarNuevoCliente() {
        System.out.println("\n=================================");
        System.out.println("       REGISTRO DE CLIENTE       ");
        System.out.println("=================================");

        int id = TypeValidator.validateInt("Ingrese su número de identificación (Cédula):");
        String nombre = TypeValidator.validateString("Ingrese su nombre completo:");
        String celular = TypeValidator.validateString("Ingrese su número de celular:");
        String usuario = TypeValidator.validateString("Cree su nombre de usuario (para iniciar sesión):");
        String clave = TypeValidator.validateString("Cree su contraseña de acceso:");

        // Construir el cliente con los datos personales
        Cliente cliente = new Cliente(id, nombre, celular, usuario, clave, 0, false);

        // El número de cuenta lo genera el banco automáticamente.
        // Solo se pregunta si el cliente QUIERE el producto.

        String tieneAhorros = TypeValidator.validateString("¿Desea abrir una Cuenta de Ahorros? (S/N):");
        if ("S".equalsIgnoreCase(tieneAhorros)) {
            double saldoAhorros = TypeValidator.validateDouble("Ingrese el monto de apertura (saldo inicial):");
            // Número generado por el banco → se asigna en BankServiceImpl, aquí va vacío temporalmente
            cliente.setCuentaAhorros(new CuentaAhorros("", saldoAhorros));
        }

        String tieneCorriente = TypeValidator.validateString("¿Desea abrir una Cuenta Corriente? (S/N):");
        if ("S".equalsIgnoreCase(tieneCorriente)) {
            double saldoCorriente = TypeValidator.validateDouble("Ingrese el monto de apertura (saldo inicial):");
            // Número generado por el banco → se asigna en BankServiceImpl, aquí va vacío temporalmente
            cliente.setCuentaCorriente(new CuentaCorriente("", saldoCorriente));
        }

        String tieneTarjeta = TypeValidator.validateString("¿Desea solicitar una Tarjeta de Crédito? (S/N):");
        if ("S".equalsIgnoreCase(tieneTarjeta)) {
            // El cupo aprobado es fijo por política del banco: $5.000.000
            // El número de tarjeta es generado por el banco → se asigna en BankServiceImpl
            double cupoFijo = 5000000.0;
            System.out.println("[Banco] Cupo aprobado para su Tarjeta de Crédito: $5.000.000");
            cliente.setTarjetaCredito(new TarjetaCredito("", cupoFijo, 0.0));
        }

        try {
            bankServiceImpl.registrarCliente(cliente);
            System.out.println("\n╔══════════════════════════════════════╗");
            System.out.println("║  ¡Registro exitoso! Ya puede         ║");
            System.out.println("║  iniciar sesión con su usuario.       ║");
            System.out.println("║  Guarde sus números de productos.     ║");
            System.out.println("╚══════════════════════════════════════╝");
        } catch (Exception e) {
            System.out.println("\n[Error] No se pudo completar el registro.");
            System.out.println("Causa posible: La cédula o el usuario ya están registrados en el sistema.");
        }
    }


    /**
     * Captura credenciales y realiza el login en el sistema.
     * @return El cliente autenticado o null en caso de fallo.
     */
    public Cliente login() {
        System.out.println("\n=================================");
        System.out.println("       INICIAR SESIÓN - CESBANK  ");
        System.out.println("=================================");
        
        String usuario = TypeValidator.validateString("Ingrese su usuario:");
        String clave = TypeValidator.validateString("Ingrese su contraseña:");

        return bankServiceImpl.login(usuario, clave);
    }

    /**
     * Muestra de forma estética los saldos y cupos de los productos financieros del cliente.
     */
    public void mostrarSaldos(Cliente cliente) {
        // Cargar productos actualizados de la base de datos
        bankServiceImpl.cargarProductosCliente(cliente);

        System.out.println("\n==================================================");
        System.out.println("             CONSULTA DE SALDOS - CESBANK         ");
        System.out.println("==================================================");
        System.out.println("Cliente: " + cliente.getNombreCompleto());
        System.out.println("--------------------------------------------------");

        CuentaAhorros ahorro = cliente.getCuentaAhorros();
        if (ahorro != null) {
            System.out.printf("  * Cuenta de Ahorros (%s) : $%,.2f%n", ahorro.getNumeroCuenta(), ahorro.getSaldo());
        } else {
            System.out.println("  * Cuenta de Ahorros      : No posee");
        }

        CuentaCorriente corriente = cliente.getCuentaCorriente();
        if (corriente != null) {
            System.out.printf("  * Cuenta Corriente  (%s) : $%,.2f%n", corriente.getNumeroCuenta(), corriente.getSaldo());
        } else {
            System.out.println("  * Cuenta Corriente       : No posee");
        }

        TarjetaCredito tc = cliente.getTarjetaCredito();
        if (tc != null) {
            System.out.printf("  * Tarjeta Crédito   (%s) : Cupo Disponible: $%,.2f | Deuda: $%,.2f%n", 
                    tc.getNumeroCuenta(), tc.getCupoDisponible(), tc.getDeudaActual());
        } else {
            System.out.println("  * Tarjeta de Crédito     : No posee");
        }
        System.out.println("==================================================");
    }

    /**
     * Captura datos y realiza una consignación.
     */
    public void realizarConsignacion(Cliente cliente) {
        System.out.println("\n--- REALIZAR CONSIGNACIÓN ---");
        System.out.println("1. Cuenta de Ahorros");
        System.out.println("2. Cuenta Corriente");
        System.out.println("3. Cancelar");
        
        int opcion = TypeValidator.validateInt("Seleccione la cuenta de destino:");
        String tipoCuenta = "";

        if (opcion == 1) tipoCuenta = "AHORROS";
        else if (opcion == 2) tipoCuenta = "CORRIENTE";
        else {
            System.out.println("Operación cancelada.");
            return;
        }

        double monto = TypeValidator.validateDouble("Ingrese el monto a consignar:");
        bankServiceImpl.consignar(cliente, tipoCuenta, monto);
    }

    /**
     * Captura datos y realiza un retiro.
     */
    public void realizarRetiro(Cliente cliente) {
        System.out.println("\n--- REALIZAR RETIRO ---");
        System.out.println("1. Cuenta de Ahorros (Comisión del 1.5%)");
        System.out.println("2. Cuenta Corriente (Permite sobregiro)");
        System.out.println("3. Cancelar");

        int opcion = TypeValidator.validateInt("Seleccione el producto de origen:");
        String tipoCuenta = "";

        if (opcion == 1) tipoCuenta = "AHORROS";
        else if (opcion == 2) tipoCuenta = "CORRIENTE";
        else {
            System.out.println("Operación cancelada.");
            return;
        }

        double monto = TypeValidator.validateDouble("Ingrese el monto a retirar:");
        bankServiceImpl.retirar(cliente, tipoCuenta, monto);
    }

    /**
     * Captura datos y realiza una transferencia desde una de las cuentas del cliente hacia otra cuenta por número.
     */
    public void realizarTransferencia(Cliente cliente) {
        System.out.println("\n--- REALIZAR TRANSFERENCIA ---");
        System.out.println("1. Desde Cuenta de Ahorros");
        System.out.println("2. Desde Cuenta Corriente");
        System.out.println("3. Cancelar");

        int opcion = TypeValidator.validateInt("Seleccione la cuenta de origen:");
        String tipoCuenta = "";

        if (opcion == 1) tipoCuenta = "AHORROS";
        else if (opcion == 2) tipoCuenta = "CORRIENTE";
        else {
            System.out.println("Operación cancelada.");
            return;
        }

        String numeroDestino = TypeValidator.validateString("Ingrese el número de cuenta destino (ej. C123456789):");
        double monto = TypeValidator.validateDouble("Ingrese el monto a transferir:");

        String confirmar = TypeValidator.validateString("¿Confirmar transferencia? (S/N):");
        if ("S".equalsIgnoreCase(confirmar)) {
            bankServiceImpl.transferir(cliente, tipoCuenta, numeroDestino, monto);
        } else {
            System.out.println("Transferencia cancelada por el usuario.");
        }
    }

    /**
     * Proceso de compra diferido a cuotas con la tarjeta de crédito.
     */
    public void realizarCompraTarjeta(Cliente cliente) {
        TarjetaCredito tc = cliente.getTarjetaCredito();
        if (tc == null) {
            System.out.println("\n[Error] No tienes una Tarjeta de Crédito registrada.");
            return;
        }

        System.out.println("\n--- COMPRA CON TARJETA DE CRÉDITO ---");
        System.out.printf("Cupo disponible actual: $%,.2f%n", tc.getCupoDisponible());
        double monto = TypeValidator.validateDouble("Ingrese el valor de la compra:");

        if (monto > tc.getCupoDisponible()) {
            System.out.printf("[Rechazado] Compra declinada. El monto supera su cupo disponible de $%,.2f.%n", tc.getCupoDisponible());
            return;
        }

        int cuotas = TypeValidator.validateInt("Ingrese el número de cuotas en las que desea diferir el pago (1 o más):");
        if (cuotas <= 0) {
            System.out.println("Error: El número de cuotas debe ser mayor a 0.");
            return;
        }

        double tasa = 0.0;
        if (cuotas >= 3 && cuotas <= 6) tasa = 1.9;
        else if (cuotas >= 7) tasa = 2.3;

        double cuotaMensual = tc.calcularCuotaMensual(monto, cuotas);

        System.out.println("\n--------------------------------------------------");
        System.out.println("             RESUMEN DE LA TRANSACCIÓN            ");
        System.out.println("--------------------------------------------------");
        System.out.printf("Valor Compra  : $%,.2f%n", monto);
        System.out.printf("Plazo         : %d cuotas%n", cuotas);
        System.out.printf("Tasa interés  : %.1f%% E.M.%n", tasa);
        System.out.printf("Pago Mensual  : $%,.2f%n", cuotaMensual);
        System.out.println("--------------------------------------------------");

        String confirmar = TypeValidator.validateString("¿Confirmar transacción? (S/N):");
        if ("S".equalsIgnoreCase(confirmar)) {
            bankServiceImpl.comprarConTarjeta(cliente, monto);
        } else {
            System.out.println("Compra cancelada por el usuario.");
        }
    }

    /**
     * Proceso de pago a la deuda de la tarjeta.
     */
    public void realizarPagoTarjeta(Cliente cliente) {
        TarjetaCredito tc = cliente.getTarjetaCredito();
        if (tc == null) {
            System.out.println("\n[Error] No tienes una Tarjeta de Crédito registrada.");
            return;
        }

        System.out.println("\n--- PAGAR TARJETA DE CRÉDITO ---");
        System.out.printf("Deuda actual: $%,.2f%n", tc.getDeudaActual());
        
        if (tc.getDeudaActual() <= 0) {
            System.out.println("Su tarjeta no tiene deudas pendientes.");
            return;
        }

        double monto = TypeValidator.validateDouble("Ingrese el valor del abono a realizar:");
        bankServiceImpl.pagarTarjeta(cliente, monto);
    }
}
