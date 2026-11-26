package modelo;

/** Estudiante registrado en el sistema de asistencia. */
public class Estudiante extends Persona {
  private final String codigo;
  private final String dni;
  private final String estado;

  public Estudiante(String id, String codigo, String nombres, String apellidos, String dni, String correo,
      String estado) {
    super(id, nombres, apellidos, correo);
    this.codigo = codigo;
    this.dni = dni;
    this.estado = estado;
  }

  public String getCodigo() {
    return codigo;
  }

  public String getDni() {
    return dni;
  }

  public String getEstado() {
    return estado;
  }
}
