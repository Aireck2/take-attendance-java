package modelo;

/**
 * Cuenta de acceso al sistema. Vincula un correo y una contrasena cifrada
 * con un perfil de Profesor o Estudiante mediante idReferencia.
 */
public class Usuario {
  private final String correo;
  private final String hashContrasena;
  private final Rol rol;
  private final String idReferencia;
  private final boolean activo;

  public Usuario(String correo, String hashContrasena, Rol rol, String idReferencia, boolean activo) {
    this.correo = correo;
    this.hashContrasena = hashContrasena;
    this.rol = rol;
    this.idReferencia = idReferencia;
    this.activo = activo;
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
}
