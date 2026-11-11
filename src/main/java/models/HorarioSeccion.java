package models;

import java.time.Duration;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;

/**
 * Horario de seccion / franja horaria: regla semanal recurrente con dia,
 * rango de horas, tipo de clase, docente y ambiente reservado. Su duracion
 * recurrente distribuye la carga semanal de la unidad didactica.
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
    getDuracionMinutos();
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

  /** Duracion del bloque semanal, expresada en minutos para conservar fracciones de hora. */
  public int getDuracionMinutos() {
    try {
      long minutos = Duration.between(LocalTime.parse(horaInicio), LocalTime.parse(horaFin)).toMinutes();
      if (minutos <= 0 || minutos > Integer.MAX_VALUE) {
        throw new IllegalArgumentException("El horario debe tener una duracion positiva en el mismo dia");
      }
      return (int) minutos;
    } catch (DateTimeParseException e) {
      throw new IllegalArgumentException("Las horas del horario deben usar el formato HH:mm", e);
    }
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
