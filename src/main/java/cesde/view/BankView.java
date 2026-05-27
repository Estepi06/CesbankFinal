package cesde.view;

import cesde.domain.Cliente;
import cesde.domain.CuentaAhorros;
import cesde.domain.CuentaCorriente;
import cesde.domain.TarjetaCredito;
import cesde.domain.validations.DateValidationRules;
import cesde.domain.validations.ValidationRules;
import cesde.service.BankServiceImpl;
import cesde.util.TypeValidator;
import cesde.util.date.DateValidator;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Capa de Presentación (View) que interactúa con el usuario a través de consola
 * y consume el servicio del banco.
 * Utiliza validación de reglas avanzada y muestra información de fechas de productos.
 */
public class BankView {

    private final BankServiceImpl bankServiceImpl;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public BankView(BankServiceImpl bankServiceImpl) {
        this.bankServiceImpl = bankServiceImpl;
    }

    /**
     * Registra un nuevo cliente aplicando validación de reglas avanzadas estilo Lucia Store.
     */
    public void registrarNuevoCliente() {
        System.out.println("\n=================================");
        System.out.println("       REGISTRO DE CLIENTE       ");
        System.out.println("=================================");

        // Validaciones basadas en Predicados
        int id = TypeValidator.validateInt(
                "Ingrese su número de identificación (Cédula):",
                ValidationRules.VALID_ID,
                "Error: La identificación debe ser un número positivo de al menos 5 dígitos."
        );

        String nombre = TypeValidator.validateString(
                "Ingrese su nombre completo:",
                ValidationRules.VALID_NAME,
                "Error: El nombre debe tener al menos 3 letras y no contener números ni caracteres especiales."
        );

        String celular = TypeValidator.validateString(
                "Ingrese su número de celular (10 dígitos):",
                ValidationRules.VALID_PHONE,
                "Error: El celular debe contener exactamente 10 dígitos numéricos."
        );

        // Lectura y validación avanzada de fechas (Reglas de negocio bancarias)
        LocalDate fechaNacimiento = null;
        while (true) {
            try {
                fechaNacimiento = DateValidator.readDate("Ingrese su fecha de nacimiento");
                DateValidationRules.validateNotFuture(fechaNacimiento, "Fecha de nacimiento");
                DateValidationRules.validateMinAge(fechaNacimiento, 18, "Fecha de nacimiento");
                break; // Validación exitosa
            } catch (IllegalArgumentException e) {
                System.out.println("Error de validación: " + e.getMessage());
            }
        }

        String usuario = TypeValidator.validateString(
                "Cree su nombre de usuario (mínimo 4 caracteres para iniciar sesión):",
                ValidationRules.VALID_USERNAME,
                "Error: El nombre de usuario debe contener al menos 4 caracteres sin espacios."
        );

        String clave = TypeValidator.validateString(
                "Cree su contraseña de acceso (mínimo 4 caracteres):",
                ValidationRules.VALID_PASSWORD,
                "Error: La contraseña debe tener una longitud mínima de 4 caracteres."
        );

        // Construir el cliente incluyendo fechaNacimiento y fechaRegistro (LocalDate.now())
        Cliente cliente = new Cliente(id, nombre, celular, usuario, clave, 0, false, fechaNacimiento, LocalDate.now());

        String tieneAhorros = TypeValidator.validateString("¿Desea abrir una Cuenta de Ahorros? (S/N):");
        if ("S".equalsIgnoreCase(tieneAhorros)) {
            double saldoAhorros = TypeValidator.validateDouble(
                    "Ingrese el monto de apertura (saldo inicial mayor o igual a 0):",
                    ValidationRules.POSITIVE_AMOUNT,
                    "Error: El monto de apertura no puede ser negativo."
            );
            cliente.setCuentaAhorros(new CuentaAhorros("", saldoAhorros));
        }

        String tieneCorriente = TypeValidator.validateString("¿Desea abrir una Cuenta Corriente? (S/N):");
        if ("S".equalsIgnoreCase(tieneCorriente)) {
            double saldoCorriente = TypeValidator.validateDouble(
                    "Ingrese el monto de apertura (saldo inicial mayor o igual a 0):",
                    ValidationRules.POSITIVE_AMOUNT,
                    "Error: El monto de apertura no puede ser negativo."
            );
            cliente.setCuentaCorriente(new CuentaCorriente("", saldoCorriente));
        }

        String tieneTarjeta = TypeValidator.validateString("¿Desea solicitar una Tarjeta de Crédito? (S/N):");
        if ("S".equalsIgnoreCase(tieneTarjeta)) {
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
     * Muestra de forma estética los saldos y cupos de los productos, incluyendo fechas avanzadas.
     */
    public void mostrarSaldos(Cliente cliente) {
        // Cargar productos actualizados usando la consulta JOIN optimizada
        bankServiceImpl.cargarProductosCliente(cliente);

        System.out.println("\n=====================================================================");
        System.out.println("                   CONSULTA DE SALDOS - CESBANK                      ");
        System.out.println("=====================================================================");
        System.out.println("Cliente          : " + cliente.getNombreCompleto());
        System.out.println("Identificación   : " + cliente.getId());
        if (cliente.getFechaNacimiento() != null) {
            System.out.println("F. Nacimiento    : " + cliente.getFechaNacimiento().format(DATE_FORMATTER));
        }
        if (cliente.getFechaRegistro() != null) {
            System.out.println("Cliente desde    : " + cliente.getFechaRegistro().format(DATE_FORMATTER));
        }
        System.out.println("---------------------------------------------------------------------");

        CuentaAhorros ahorro = cliente.getCuentaAhorros();
        if (ahorro != null) {
            String fAp = (ahorro.getFechaApertura() != null) ? ahorro.getFechaApertura().format(DATE_FORMATTER) : "N/D";
            System.out.printf("  * Cuenta de Ahorros (%s) : Saldo: $%,.2f | Apertura: %s%n", 
                    ahorro.getNumeroCuenta(), ahorro.getSaldo(), fAp);
        } else {
            System.out.println("  * Cuenta de Ahorros      : No posee");
        }

        CuentaCorriente corriente = cliente.getCuentaCorriente();
        if (corriente != null) {
            String fAp = (corriente.getFechaApertura() != null) ? corriente.getFechaApertura().format(DATE_FORMATTER) : "N/D";
            System.out.printf("  * Cuenta Corriente  (%s) : Saldo: $%,.2f | Apertura: %s%n", 
                    corriente.getNumeroCuenta(), corriente.getSaldo(), fAp);
        } else {
            System.out.println("  * Cuenta Corriente       : No posee");
        }

        TarjetaCredito tc = cliente.getTarjetaCredito();
        if (tc != null) {
            String fExp = (tc.getFechaExpedicion() != null) ? tc.getFechaExpedicion().format(DATE_FORMATTER) : "N/D";
            System.out.printf("  * Tarjeta Crédito   (%s) : Cupo: $%,.2f | Deuda: $%,.2f | Expedida: %s%n", 
                    tc.getNumeroCuenta(), tc.getCupoDisponible(), tc.getDeudaActual(), fExp);
        } else {
            System.out.println("  * Tarjeta de Crédito     : No posee");
        }
        System.out.println("=====================================================================");
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

        double monto = TypeValidator.validateDouble(
                "Ingrese el monto a consignar:",
                ValidationRules.POSITIVE_AMOUNT,
                "Error: El monto a consignar debe ser mayor o igual a 0."
        );
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

        double monto = TypeValidator.validateDouble(
                "Ingrese el monto a retirar:",
                ValidationRules.POSITIVE_AMOUNT,
                "Error: El monto a retirar debe ser mayor o igual a 0."
        );
        bankServiceImpl.retirar(cliente, tipoCuenta, monto);
    }

    /**
     * Captura datos y realiza una transferencia.
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
        double monto = TypeValidator.validateDouble(
                "Ingrese el monto a transferir:",
                ValidationRules.POSITIVE_AMOUNT,
                "Error: El monto a transferir debe ser mayor o igual a 0."
        );

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
        double monto = TypeValidator.validateDouble(
                "Ingrese el valor de la compra:",
                ValidationRules.POSITIVE_AMOUNT,
                "Error: El monto de la compra debe ser mayor o igual a 0."
        );

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

        double monto = TypeValidator.validateDouble(
                "Ingrese el valor del abono a realizar:",
                ValidationRules.POSITIVE_AMOUNT,
                "Error: El abono debe ser mayor o igual a 0."
        );
        bankServiceImpl.pagarTarjeta(cliente, monto);
    }
}
