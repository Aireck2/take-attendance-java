package modelo;

/** Unidad didactica de un plan de estudios con su carga lectiva total. */
public class Curso {
  private final String idCurso;
  private final String nombreCurso;
  private final String idModulo;
  private final int totalHorasLectivas;

  public Curso(String idCurso, String nombreCurso, String idModulo, int totalHorasLectivas) {
    if (totalHorasLectivas < 0) {
      throw new IllegalArgumentException("totalHorasLectivas no puede ser negativo");
    }
    this.idCurso = idCurso;
    this.nombreCurso = nombreCurso;
    this.idModulo = idModulo;
    this.totalHorasLectivas = totalHorasLectivas;
  }

  public String getIdCurso() {
    return idCurso;
  }

  public String getNombreCurso() {
    return nombreCurso;
  }

  public String getIdModulo() {
    return idModulo;
  }

  public int getTotalHorasLectivas() {
    return totalHorasLectivas;
  }
}
