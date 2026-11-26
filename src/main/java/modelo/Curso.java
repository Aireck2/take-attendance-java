package modelo;

/** Unidad didactica vinculada a una carrera con su carga lectiva total. */
public class Curso {
  private final String idCurso;
  private final String nombreCurso;
  private final String idCarrera;
  private final int totalHorasLectivas;

  public Curso(String idCurso, String nombreCurso, String idCarrera, int totalHorasLectivas) {
    if (totalHorasLectivas < 0) {
      throw new IllegalArgumentException("totalHorasLectivas no puede ser negativo");
    }
    this.idCurso = idCurso;
    this.nombreCurso = nombreCurso;
    this.idCarrera = idCarrera;
    this.totalHorasLectivas = totalHorasLectivas;
  }

  public String getIdCurso() {
    return idCurso;
  }

  public String getNombreCurso() {
    return nombreCurso;
  }

  public String getIdCarrera() {
    return idCarrera;
  }

  public int getTotalHorasLectivas() {
    return totalHorasLectivas;
  }
}
