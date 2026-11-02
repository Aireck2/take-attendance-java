package models;

/**
 * Registro de asistencia de un estudiante a un curso en una fecha determinada.
 * Guarda cuántas horas asistió el estudiante y si realmente estuvo presente.
 */
public class RegistroDeAsistencia {
  private String idAsistencia;
  private String idCurso;
  private String idEstudiante;
  private String fecha;
  private int horasAsistidas;
  private boolean presente;

  public RegistroDeAsistencia(String idAsistencia, String idCurso, String idEstudiante, String fecha, int horasAsistidas,
      boolean presente) {
    this.idAsistencia = idAsistencia;
    this.idCurso = idCurso;
    this.idEstudiante = idEstudiante;
    this.fecha = fecha;
    this.horasAsistidas = horasAsistidas;
    this.presente = presente;
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

  public String toCsv() {
    // Se guarda el estado como 1 (presente) o 0 (ausente) para facilitar la lectura en CSV.
    return idAsistencia + "," + idCurso + "," + idEstudiante + "," + fecha + "," + horasAsistidas + "," + (presente ? 1 : 0);
  }
}
