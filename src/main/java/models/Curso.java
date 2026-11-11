package models;

/**
 * Unidad Didactica de un plan de estudios: pertenece a un modulo profesional y
 * declara sus creditos, horas teoricas y practicas, y el total lectivo que
 * resulta de ambas. Las semanas lectivas pertenecen al periodo academico y
 * la distribucion semanal se representa en los horarios de sus secciones.
 * El docente no pertenece al curso: se resuelve por la franja
 * horaria y la sesion de clase.
 */
public class Curso {
  private String idCurso;
  private String nombreCurso;
  private String idModulo;
  private int creditos;
  private int horasTeoricas;
  private int horasPracticas;
  private int totalHorasLectivas;

  /** Constructor de compatibilidad con la antigua fila de curso totalHorasSesion × duracionCurso. */
  public Curso(String idCurso, String nombreCurso, int totalHorasSesion, int duracionCurso) {
    this(idCurso, nombreCurso, "", 0, 0, 0, totalHorasSesion * duracionCurso);
  }

  /** Constructor de compatibilidad con el formato CSV anterior que incluia duracion y horas por sesion. */
  public Curso(String idCurso, String nombreCurso, String idModulo, int creditos, int horasTeoricas,
      int horasPracticas, int totalHorasSesion, int duracionCurso) {
    this(idCurso, nombreCurso, idModulo, creditos, horasTeoricas, horasPracticas,
        horasTeoricas + horasPracticas);
  }

  public Curso(String idCurso, String nombreCurso, String idModulo, int creditos, int horasTeoricas,
      int horasPracticas, int totalHorasLectivas) {
    this.idCurso = idCurso;
    this.nombreCurso = nombreCurso;
    this.idModulo = idModulo;
    this.creditos = creditos;
    this.horasTeoricas = horasTeoricas;
    this.horasPracticas = horasPracticas;
    int sumaHorasDeclaradas = horasTeoricas + horasPracticas;
    if (sumaHorasDeclaradas > 0 && totalHorasLectivas != sumaHorasDeclaradas) {
      throw new IllegalArgumentException("totalHorasLectivas debe ser la suma de horas teoricas y practicas");
    }
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

  public int getCreditos() {
    return creditos;
  }

  public int getHorasTeoricas() {
    return horasTeoricas;
  }

  public int getHorasPracticas() {
    return horasPracticas;
  }

  public int getTotalHorasLectivas() {
    return totalHorasLectivas;
  }

  /**
   * Alias conservado por compatibilidad con llamadas anteriores.
   */
  public int getHorasLectivasTotales() {
    return getTotalHorasLectivas();
  }
}
