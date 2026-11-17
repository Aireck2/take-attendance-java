package almacenamiento.csv;

import java.util.Optional;

import modelo.Usuario;
import repositorio.RepositorioUsuario;

/** Implementación CSV del repositorio de usuarios. */
public final class RepositorioUsuarioCsv implements RepositorioUsuario {
  @Override
  public Optional<Usuario> buscarPorCorreo(String correo) {
    if (correo == null)
      return Optional.empty();
    return AlmacenamientoUsuarioCsv.leerTodos().stream()
        .filter(usuario -> usuario.getCorreo().equalsIgnoreCase(correo.trim()))
        .findFirst();
  }
}
