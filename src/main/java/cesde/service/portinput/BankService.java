package cesde.service.portinput;

import cesde.domain.Cliente;

/**
 * Puerto de entrada que define los contratos de servicios bancarios del cajero automático.
 */
public interface BankService {
    
    /**
     * Valida el inicio de sesión del cliente.
     * @param usuario Nombre de usuario.
     * @param clave Contraseña del cliente.
     * @return El objeto Cliente si las credenciales son válidas, null de lo contrario.
     */
    Cliente login(String usuario, String clave);

    /**
     * Registra un nuevo cliente con sus respectivos productos (cuentas/tarjeta) en la base de datos.
     * @param cliente El cliente con sus productos asignados.
     */
    void registrarCliente(Cliente cliente);

    /**
     * Carga todos los productos financieros asociados al cliente (Cuentas y Tarjetas).
     * @param cliente El cliente al cual se le cargarán los productos.
     */
    void cargarProductosCliente(Cliente cliente);

    /**
     * Incrementa en 1 el contador de intentos fallidos en base de datos.
     * @param cliente El cliente.
     */
    void registrarIntentoFallido(Cliente cliente);

    /**
     * Bloquea definitivamente al cliente en base de datos.
     * @param cliente El cliente.
     */
    void bloquearCliente(Cliente cliente);

    /**
     * Restablece el contador de intentos fallidos a 0.
     * @param cliente El cliente.
     */
    void restablecerIntentos(Cliente cliente);

    /**
     * Realiza una consignación (depósito) en la cuenta del cliente (Ahorros o Corriente).
     * @param cliente El cliente autenticado.
     * @param tipoCuenta "AHORROS" o "CORRIENTE".
     * @param monto Cantidad a depositar.
     * @return true si la operación se completó y persistió con éxito.
     */
    boolean consignar(Cliente cliente, String tipoCuenta, double monto);

    /**
     * Realiza un retiro en la cuenta del cliente (Ahorros o Corriente) aplicando reglas específicas.
     * @param cliente El cliente autenticado.
     * @param tipoCuenta "AHORROS" o "CORRIENTE".
     * @param monto Cantidad a retirar.
     * @return true si el retiro fue autorizado y guardado con éxito.
     */
    boolean retirar(Cliente cliente, String tipoCuenta, double monto);

    /**
     * Realiza una compra con la Tarjeta de Crédito del cliente.
     * @param cliente El cliente autenticado.
     * @param monto Costo de la compra.
     * @return true si el cupo fue suficiente y la deuda se actualizó.
     */
    boolean comprarConTarjeta(Cliente cliente, double monto);

    /**
     * Realiza un pago a la deuda de la Tarjeta de Crédito del cliente.
     * @param cliente El cliente autenticado.
     * @param monto Cantidad a abonar.
     * @return true si el pago se procesó y guardó correctamente.
     */
    boolean pagarTarjeta(Cliente cliente, double monto);

    /**
     * Realiza una transferencia desde una de las cuentas del cliente hacia otra cuenta identificada por su número.
     * @param cliente Cliente autenticado que realiza la transferencia.
     * @param tipoCuentaOrigen "AHORROS" o "CORRIENTE" indicando la cuenta de origen del cliente.
     * @param numeroCuentaDestino Número de cuenta destino (ej. C123456789).
     * @param monto Monto a transferir.
     * @return true si la transferencia se realizó y persistió correctamente.
     */
    boolean transferir(Cliente cliente, String tipoCuentaOrigen, String numeroCuentaDestino, double monto);
}
