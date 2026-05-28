package cesde.domain;

import java.time.LocalDate;

/**
 * Entidad que representa una transacción financiera realizada sobre una cuenta o tarjeta.
 */
public class Transaccion {
    private int id;
    private String numeroCuenta;
    private String tipo; // CONSIGNACION, RETIRO, TRANSFERENCIA_ENVIADA, TRANSFERENCIA_RECIBIDA, COMPRA_TARJETA, PAGO_TARJETA
    private double monto;
    private LocalDate fecha;
    private String descripcion;

    public Transaccion() {
    }

    public Transaccion(int id, String numeroCuenta, String tipo, double monto, LocalDate fecha, String descripcion) {
        this.id = id;
        this.numeroCuenta = numeroCuenta;
        this.tipo = tipo;
        this.monto = monto;
        this.fecha = fecha;
        this.descripcion = descripcion;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNumeroCuenta() {
        return numeroCuenta;
    }

    public void setNumeroCuenta(String numeroCuenta) {
        this.numeroCuenta = numeroCuenta;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public double getMonto() {
        return monto;
    }

    public void setMonto(double monto) {
        this.monto = monto;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
}
