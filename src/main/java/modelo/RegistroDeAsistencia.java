package modelo;

/**
 * Registro de asistencia de un estudiante a una sesion de clase en una fecha determinada.
 */
public class RegistroDeAsistencia {
  private String idAsistencia;
  private final String idCurso;
  private final String idEstudiante;
  private final String fecha;
  private final int horasAsistidas;
  private final boolean presente;
  private final String idSesion;
  private final String idMatricula;

  public RegistroDeAsistencia(String idAsistencia, String idCurso, String idEstudiante, String fecha,
      int horasAsistidas, boolean presente, String idSesion, String idMatricula) {
    this.idAsistencia = idAsistencia;
    this.idCurso = idCurso;
    this.idEstudiante = idEstudiante;
    this.fecha = fecha;
    this.horasAsistidas = horasAsistidas;
    this.presente = presente;
    this.idSesion = idSesion == null ? "" : idSesion;
    this.idMatricula = idMatricula == null ? "" : idMatricula;
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

  public String getIdMatricula() {
    return idMatricula;
  }

  public String toCsv() {
    return idAsistencia + "," + idCurso + "," + idEstudiante + "," + fecha + "," + horasAsistidas + ","
        + (presente ? 1 : 0) + "," + idSesion + "," + idMatricula;
  }
}
