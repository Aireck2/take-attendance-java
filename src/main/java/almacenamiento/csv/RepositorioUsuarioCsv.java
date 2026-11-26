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
    return LectorCsv.leer("data/usuario.csv", c -> c.length >= 5
        ? new Usuario(c[0], c[1], Rol.valueOf(c[2]), c[3], Boolean.parseBoolean(c[4]))
        : null).stream()
        .filter(usuario -> usuario.getCorreo().equalsIgnoreCase(buscado))
        .findFirst();
  }
}
