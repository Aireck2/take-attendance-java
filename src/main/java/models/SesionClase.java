package models;

/**
 * Sesion de clase: instancia real de una franja en una fecha exacta, con
 * docente, ambiente final y horas programadas.
 */
public class SesionClase {
  private String idSesion;
  private String idHorario;
  private String fecha;
  private int horasProgramadas;
  private String idProfesor;
  private String idSalon;

  public SesionClase(String idSesion, String idHorario, String fecha, int horasProgramadas, String idProfesor,
      String idSalon) {
    this.idSesion = idSesion;
    this.idHorario = idHorario;
    this.fecha = fecha;
    this.horasProgramadas = horasProgramadas;
    this.idProfesor = idProfesor;
    this.idSalon = idSalon;
  }

  public String getIdSesion() {
    return idSesion;
  }

  public String getIdHorario() {
    return idHorario;
  }

  public String getFecha() {
    return fecha;
  }

  public int getHorasProgramadas() {
    return horasProgramadas;
  }

  public String getIdProfesor() {
    return idProfesor;
  }

  public String getIdSalon() {
    return idSalon;
  }
}
