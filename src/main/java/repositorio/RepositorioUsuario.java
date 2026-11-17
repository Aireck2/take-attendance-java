package repositorio;

import java.util.Optional;
import models.Usuario;

/** Contrato para consultar usuarios persistidos. */
public interface RepositorioUsuario {
  Optional<Usuario> buscarPorCorreo(String correo);
}
