package service;

import java.util.Locale;

import almacenamiento.csv.RepositorioUsuarioCsv;
import models.Usuario;
import repositorio.RepositorioUsuario;
import security.PasswordHasher;

/**
 * Valida credenciales de acceso contra los usuarios persistidos.
 */
public class AutenticacionService {
  private final RepositorioUsuario repositorio;

  /**
   * Hash ficticio para gastar el mismo tiempo de calculo cuando el correo no
   * existe y no filtrar por diferencias de tiempo si una cuenta esta registrada.
   */
  private static final String HASH_FICTICIO = PasswordHasher.hash("usuario-inexistente");

  public AutenticacionService() {
    this(new RepositorioUsuarioCsv());
  }

  public AutenticacionService(RepositorioUsuario repositorio) {
    this.repositorio = repositorio;
  }

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

    Usuario usuario = repositorio.buscarPorCorreo(correoNormalizado).orElse(null);
    if (usuario == null || !usuario.isActivo()) {
      PasswordHasher.verificar(contrasena, HASH_FICTICIO);
      return null;
    }
    if (!PasswordHasher.verificar(contrasena, usuario.getHashContrasena())) {
      return null;
    }
    return usuario;
  }

}
