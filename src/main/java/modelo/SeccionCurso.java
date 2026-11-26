package modelo;

/**
 * Seccion de curso: instancia concreta de una unidad didactica dentro de un bloque.
 */
public class SeccionCurso {
  private final String idSeccion;
  private final String idBloque;
  private final String idCurso;

  public SeccionCurso(String idSeccion, String idBloque, String idCurso) {
    this.idSeccion = idSeccion;
    this.idBloque = idBloque;
    this.idCurso = idCurso;
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
}
