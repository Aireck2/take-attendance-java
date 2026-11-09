package models;

/**
 * Matricula: registro formal que valida la condicion del estudiante en un
 * bloque de seccion para el periodo activo.
 */
public class Matricula {
  private String idMatricula;
  private String idEstudiante;
  private String idBloque;
  private String fechaMatricula;
  private EstadoMatricula estado;

  public Matricula(String idMatricula, String idEstudiante, String idBloque, String fechaMatricula,
      EstadoMatricula estado) {
    this.idMatricula = idMatricula;
    this.idEstudiante = idEstudiante;
    this.idBloque = idBloque;
    this.fechaMatricula = fechaMatricula;
    this.estado = estado;
  }

  public String getIdMatricula() {
    return idMatricula;
  }

  public String getIdEstudiante() {
    return idEstudiante;
  }

  public String getIdBloque() {
    return idBloque;
  }

  public String getFechaMatricula() {
    return fechaMatricula;
  }

  public EstadoMatricula getEstado() {
    return estado;
  }

  public String toCsv() {
    return idMatricula + "," + idEstudiante + "," + idBloque + "," + fechaMatricula + "," + estado;
  }
}
