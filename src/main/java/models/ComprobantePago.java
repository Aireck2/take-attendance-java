package models;

/**
 * Comprobante de pago: registro financiero emitido a un alumno que amortiza un
 * derecho de pago.
 */
public class ComprobantePago {
  private String idComprobante;
  private String idEstudiante;
  private String idDerecho;
  private String fecha;
  private double monto;

  public ComprobantePago(String idComprobante, String idEstudiante, String idDerecho, String fecha, double monto) {
    this.idComprobante = idComprobante;
    this.idEstudiante = idEstudiante;
    this.idDerecho = idDerecho;
    this.fecha = fecha;
    this.monto = monto;
  }

  public String getIdComprobante() {
    return idComprobante;
  }

  public void setIdComprobante(String idComprobante) {
    this.idComprobante = idComprobante;
  }

  public String getIdEstudiante() {
    return idEstudiante;
  }

  public String getIdDerecho() {
    return idDerecho;
  }

  public String getFecha() {
    return fecha;
  }

  public double getMonto() {
    return monto;
  }

  public String toCsv() {
    return idComprobante + "," + idEstudiante + "," + idDerecho + "," + fecha + "," + monto;
  }
}
