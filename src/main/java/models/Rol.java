package models;

/**
 * Roles disponibles para iniciar sesion en el sistema.
 */
public enum Rol {
  PROFESOR,
  ESTUDIANTE;

  /**
   * Menu principal al que se redirige la sesion segun el rol.
   * Un rol sin menu asignado devuelve MenuSesion.NINGUNO, de modo que la
   * aplicacion lo reporte en vez de abrir el menu de otro rol.
   */
  public MenuSesion menuSesion() {
    return switch (this) {
      case PROFESOR -> MenuSesion.PROFESOR;
      case ESTUDIANTE -> MenuSesion.ESTUDIANTE;
      default -> MenuSesion.NINGUNO;
    };
  }
}
