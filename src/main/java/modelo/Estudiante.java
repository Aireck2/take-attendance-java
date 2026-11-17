package modelo;

/**
 * Representa a un estudiante del sistema de asistencia.
 * Guarda su codigo, datos personales, documento de identidad y estado
 * academico.
 */
public class Estudiante {
  private String id;
  private String codigo;
  private String nombres;
  private String apellidos;
  private String dni;
  private String correo;
  private EstadoEstudiante estado;

  public Estudiante(String id, String codigo, String nombres, String apellidos, String dni, String correo,
      EstadoEstudiante estado) {
    this.id = id;
    this.codigo = codigo;
    this.nombres = nombres;
    this.apellidos = apellidos;
    this.dni = dni;
    this.correo = correo;
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

  public String getDni() {
    return dni;
  }

  public String getCorreo() {
    return correo;
  }

  public EstadoEstudiante getEstado() {
    return estado;
  }

  public void mostrarPerfil() {
    imprimirTitulo("PERFIL DEL ESTUDIANTE");
    System.out.println("ID: " + id);
    System.out.println("Codigo: " + codigo);
    System.out.println("Nombres: " + nombres);
    System.out.println("Apellidos: " + apellidos);
    System.out.println("DNI: " + dni);
    System.out.println("Correo: " + correo);
    System.out.println("Estado: " + estado);
  }

  private void imprimirTitulo(String titulo) {
    int ancho = Math.max(46, titulo.length() + 4);
    int espaciosIniciales = (ancho - titulo.length()) / 2;
    String borde = "=".repeat(ancho);
    System.out.println("\n" + borde);
    System.out.println(" ".repeat(espaciosIniciales) + titulo);
    System.out.println(borde);
  }
}
