package cesde.domain;

/**
 * Tarjeta de Crédito: Hereda de Cuenta (con saldo base 0).
 * Maneja cupo disponible, deuda actual y permite realizar compras diferidas a cuotas con tasas de interés.
 */
public class TarjetaCredito extends Cuenta {
    private double cupoDisponible;
    private double deudaActual;

    public TarjetaCredito() {
        super();
        this.saldo = 0; // Tarjeta de crédito maneja cupo y deuda, no saldo tradicional
    }

    public TarjetaCredito(String numeroTarjeta, double cupoDisponible, double deudaActual) {
        super(numeroTarjeta, 0);
        this.cupoDisponible = cupoDisponible;
        this.deudaActual = deudaActual;
    }

    public double getCupoDisponible() {
        return cupoDisponible;
    }

    public void setCupoDisponible(double cupoDisponible) {
        this.cupoDisponible = cupoDisponible;
    }

    public double getDeudaActual() {
        return deudaActual;
    }

    public void setDeudaActual(double deudaActual) {
        this.deudaActual = deudaActual;
    }

    /**
     * Realiza una compra restando al cupo disponible y sumando a la deuda actual.
     * @param monto Costo de la compra.
     * @return true si el cupo es suficiente, false de lo contrario.
     */
    public boolean realizarCompra(double monto) {
        if (monto > 0 && monto <= this.cupoDisponible) {
            this.cupoDisponible -= monto;
            this.deudaActual += monto;
            return true;
        }
        return false;
    }

    /**
     * Realiza un pago a la deuda de la tarjeta. Resta a la deuda y suma al cupo disponible.
     * Si el pago es superior a la deuda actual, se ajusta para pagar exactamente el total de la deuda.
     * @param monto Cantidad a abonar.
     * @return true si el pago se realiza con éxito.
     */
    public boolean realizarPago(double monto) {
        if (monto <= 0) return false;
        
        // No se puede abonar más de la deuda actual
        if (monto > this.deudaActual) {
            monto = this.deudaActual;
        }
        
        this.deudaActual -= monto;
        this.cupoDisponible += monto;
        return true;
    }

    /**
     * Calcula la cuota mensual aproximada según el número de cuotas y el interés diferido.
     * - 1 a 2 cuotas: Sin interés.
     * - 3 a 6 cuotas: Interés mensual del 1.9% (0.019).
     * - 7 o más cuotas: Interés mensual del 2.3% (0.023).
     * 
     * @param monto Compra diferida.
     * @param cuotas Número de cuotas.
     * @return Valor aproximado de la cuota mensual.
     */
    public double calcularCuotaMensual(double monto, int cuotas) {
        if (cuotas <= 0 || monto <= 0) return 0;
        
        if (cuotas <= 2) {
            return monto / cuotas; // Sin interés
        }
        
        double tasaInteres = (cuotas <= 6) ? 0.019 : 0.023;
        
        // Fórmula de Amortización Francesa (Cuota Fija)
        return (monto * tasaInteres) / (1 - Math.pow(1 + tasaInteres, -cuotas));
    }
}
