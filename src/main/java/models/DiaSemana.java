package models;

/**
 * Dia de la semana de una franja horaria.
 */
public enum DiaSemana {
  LUNES,
  MARTES,
  MIERCOLES,
  JUEVES,
  VIERNES,
  SABADO,
  DOMINGO;

  /**
   * Nombre del dia listo para mostrar en la interfaz.
   */
  public String getNombre() {
    return switch (this) {
      case LUNES -> "Lunes";
      case MARTES -> "Martes";
      case MIERCOLES -> "Miércoles";
      case JUEVES -> "Jueves";
      case VIERNES -> "Viernes";
      case SABADO -> "Sábado";
      case DOMINGO -> "Domingo";
    };
  }
}
