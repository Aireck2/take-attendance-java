package models;

/**
 * Registro de asistencia de un estudiante a una sesion de clase en una fecha
 * determinada. Guarda cuantas horas asistio el estudiante y si realmente
 * estuvo presente.
 *
 * El campo idSesion vincula el registro con la sesion de clase que lo genero.
 * Las filas legadas sin sesion conservan una cadena vacia.
 */
public class RegistroDeAsistencia {
  private String idAsistencia;
  private String idCurso;
  private String idEstudiante;
  private String fecha;
  private int horasAsistidas;
  private boolean presente;
  private String idSesion;

  public RegistroDeAsistencia(String idAsistencia, String idCurso, String idEstudiante, String fecha, int horasAsistidas,
      boolean presente) {
    this(idAsistencia, idCurso, idEstudiante, fecha, horasAsistidas, presente, "");
  }

  public RegistroDeAsistencia(String idAsistencia, String idCurso, String idEstudiante, String fecha, int horasAsistidas,
      boolean presente, String idSesion) {
    this.idAsistencia = idAsistencia;
    this.idCurso = idCurso;
    this.idEstudiante = idEstudiante;
    this.fecha = fecha;
    this.horasAsistidas = horasAsistidas;
    this.presente = presente;
    this.idSesion = idSesion == null ? "" : idSesion;
  }

  public String getIdAsistencia() {
    return idAsistencia;
  }

  public void setIdAsistencia(String idAsistencia) {
    this.idAsistencia = idAsistencia;
  }

  public String getIdCurso() {
    return idCurso;
  }

  public String getIdEstudiante() {
    return idEstudiante;
  }

  public String getFecha() {
    return fecha;
  }

  public int getHorasAsistidas() {
    return horasAsistidas;
  }

  public boolean isPresente() {
    return presente;
  }

  public String getIdSesion() {
    return idSesion;
  }

  public String toCsv() {
    // Se guarda el estado como 1 (presente) o 0 (ausente) para facilitar la lectura en CSV.
    return idAsistencia + "," + idCurso + "," + idEstudiante + "," + fecha + "," + horasAsistidas + ","
        + (presente ? 1 : 0) + "," + idSesion;
  }
}
