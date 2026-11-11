package models;

/**
 * Menu principal al que se abre la sesion segun el rol de la cuenta.
 * NINGUNO indica que el rol no tiene un menu asignado: la aplicacion avisa y
 * cierra la sesion en vez de abrir el menu de otro rol.
 */
public enum MenuSesion {
  PROFESOR,
  ESTUDIANTE,
  NINGUNO
}
