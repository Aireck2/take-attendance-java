package modelo;

/** Matricula del estudiante en un bloque de seccion. */
public class Matricula {
  private final String idMatricula;
  private final String idEstudiante;
  private final String idBloque;
  private final EstadoMatricula estado;

  public Matricula(String idMatricula, String idEstudiante, String idBloque, EstadoMatricula estado) {
    this.idMatricula = idMatricula;
    this.idEstudiante = idEstudiante;
    this.idBloque = idBloque;
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

  public EstadoMatricula getEstado() {
    return estado;
  }
}
