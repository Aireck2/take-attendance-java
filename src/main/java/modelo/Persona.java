package modelo;

/** Base comun para personas registradas en el sistema academico. */
public abstract class Persona {
  private final String id;
  private final String nombres;
  private final String apellidos;
  private final String correo;

  protected Persona(String id, String nombres, String apellidos, String correo) {
    this.id = id;
    this.nombres = nombres;
    this.apellidos = apellidos;
    this.correo = correo;
  }

  public String getId() {
    return id;
  }

  public String getNombres() {
    return nombres;
  }

  public String getApellidos() {
    return apellidos;
  }

  public String getNombreCompleto() {
    return nombres + " " + apellidos;
  }

  public String getCorreo() {
    return correo;
  }
}
