package repositorio;

import java.util.Optional;

import modelo.Usuario;

/** Contrato para consultar usuarios persistidos. */
public interface RepositorioUsuario {
  Optional<Usuario> buscarPorCorreo(String correo);
}
