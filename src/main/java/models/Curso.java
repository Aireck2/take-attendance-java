package models;

/**
 * Unidad Didactica de un plan de estudios: pertenece a un modulo profesional y
 * declara sus creditos, horas teoricas y practicas, horas por sesion y duracion
 * en semanas. El docente no pertenece al curso: se resuelve por la franja
 * horaria y la sesion de clase.
 */
public class Curso {
  private String idCurso;
  private String nombreCurso;
  private String idModulo;
  private int creditos;
  private int horasTeoricas;
  private int horasPracticas;
  private int totalHorasSesion;
  private int duracionCurso;

  public Curso(String idCurso, String nombreCurso, int totalHorasSesion, int duracionCurso) {
    this(idCurso, nombreCurso, "", 0, 0, 0, totalHorasSesion, duracionCurso);
  }

  public Curso(String idCurso, String nombreCurso, String idModulo, int creditos, int horasTeoricas,
      int horasPracticas, int totalHorasSesion, int duracionCurso) {
    this.idCurso = idCurso;
    this.nombreCurso = nombreCurso;
    this.idModulo = idModulo;
    this.creditos = creditos;
    this.horasTeoricas = horasTeoricas;
    this.horasPracticas = horasPracticas;
    this.totalHorasSesion = totalHorasSesion;
    this.duracionCurso = duracionCurso;
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

  public int getTotalHorasSesion() {
    return totalHorasSesion;
  }

  public int getDuracionCurso() {
    return duracionCurso;
  }

  /**
   * Total de horas lectivas de la unidad didactica.
   * Si la fila es legada y no declara horas teoricas/practicas, se deriva
   * de la duracion del curso por las horas de cada sesion.
   */
  public int getHorasLectivasTotales() {
    int suma = horasTeoricas + horasPracticas;
    if (suma > 0) {
      return suma;
    }
    return duracionCurso * totalHorasSesion;
  }
}
