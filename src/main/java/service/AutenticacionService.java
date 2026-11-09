package service;

import java.util.List;
import java.util.Locale;

import models.Usuario;
import security.PasswordHasher;
import storage.ArchivoManager;

/**
 * Valida credenciales de acceso contra los usuarios persistidos.
 */
public class AutenticacionService {

  /**
   * Hash ficticio para gastar el mismo tiempo de calculo cuando el correo no
   * existe y no filtrar por diferencias de tiempo si una cuenta esta registrada.
   */
  private static final String HASH_FICTICIO = PasswordHasher.hash("usuario-inexistente");

  /**
   * Autentica un correo y una contrasena. Devuelve el usuario activo o null.
   * El correo se compara sin distinguir mayusculas y sin espacios externos.
   */
  public Usuario autenticar(String correo, String contrasena) {
    if (correo == null || contrasena == null) {
      return null;
    }
    String correoNormalizado = correo.trim().toLowerCase(Locale.ROOT);
    if (correoNormalizado.isEmpty() || contrasena.isEmpty()) {
      return null;
    }

    Usuario usuario = buscarPorCorreo(correoNormalizado);
    if (usuario == null || !usuario.isActivo()) {
      PasswordHasher.verificar(contrasena, HASH_FICTICIO);
      return null;
    }
    if (!PasswordHasher.verificar(contrasena, usuario.getHashContrasena())) {
      return null;
    }
    return usuario;
  }

  private Usuario buscarPorCorreo(String correoNormalizado) {
    List<Usuario> usuarios = ArchivoManager.leerUsuarios();
    for (Usuario usuario : usuarios) {
      if (usuario.getCorreo().equalsIgnoreCase(correoNormalizado)) {
        return usuario;
      }
    }
    return null;
  }
}
