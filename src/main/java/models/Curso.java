package models;

/**
 * Representa un curso del instituto, con sus horas por sesion academica,
 * el profesor responsable y la duracion del curso en dias de clase.
 */
public class Curso {
  private String idCurso;
  private String nombreCurso;
  private int totalHorasSesion;
  private String idProfesor;
  private int duracionCurso;

  public Curso(String idCurso, String nombreCurso, int totalHorasSesion, String idProfesor, int duracionCurso) {
    this.idCurso = idCurso;
    this.nombreCurso = nombreCurso;
    this.totalHorasSesion = totalHorasSesion;
    this.idProfesor = idProfesor;
    this.duracionCurso = duracionCurso;
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
}
