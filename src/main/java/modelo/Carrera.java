package modelo;

/** Carrera o programa de estudios autorizado por la institucion. */
public class Carrera {
  private final String idCarrera;
  private final String nombre;

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
