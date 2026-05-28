package cesde.domain;

import java.time.LocalDate;

/**
 * Clase base que representa una cuenta bancaria genérica.
 */
public class Cuenta {
    protected String numeroCuenta;
    protected double saldo;
    protected LocalDate fechaApertura;

    public Cuenta() {
        this.fechaApertura = LocalDate.now();
    }

    public Cuenta(String numeroCuenta, double saldo) {
        this.numeroCuenta = numeroCuenta;
        this.saldo = saldo;
        this.fechaApertura = LocalDate.now();
    }

    public Cuenta(String numeroCuenta, double saldo, LocalDate fechaApertura) {
        this.numeroCuenta = numeroCuenta;
        this.saldo = saldo;
        this.fechaApertura = fechaApertura != null ? fechaApertura : LocalDate.now();
    }

    public String getNumeroCuenta() {
        return numeroCuenta;
    }

    public void setNumeroCuenta(String numeroCuenta) {
        this.numeroCuenta = numeroCuenta;
    }

    public double getSaldo() {
        return saldo;
    }

    public void setSaldo(double saldo) {
        this.saldo = saldo;
    }

    public LocalDate getFechaApertura() {
        return fechaApertura;
    }

    public void setFechaApertura(LocalDate fechaApertura) {
        this.fechaApertura = fechaApertura;
    }

    /**
     * Realiza una consignación (depósito) en la cuenta.
     * @param monto Cantidad a consignar.
     */
    public void consignar(double monto) {
        if (monto > 0) {
            this.saldo += monto;
        }
    }

    /**
     * Realiza un retiro básico de la cuenta si hay fondos suficientes.
     * @param monto Cantidad a retirar.
     * @return true si el retiro fue exitoso, false de lo contrario.
     */
    public boolean retirar(double monto) {
        if (monto > 0 && this.saldo >= monto) {
            this.saldo -= monto;
            return true;
        }
        return false;
    }
}
