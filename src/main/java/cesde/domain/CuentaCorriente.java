package cesde.domain;

/**
 * Cuenta Corriente: Permite un sobregiro del 20% sobre el saldo disponible.
 * El límite total de retiro permitido es saldo * 1.20.
 */
public class CuentaCorriente extends Cuenta {
    private static final double LIMITE_SOBREGIRO = 1.20; // 20% adicional

    public CuentaCorriente() {
        super();
    }

    public CuentaCorriente(String numeroCuenta, double saldo) {
        super(numeroCuenta, saldo);
    }

    /**
     * Sobrescribe retirar permitiendo sobregiro de hasta el 20% del saldo actual.
     */
    @Override
    public boolean retirar(double monto) {
        if (monto <= 0) return false;
        
        double limiteMaximo = this.saldo * LIMITE_SOBREGIRO;
        
        if (monto <= limiteMaximo) {
            this.saldo -= monto; // El saldo puede resultar negativo
            return true;
        }
        return false;
    }
}
