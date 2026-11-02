package models;

/**
 * Representa a un estudiante del sistema de asistencia universitaria.
 * Guarda su codigo, datos personales, carrera y estado academico.
 */
public class Estudiante {
  private String id;
  private String codigo;
  private String nombres;
  private String apellidos;
  private String correo;
  private String escuelaProfesional;
  private EstadoEstudiante estado;

  public Estudiante(String id, String codigo, String nombres, String apellidos, String correo, String escuelaProfesional,
      EstadoEstudiante estado) {
    this.id = id;
    this.codigo = codigo;
    this.nombres = nombres;
    this.apellidos = apellidos;
    this.correo = correo;
    this.escuelaProfesional = escuelaProfesional;
    this.estado = estado;
  }

  public String getId() {
    return id;
  }

  public String getCodigo() {
    return codigo;
  }

  public String getNombreCompleto() {
    return nombres + " " + apellidos;
  }

  public String getCorreo() {
    return correo;
  }

  public String getEscuelaProfesional() {
    return escuelaProfesional;
  }

  public EstadoEstudiante getEstado() {
    return estado;
  }

  public void mostrarPerfil() {
    System.out.println();
    System.out.println("--- PERFIL DEL ESTUDIANTE ---");
    System.out.println("ID: " + id);
    System.out.println("Codigo: " + codigo);
    System.out.println("Nombres: " + nombres);
    System.out.println("Apellidos: " + apellidos);
    System.out.println("Correo: " + correo);
    System.out.println("Escuela profesional: " + escuelaProfesional);
    System.out.println("Estado: " + estado);
  }
}
