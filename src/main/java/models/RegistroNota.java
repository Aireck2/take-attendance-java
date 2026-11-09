package models;

/**
 * Registro de nota: calificacion vigesimal (0 a 20) obtenida por un alumno en
 * un indicador de logro concreto.
 */
public class RegistroNota {
  private String idRegistroNota;
  private String idIndicador;
  private String idEstudiante;
  private int nota;
  private String fecha;

  public RegistroNota(String idRegistroNota, String idIndicador, String idEstudiante, int nota, String fecha) {
    this.idRegistroNota = idRegistroNota;
    this.idIndicador = idIndicador;
    this.idEstudiante = idEstudiante;
    this.nota = nota;
    this.fecha = fecha;
  }

  public String getIdRegistroNota() {
    return idRegistroNota;
  }

  public void setIdRegistroNota(String idRegistroNota) {
    this.idRegistroNota = idRegistroNota;
  }

  public String getIdIndicador() {
    return idIndicador;
  }

  public String getIdEstudiante() {
    return idEstudiante;
  }

  public int getNota() {
    return nota;
  }

  public String getFecha() {
    return fecha;
  }

  public String toCsv() {
    return idRegistroNota + "," + idIndicador + "," + idEstudiante + "," + nota + "," + fecha;
  }
}
