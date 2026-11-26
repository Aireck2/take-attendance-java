package modelo;

import java.time.Duration;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;

/**
 * Horario de seccion / franja horaria semanal recurrente con dia, rango de horas,
 * docente y ambiente reservado.
 */
public class HorarioSeccion {
  private final String idHorario;
  private final String idSeccion;
  private final DiaSemana diaSemana;
  private final String horaInicio;
  private final String horaFin;
  private final String idProfesor;
  private final String idSalon;

  public HorarioSeccion(String idHorario, String idSeccion, DiaSemana diaSemana, String horaInicio, String horaFin,
      String idProfesor, String idSalon) {
    validarRangoHorario(horaInicio, horaFin);
    this.idHorario = idHorario;
    this.idSeccion = idSeccion;
    this.diaSemana = diaSemana;
    this.horaInicio = horaInicio;
    this.horaFin = horaFin;
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

  public String getIdProfesor() {
    return idProfesor;
  }

  public String getIdSalon() {
    return idSalon;
  }

  private static void validarRangoHorario(String horaInicio, String horaFin) {
    try {
      long minutos = Duration.between(LocalTime.parse(horaInicio), LocalTime.parse(horaFin)).toMinutes();
      if (minutos <= 0 || minutos > Integer.MAX_VALUE) {
        throw new IllegalArgumentException("El horario debe tener una duracion positiva en el mismo dia");
      }
    } catch (DateTimeParseException e) {
      throw new IllegalArgumentException("Las horas del horario deben usar el formato HH:mm", e);
    }
  }
}
