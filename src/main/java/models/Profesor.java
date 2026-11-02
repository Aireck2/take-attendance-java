package models;

/**
 * Representa a un profesor del sistema de asistencia universitaria.
 * Contiene sus datos personales, de contacto y su especialidad academica.
 */
public class Profesor {
  private String id;
  private String nombre;
  private String apellido;
  private String correo;
  private String tipoDeDocumento;
  private String nroDocumento;
  private int telefono;
  private String departamentoAcademico;
  private String especialidad;

  public Profesor(String id, String nombre, String apellido, String correo, String tipoDeDocumento, String nroDocumento,
      int telefono, String departamentoAcademico, String especialidad) {
    this.id = id;
    this.nombre = nombre;
    this.apellido = apellido;
    this.correo = correo;
    this.tipoDeDocumento = tipoDeDocumento;
    this.nroDocumento = nroDocumento;
    this.telefono = telefono;
    this.departamentoAcademico = departamentoAcademico;
    this.especialidad = especialidad;
  }

  public String getId() {
    return id;
  }

  public String getNombre() {
    return nombre;
  }

  public String getApellido() {
    return apellido;
  }

  public String getNombreCompleto() {
    return nombre + " " + apellido;
  }

  public String getCorreo() {
    return correo;
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

  public void mostrarPerfil() {
    System.out.println();
    System.out.println("--- PERFIL DEL PROFESOR ---");
    System.out.println("ID: " + id);
    System.out.println("Nombre: " + nombre);
    System.out.println("Apellido: " + apellido);
    System.out.println("Correo: " + correo);
    System.out.println("Telefono: " + telefono);
    System.out.println("Tipo de documento: " + tipoDeDocumento);
    System.out.println("Nro de documento: " + nroDocumento);
    System.out.println("Departamento academico: " + departamentoAcademico);
    System.out.println("Especialidad: " + especialidad);
  }
}
