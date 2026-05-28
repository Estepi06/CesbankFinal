package cesde.domain;

import java.time.LocalDate;

/**
 * Entidad que representa a un cliente del banco.
 * Contiene información de perfil, estado de seguridad del login, y productos financieros asociados.
 */
public class Cliente {
    private int id;
    private String nombreCompleto;
    private String celular;
    private String usuario;
    private String clave;
    private int intentosFallidos;
    private boolean bloqueado;
    
    // Fechas avanzadas del cliente
    private LocalDate fechaNacimiento;
    private LocalDate fechaRegistro;

    // Productos bancarios del cliente (pueden ser nulos si no los posee)
    private CuentaAhorros cuentaAhorros;
    private CuentaCorriente cuentaCorriente;
    private TarjetaCredito tarjetaCredito;

    public Cliente() {
    }

    public Cliente(int id, String nombreCompleto, String celular, String usuario, String clave, int intentosFallidos, boolean bloqueado) {
        this.id = id;
        this.nombreCompleto = nombreCompleto;
        this.celular = celular;
        this.usuario = usuario;
        this.clave = clave;
        this.intentosFallidos = intentosFallidos;
        this.bloqueado = bloqueado;
        this.fechaRegistro = LocalDate.now(); // Por defecto
    }

    public Cliente(int id, String nombreCompleto, String celular, String usuario, String clave, int intentosFallidos, boolean bloqueado, LocalDate fechaNacimiento, LocalDate fechaRegistro) {
        this.id = id;
        this.nombreCompleto = nombreCompleto;
        this.celular = celular;
        this.usuario = usuario;
        this.clave = clave;
        this.intentosFallidos = intentosFallidos;
        this.bloqueado = bloqueado;
        this.fechaNacimiento = fechaNacimiento;
        this.fechaRegistro = fechaRegistro != null ? fechaRegistro : LocalDate.now();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombreCompleto() {
        return nombreCompleto;
    }

    public void setNombreCompleto(String nombreCompleto) {
        this.nombreCompleto = nombreCompleto;
    }

    public String getCelular() {
        return celular;
    }

    public void setCelular(String celular) {
        this.celular = celular;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getClave() {
        return clave;
    }

    public void setClave(String clave) {
        this.clave = clave;
    }

    public int getIntentosFallidos() {
        return intentosFallidos;
    }

    public void setIntentosFallidos(int intentosFallidos) {
        this.intentosFallidos = intentosFallidos;
    }

    public boolean isBloqueado() {
        return bloqueado;
    }

    public void setBloqueado(boolean bloqueado) {
        this.bloqueado = bloqueado;
    }

    public LocalDate getFechaNacimiento() {
        return fechaNacimiento;
    }

    public void setFechaNacimiento(LocalDate fechaNacimiento) {
        this.fechaNacimiento = fechaNacimiento;
    }

    public LocalDate getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(LocalDate fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }

    public CuentaAhorros getCuentaAhorros() {
        return cuentaAhorros;
    }

    public void setCuentaAhorros(CuentaAhorros cuentaAhorros) {
        this.cuentaAhorros = cuentaAhorros;
    }

    public CuentaCorriente getCuentaCorriente() {
        return cuentaCorriente;
    }

    public void setCuentaCorriente(CuentaCorriente cuentaCorriente) {
        this.cuentaCorriente = cuentaCorriente;
    }

    public TarjetaCredito getTarjetaCredito() {
        return tarjetaCredito;
    }

    public void setTarjetaCredito(TarjetaCredito tarjetaCredito) {
        this.tarjetaCredito = tarjetaCredito;
    }
}
