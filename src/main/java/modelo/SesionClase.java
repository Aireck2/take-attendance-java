package modelo;

/** Sesion de clase programada en una fecha especifica para una franja horaria. */
public class SesionClase {
  private final String idSesion;
  private final String idHorario;
  private final String fecha;
  private final int horasProgramadas;

  public SesionClase(String idSesion, String idHorario, String fecha, int horasProgramadas) {
    if (horasProgramadas <= 0) {
      throw new IllegalArgumentException("La sesion debe tener horas programadas positivas");
    }
    this.idSesion = idSesion;
    this.idHorario = idHorario;
    this.fecha = fecha;
    this.horasProgramadas = horasProgramadas;
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
}
