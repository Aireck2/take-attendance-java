package models;

/**
 * Representa un curso del instituto, con sus horas por sesion academica,
 * el profesor responsable y la duracion del curso en dias de clase.
 *
 * Tambien modela la Unidad Didactica del plan de estudios: pertenece a un
 * modulo profesional y declara sus horas lectivas teoricas y practicas.
 */
public class Curso {
  private String idCurso;
  private String nombreCurso;
  private int totalHorasSesion;
  private String idProfesor;
  private int duracionCurso;
  private String idModulo;
  private int creditos;
  private int horasTeoricas;
  private int horasPracticas;

  public Curso(String idCurso, String nombreCurso, int totalHorasSesion, String idProfesor, int duracionCurso) {
    this(idCurso, nombreCurso, totalHorasSesion, idProfesor, duracionCurso, "", 0, 0, 0);
  }

  public Curso(String idCurso, String nombreCurso, int totalHorasSesion, String idProfesor, int duracionCurso,
      String idModulo, int creditos, int horasTeoricas, int horasPracticas) {
    this.idCurso = idCurso;
    this.nombreCurso = nombreCurso;
    this.totalHorasSesion = totalHorasSesion;
    this.idProfesor = idProfesor;
    this.duracionCurso = duracionCurso;
    this.idModulo = idModulo;
    this.creditos = creditos;
    this.horasTeoricas = horasTeoricas;
    this.horasPracticas = horasPracticas;
  }

  public String getIdCurso() {
    return idCurso;
  }

  public String getNombreCurso() {
    return nombreCurso;
  }

  public int getTotalHorasSesion() {
    return totalHorasSesion;
  }

  public String getIdProfesor() {
    return idProfesor;
  }

  public int getDuracionCurso() {
    return duracionCurso;
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
