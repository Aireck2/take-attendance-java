package modelo;

/** Profesor registrado en el sistema de control de asistencia. */
public class Profesor extends Persona {
  private final String tipoDeDocumento;
  private final String nroDocumento;
  private final int telefono;
  private final String departamentoAcademico;
  private final String especialidad;

  public Profesor(String id, String nombre, String apellido, String correo, String tipoDeDocumento, String nroDocumento,
      int telefono, String departamentoAcademico, String especialidad) {
    super(id, nombre, apellido, correo);
    this.tipoDeDocumento = tipoDeDocumento;
    this.nroDocumento = nroDocumento;
    this.telefono = telefono;
    this.departamentoAcademico = departamentoAcademico;
    this.especialidad = especialidad;
  }

  public int getTelefono() {
    return telefono;
  }

  public String getTipoDeDocumento() {
    return tipoDeDocumento;
  }

  public String getNroDocumento() {
    return nroDocumento;
  }

  public String getDepartamentoAcademico() {
    return departamentoAcademico;
  }

  public String getEspecialidad() {
    return especialidad;
  }
}
