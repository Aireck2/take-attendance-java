package servicio;

import java.util.Locale;

import modelo.Usuario;
import repositorio.RepositorioUsuario;
import security.PasswordHasher;

/** Valida credenciales de acceso contra el repositorio de usuarios. */
public final class AutenticacionService {
  private static final String HASH_FICTICIO = PasswordHasher.hash("usuario-inexistente");
  private final RepositorioUsuario repositorio;

  public AutenticacionService(RepositorioUsuario repositorio) {
    this.repositorio = repositorio;
  }

  public Usuario autenticar(String correo, String contrasena) {
    if (correo == null || contrasena == null) {
      return null;
    }
    String correoNormalizado = correo.trim().toLowerCase(Locale.ROOT);
    if (correoNormalizado.isEmpty() || contrasena.isEmpty()) {
      return null;
    }

    Usuario usuario = repositorio.buscarPorCorreo(correoNormalizado).orElse(null);
    if (usuario == null || !usuario.isActivo()) {
      PasswordHasher.verificar(contrasena, HASH_FICTICIO);
      return null;
    }
    return PasswordHasher.verificar(contrasena, usuario.getHashContrasena()) ? usuario : null;
  }
}
