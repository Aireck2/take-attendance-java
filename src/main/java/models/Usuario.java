package models;

/**
 * Cuenta de acceso al sistema. Vincula un correo y una contrasena cifrada
 * con un perfil de Profesor o Estudiante mediante idReferencia.
 */
public class Usuario {
  private String id;
  private String correo;
  private String hashContrasena;
  private Rol rol;
  private String idReferencia;
  private boolean activo;

  public Usuario(String id, String correo, String hashContrasena, Rol rol, String idReferencia, boolean activo) {
    this.id = id;
    this.correo = correo;
    this.hashContrasena = hashContrasena;
    this.rol = rol;
    this.idReferencia = idReferencia;
    this.activo = activo;
  }

  public String getId() {
    return id;
  }

  public String getCorreo() {
    return correo;
  }

  public String getHashContrasena() {
    return hashContrasena;
  }

  public Rol getRol() {
    return rol;
  }

  public String getIdReferencia() {
    return idReferencia;
  }

  public boolean isActivo() {
    return activo;
  }

  public boolean esProfesor() {
    return rol == Rol.PROFESOR;
  }

  public boolean esEstudiante() {
    return rol == Rol.ESTUDIANTE;
  }
}
