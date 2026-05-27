package cesde.service;

import cesde.domain.*;
import cesde.service.portinput.BankService;
import cesde.service.portoutput.ClientePersistencePort;
import cesde.service.portoutput.CuentaPersistencePort;
import cesde.service.portoutput.TarjetaPersistencePort;
import java.util.Random;

/**
 * Implementación del servicio bancario de CesBank.
 * Coordina las reglas de negocio del dominio con los adaptadores de persistencia (puertos de salida).
 * Utiliza consultas JOIN eficientes estilo Lucia Store.
 */
public class BankServiceImpl implements BankService {

    private final ClientePersistencePort clientePersistencePort;
    private final CuentaPersistencePort cuentaPersistencePort;
    private final TarjetaPersistencePort tarjetaPersistencePort;

    public BankServiceImpl(ClientePersistencePort clientePersistencePort,
                           CuentaPersistencePort cuentaPersistencePort,
                           TarjetaPersistencePort tarjetaPersistencePort) {
        this.clientePersistencePort = clientePersistencePort;
        this.cuentaPersistencePort = cuentaPersistencePort;
        this.tarjetaPersistencePort = tarjetaPersistencePort;
    }

    @Override
    public Cliente login(String usuario, String clave) {
        // Carga al cliente y todos sus productos en UNA sola llamada de base de datos usando LEFT JOIN
        Cliente cliente = clientePersistencePort.buscarPorUsuario(usuario);
        
        if (cliente == null) {
            System.out.println("[Login] Usuario no encontrado.");
            return null;
        }

        if (cliente.isBloqueado()) {
            System.out.println("[Login] Acceso denegado. Este usuario se encuentra BLOQUEADO.");
            return cliente;
        }

        if (cliente.getClave().equals(clave)) {
            // Login exitoso
            restablecerIntentos(cliente);
            System.out.println("[Login] ¡Inicio de sesión correcto! Bienvenido " + cliente.getNombreCompleto());
            return cliente;
        } else {
            // Clave incorrecta
            registrarIntentoFallido(cliente);
            return cliente; // Retorna el cliente con el contador de intentos actualizado
        }
    }

    @Override
    public void registrarCliente(Cliente cliente) {
        if (cliente == null) return;

        // 1. Guardar el cliente en la tabla 'clientes' (con fecha de registro y nacimiento)
        clientePersistencePort.crearCliente(cliente);

        // 2. Generar número único y guardar Cuenta de Ahorros si el cliente la solicitó
        if (cliente.getCuentaAhorros() != null) {
            String numAhorros = generarNumeroCuenta(); // El banco genera el número, no el cliente
            cliente.getCuentaAhorros().setNumeroCuenta(numAhorros);
            cuentaPersistencePort.crearCuenta(cliente.getCuentaAhorros(), cliente.getId(), "AHORROS");
            System.out.println("[Banco] Número asignado a su Cuenta de Ahorros: " + numAhorros);
        }

        // 3. Generar número único y guardar Cuenta Corriente si el cliente la solicitó
        if (cliente.getCuentaCorriente() != null) {
            String numCorriente = generarNumeroCuenta(); // El banco genera el número, no el cliente
            cliente.getCuentaCorriente().setNumeroCuenta(numCorriente);
            cuentaPersistencePort.crearCuenta(cliente.getCuentaCorriente(), cliente.getId(), "CORRIENTE");
            System.out.println("[Banco] Número asignado a su Cuenta Corriente: " + numCorriente);
        }

        // 4. Generar número único y guardar Tarjeta de Crédito si el cliente la solicitó
        if (cliente.getTarjetaCredito() != null) {
            String numTarjeta = generarNumeroTarjeta(); // El banco genera el número, no el cliente
            cliente.getTarjetaCredito().setNumeroCuenta(numTarjeta);
            tarjetaPersistencePort.crearTarjeta(cliente.getTarjetaCredito(), cliente.getId());
            System.out.println("[Banco] Número asignado a su Tarjeta de Crédito: " + numTarjeta);
        }

        System.out.println("[Servicio] ¡Cliente y productos registrados exitosamente!");
    }

    /**
     * Genera un número de cuenta de 9 dígitos único, verificando que no exista ya en la base de datos.
     * El prefijo 'C' identifica que es una cuenta (Cuenta).
     */
    private String generarNumeroCuenta() {
        Random random = new Random();
        String numero;
        do {
            // Genera un número de 9 dígitos (entre 100000000 y 999999999)
            int digitos = 100000000 + random.nextInt(900000000);
            numero = "C" + digitos; // Formato: C123456789
        } while (cuentaPersistencePort.existeNumeroCuenta(numero)); // Reintenta si ya existe
        return numero;
    }

    /**
     * Genera un número de tarjeta de 9 dígitos único, verificando que no exista ya en la base de datos.
     * El prefijo 'T' identifica que es una tarjeta (Tarjeta).
     */
    private String generarNumeroTarjeta() {
        Random random = new Random();
        String numero;
        do {
            // Genera un número de 9 dígitos (entre 100000000 y 999999999)
            int digitos = 100000000 + random.nextInt(900000000);
            numero = "T" + digitos; // Formato: T123456789
        } while (tarjetaPersistencePort.existeNumeroTarjeta(numero)); // Reintenta si ya existe
        return numero;
    }

    @Override
    public void cargarProductosCliente(Cliente cliente) {
        if (cliente == null) return;

        // OPTIMIZACIÓN JOIN: Recargamos los productos del cliente usando un único query JOIN
        Cliente cargado = clientePersistencePort.buscarPorUsuario(cliente.getUsuario());
        if (cargado != null) {
            cliente.setCuentaAhorros(cargado.getCuentaAhorros());
            cliente.setCuentaCorriente(cargado.getCuentaCorriente());
            cliente.setTarjetaCredito(cargado.getTarjetaCredito());
            cliente.setFechaNacimiento(cargado.getFechaNacimiento());
            cliente.setFechaRegistro(cargado.getFechaRegistro());
        }
    }

    @Override
    public void registrarIntentoFallido(Cliente cliente) {
        int nuevosIntentos = cliente.getIntentosFallidos() + 1;
        cliente.setIntentosFallidos(nuevosIntentos);
        clientePersistencePort.actualizarIntentosFallidos(cliente.getId(), nuevosIntentos);
        
        System.out.println("[Seguridad] Clave incorrecta. Intento fallido " + nuevosIntentos + " de 3.");
        
        if (nuevosIntentos >= 3) {
            bloquearCliente(cliente);
        }
    }

    @Override
    public void bloquearCliente(Cliente cliente) {
        cliente.setBloqueado(true);
        clientePersistencePort.bloquearCliente(cliente.getId());
        System.out.println("[Seguridad] ¡USUARIO BLOQUEADO! Superó el límite de 3 intentos fallidos. Comuníquese con soporte.");
    }

    @Override
    public void restablecerIntentos(Cliente cliente) {
        cliente.setIntentosFallidos(0);
        clientePersistencePort.resetearIntentosFallidos(cliente.getId());
    }

    @Override
    public boolean consignar(Cliente cliente, String tipoCuenta, double monto) {
        if (monto <= 0) {
            System.out.println("[Error] El monto a consignar debe ser mayor a 0.");
            return false;
        }

        if ("AHORROS".equalsIgnoreCase(tipoCuenta)) {
            CuentaAhorros ahorro = cliente.getCuentaAhorros();
            if (ahorro != null) {
                ahorro.consignar(monto);
                cuentaPersistencePort.actualizarSaldo(ahorro.getNumeroCuenta(), ahorro.getSaldo());
                System.out.printf("[Éxito] Consignación de $%.2f realizada en Cuenta de Ahorros. Nuevo saldo: $%.2f%n", monto, ahorro.getSaldo());
                return true;
            } else {
                System.out.println("[Error] No posee una Cuenta de Ahorros.");
            }
        } else if ("CORRIENTE".equalsIgnoreCase(tipoCuenta)) {
            CuentaCorriente corriente = cliente.getCuentaCorriente();
            if (corriente != null) {
                corriente.consignar(monto);
                cuentaPersistencePort.actualizarSaldo(corriente.getNumeroCuenta(), corriente.getSaldo());
                System.out.printf("[Éxito] Consignación de $%.2f realizada en Cuenta Corriente. Nuevo saldo: $%.2f%n", monto, corriente.getSaldo());
                return true;
            } else {
                System.out.println("[Error] No posee una Cuenta Corriente.");
            }
        }
        return false;
    }

    @Override
    public boolean retirar(Cliente cliente, String tipoCuenta, double monto) {
        if (monto <= 0) {
            System.out.println("[Error] El monto a retirar debe ser mayor a 0.");
            return false;
        }

        if ("AHORROS".equalsIgnoreCase(tipoCuenta)) {
            CuentaAhorros ahorro = cliente.getCuentaAhorros();
            if (ahorro != null) {
                if (ahorro.retirar(monto)) {
                    cuentaPersistencePort.actualizarSaldo(ahorro.getNumeroCuenta(), ahorro.getSaldo());
                    System.out.printf("[Éxito] Retiro de $%.2f exitoso. Comisión del 1.5%% cobrada. Nuevo saldo: $%.2f%n", monto, ahorro.getSaldo());
                    return true;
                } else {
                    System.out.printf("[Rechazado] Fondos insuficientes. Recuerda que retirar $%.2f cuesta $%.2f en total (incluyendo 1.5%% de comisión) y tu saldo es $%.2f.%n", monto, monto * 1.015, ahorro.getSaldo());
                }
            } else {
                System.out.println("[Error] No posee una Cuenta de Ahorros.");
            }
        } else if ("CORRIENTE".equalsIgnoreCase(tipoCuenta)) {
            CuentaCorriente corriente = cliente.getCuentaCorriente();
            if (corriente != null) {
                if (corriente.retirar(monto)) {
                    cuentaPersistencePort.actualizarSaldo(corriente.getNumeroCuenta(), corriente.getSaldo());
                    System.out.printf("[Éxito] Retiro de $%.2f exitoso (Sobregiro aplicado si el saldo es negativo). Nuevo saldo: $%.2f%n", monto, corriente.getSaldo());
                    return true;
                } else {
                    System.out.printf("[Rechazado] Retiro supera el límite permitido. Su saldo es $%.2f, su límite con sobregiro (20%%) es $%.2f.%n", corriente.getSaldo(), corriente.getSaldo() * 1.20);
                }
            } else {
                System.out.println("[Error] No posee una Cuenta Corriente.");
            }
        }
        return false;
    }

    @Override
    public boolean comprarConTarjeta(Cliente cliente, double monto) {
        TarjetaCredito tc = cliente.getTarjetaCredito();
        if (tc == null) {
            System.out.println("[Error] No posee una Tarjeta de Crédito.");
            return false;
        }

        if (tc.realizarCompra(monto)) {
            tarjetaPersistencePort.actualizarCupoYDeuda(tc.getNumeroCuenta(), tc.getCupoDisponible(), tc.getDeudaActual());
            System.out.printf("[Éxito] Compra autorizada por $%.2f. Cupo restante: $%.2f, Deuda actual: $%.2f%n", monto, tc.getCupoDisponible(), tc.getDeudaActual());
            return true;
        } else {
            System.out.printf("[Rechazado] Compra declinada. El monto de $%.2f supera su cupo disponible de $%.2f.%n", monto, tc.getCupoDisponible());
        }
        return false;
    }

    @Override
    public boolean pagarTarjeta(Cliente cliente, double monto) {
        TarjetaCredito tc = cliente.getTarjetaCredito();
        if (tc == null) {
            System.out.println("[Error] No posee una Tarjeta de Crédito.");
            return false;
        }

        if (monto <= 0) {
            System.out.println("[Error] El monto a pagar debe ser mayor a 0.");
            return false;
        }

        double deudaOriginal = tc.getDeudaActual();
        if (tc.realizarPago(monto)) {
            tarjetaPersistencePort.actualizarCupoYDeuda(tc.getNumeroCuenta(), tc.getCupoDisponible(), tc.getDeudaActual());
            double pagoAplicado = deudaOriginal - tc.getDeudaActual();
            System.out.printf("[Éxito] Pago aplicado por $%.2f. Nueva deuda: $%.2f, Cupo disponible: $%.2f%n", pagoAplicado, tc.getDeudaActual(), tc.getCupoDisponible());
            return true;
        }
        return false;
    }

    @Override
    public boolean transferir(Cliente cliente, String tipoCuentaOrigen, String numeroCuentaDestino, double monto) {
        if (monto <= 0) {
            System.out.println("[Error] El monto a transferir debe ser mayor a 0.");
            return false;
        }

        if (numeroCuentaDestino == null || numeroCuentaDestino.trim().isEmpty()) {
            System.out.println("[Error] Número de cuenta destino inválido.");
            return false;
        }

        // Evitar transferencias al mismo número de cuenta de origen
        Cuenta destino = cuentaPersistencePort.buscarCuentaPorNumero(numeroCuentaDestino);
        if (destino == null) {
            System.out.println("[Error] Cuenta destino no encontrada.");
            return false;
        }

        // Determinar cuenta origen del cliente
        if ("AHORROS".equalsIgnoreCase(tipoCuentaOrigen)) {
            CuentaAhorros ahorro = cliente.getCuentaAhorros();
            if (ahorro == null) {
                System.out.println("[Error] No posee una Cuenta de Ahorros para transferir.");
                return false;
            }
            if (ahorro.getNumeroCuenta().equalsIgnoreCase(numeroCuentaDestino)) {
                System.out.println("[Error] No puede transferir a la misma cuenta de origen.");
                return false;
            }

            // Para transferencias desde ahorros no aplicamos la comisión de retiro (solo se debita el monto)
            if (ahorro.getSaldo() < monto) {
                System.out.println("[Rechazado] Fondos insuficientes en Cuenta de Ahorros.");
                return false;
            }

            // Realizar débito y crédito
            ahorro.setSaldo(ahorro.getSaldo() - monto);
            destino.setSaldo(destino.getSaldo() + monto);
            cuentaPersistencePort.actualizarSaldo(ahorro.getNumeroCuenta(), ahorro.getSaldo());
            cuentaPersistencePort.actualizarSaldo(destino.getNumeroCuenta(), destino.getSaldo());
            System.out.printf("[Éxito] Transferencia de $%.2f desde %s hacia %s realizada. Nuevo saldo origen: $%.2f%n",
                    monto, ahorro.getNumeroCuenta(), destino.getNumeroCuenta(), ahorro.getSaldo());
            return true;

        } else if ("CORRIENTE".equalsIgnoreCase(tipoCuentaOrigen)) {
            CuentaCorriente corriente = cliente.getCuentaCorriente();
            if (corriente == null) {
                System.out.println("[Error] No posee una Cuenta Corriente para transferir.");
                return false;
            }
            if (corriente.getNumeroCuenta().equalsIgnoreCase(numeroCuentaDestino)) {
                System.out.println("[Error] No puede transferir a la misma cuenta de origen.");
                return false;
            }

            double limiteMaximo = corriente.getSaldo() * 1.20; // mismo criterio que retirar
            if (monto > limiteMaximo) {
                System.out.printf("[Rechazado] Transferencia supera el límite permitido. Su saldo es $%.2f, límite con sobregiro: $%.2f.%n",
                        corriente.getSaldo(), limiteMaximo);
                return false;
            }

            corriente.setSaldo(corriente.getSaldo() - monto);
            destino.setSaldo(destino.getSaldo() + monto);
            cuentaPersistencePort.actualizarSaldo(corriente.getNumeroCuenta(), corriente.getSaldo());
            cuentaPersistencePort.actualizarSaldo(destino.getNumeroCuenta(), destino.getSaldo());
            System.out.printf("[Éxito] Transferencia de $%.2f desde %s hacia %s realizada. Nuevo saldo origen: $%.2f%n",
                    monto, corriente.getNumeroCuenta(), destino.getNumeroCuenta(), corriente.getSaldo());
            return true;
        }

        System.out.println("[Error] Tipo de cuenta de origen inválido.");
        return false;
    }
}
