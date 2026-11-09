package models;

/**
 * Horario de seccion / franja horaria: regla semanal recurrente con dia,
 * rango de horas, tipo de clase, docente y ambiente reservado.
 */
public class HorarioSeccion {
  private String idHorario;
  private String idSeccion;
  private DiaSemana diaSemana;
  private String horaInicio;
  private String horaFin;
  private TipoClase tipoClase;
  private String idProfesor;
  private String idSalon;

  public HorarioSeccion(String idHorario, String idSeccion, DiaSemana diaSemana, String horaInicio, String horaFin,
      TipoClase tipoClase, String idProfesor, String idSalon) {
    this.idHorario = idHorario;
    this.idSeccion = idSeccion;
    this.diaSemana = diaSemana;
    this.horaInicio = horaInicio;
    this.horaFin = horaFin;
    this.tipoClase = tipoClase;
    this.idProfesor = idProfesor;
    this.idSalon = idSalon;
  }

  public String getIdHorario() {
    return idHorario;
  }

  public String getIdSeccion() {
    return idSeccion;
  }

  public DiaSemana getDiaSemana() {
    return diaSemana;
  }

  public String getHoraInicio() {
    return horaInicio;
  }

  public String getHoraFin() {
    return horaFin;
  }

  public TipoClase getTipoClase() {
    return tipoClase;
  }

  public String getIdProfesor() {
    return idProfesor;
  }

  public String getIdSalon() {
    return idSalon;
  }
}
