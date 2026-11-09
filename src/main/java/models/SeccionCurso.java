package models;

/**
 * Seccion de curso: instancia concreta de una unidad didactica dentro de un
 * bloque, con modalidad, docente responsable y cupo maximo.
 */
public class SeccionCurso {
  private String idSeccion;
  private String idBloque;
  private String idCurso;
  private ModalidadDictado modalidad;
  private String idProfesor;
  private int cupoMaximo;

  public SeccionCurso(String idSeccion, String idBloque, String idCurso, ModalidadDictado modalidad, String idProfesor,
      int cupoMaximo) {
    this.idSeccion = idSeccion;
    this.idBloque = idBloque;
    this.idCurso = idCurso;
    this.modalidad = modalidad;
    this.idProfesor = idProfesor;
    this.cupoMaximo = cupoMaximo;
  }

  public String getIdSeccion() {
    return idSeccion;
  }

  public String getIdBloque() {
    return idBloque;
  }

  public String getIdCurso() {
    return idCurso;
  }

  public ModalidadDictado getModalidad() {
    return modalidad;
  }

  public String getIdProfesor() {
    return idProfesor;
  }

  public int getCupoMaximo() {
    return cupoMaximo;
  }
}
