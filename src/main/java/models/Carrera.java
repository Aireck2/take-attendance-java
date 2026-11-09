package models;

/**
 * Carrera / Programa de Estudios: programa profesional tecnico autorizado por
 * la institucion. Nivel institucional superior/estrategico.
 * Administra uno o varios planes de estudio vigentes o historicos.
 */
public class Carrera {
  private String idCarrera;
  private String nombre;

  public Carrera(String idCarrera, String nombre) {
    this.idCarrera = idCarrera;
    this.nombre = nombre;
  }

  public String getIdCarrera() {
    return idCarrera;
  }

  public String getNombre() {
    return nombre;
  }
}
