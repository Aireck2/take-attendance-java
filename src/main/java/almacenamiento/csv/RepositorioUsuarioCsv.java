package almacenamiento.csv;

import java.util.Optional;

import modelo.Rol;
import modelo.Usuario;
import repositorio.RepositorioUsuario;

/** Implementación CSV del repositorio de usuarios. */
public final class RepositorioUsuarioCsv implements RepositorioUsuario {
  @Override
  public Optional<Usuario> buscarPorCorreo(String correo) {
    if (correo == null)
      return Optional.empty();
    String buscado = correo.trim();
    return LectorCsv.leer("data/usuario.csv", c -> c.length >= 6
        ? new Usuario(c[0], c[1], c[2], Rol.valueOf(c[3]), c[4], Boolean.parseBoolean(c[5]))
        : null).stream()
        .filter(usuario -> usuario.getCorreo().equalsIgnoreCase(buscado))
        .findFirst();
  }
}
