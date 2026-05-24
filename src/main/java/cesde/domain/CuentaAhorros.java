package cesde.domain;

/**
 * Cuenta de Ahorros: Aplica una comisión del 1.5% sobre el monto retirado.
 */
public class CuentaAhorros extends Cuenta {
    private static final double COMISION_RETIRO = 0.015; // 1.5%

    public CuentaAhorros() {
        super();
    }

    public CuentaAhorros(String numeroCuenta, double saldo) {
        super(numeroCuenta, saldo);
    }

    /**
     * Sobrescribe retirar aplicando el 1.5% de comisión.
     * Si monto + comisión supera el saldo, no se autoriza.
     */
    @Override
    public boolean retirar(double monto) {
        if (monto <= 0) return false;
        
        double comision = monto * COMISION_RETIRO;
        double totalDebitar = monto + comision;
        
        if (totalDebitar <= this.saldo) {
            this.saldo -= totalDebitar;
            return true;
        }
        return false;
    }
}
