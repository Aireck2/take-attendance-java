package servicio;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;

import modelo.PeriodoAcademico;

/**
 * Teaching-week calculations based on an academic period's configured dates.
 */
final class CalendarioAcademico {
  private CalendarioAcademico() {
  }

  static int semanaLectiva(PeriodoAcademico periodo, LocalDate fecha) {
    if (periodo == null || fecha == null || periodo.getSemanasLectivas() <= 0)
      return 0;
    if (periodo.getFechaInicio() == null || periodo.getFechaFin() == null
        || periodo.getFechaInicio().isBlank() || periodo.getFechaFin().isBlank())
      return 0;
    try {
      LocalDate inicio = LocalDate.parse(periodo.getFechaInicio());
      LocalDate fin = LocalDate.parse(periodo.getFechaFin());
      if (fecha.isBefore(inicio) || fecha.isAfter(fin))
        return 0;
      long dias = ChronoUnit.DAYS.between(inicio, fecha);
      if (dias >= (long) periodo.getSemanasLectivas() * 7)
        return 0;
      return (int) (dias / 7) + 1;
    } catch (DateTimeParseException e) {
      return 0;
    }
  }
}
