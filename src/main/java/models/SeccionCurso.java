package models;

/**
 * Seccion de curso: instancia concreta de una unidad didactica dentro de un
 * bloque, con modalidad y cupo maximo. El docente y el ambiente se resuelven
 * por cada franja horaria (HorarioSeccion).
 */
public class SeccionCurso {
  private String idSeccion;
  private String idBloque;
  private String idCurso;
  private ModalidadDictado modalidad;
  private int cupoMaximo;

  public SeccionCurso(String idSeccion, String idBloque, String idCurso, ModalidadDictado modalidad, int cupoMaximo) {
    this.idSeccion = idSeccion;
    this.idBloque = idBloque;
    this.idCurso = idCurso;
    this.modalidad = modalidad;
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

  public int getCupoMaximo() {
    return cupoMaximo;
  }
}
