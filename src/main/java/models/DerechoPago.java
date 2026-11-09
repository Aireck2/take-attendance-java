package models;

/**
 * Derecho de pago: concepto normado de cobro institucional, por ejemplo la
 * matricula semestral o el carne universitario.
 */
public class DerechoPago {
  private String idDerecho;
  private String codigo;
  private String nombre;
  private double monto;

  public DerechoPago(String idDerecho, String codigo, String nombre, double monto) {
    this.idDerecho = idDerecho;
    this.codigo = codigo;
    this.nombre = nombre;
    this.monto = monto;
  }

  public String getIdDerecho() {
    return idDerecho;
  }

  public String getCodigo() {
    return codigo;
  }

  public String getNombre() {
    return nombre;
  }

  public double getMonto() {
    return monto;
  }
}
