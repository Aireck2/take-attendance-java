package almacenamiento.csv;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import models.Rol;
import models.Usuario;

/** Lee cuentas de usuario desde su archivo CSV. */
final class AlmacenamientoUsuarioCsv {
  private static final String ARCHIVO = "data/usuario.csv";

  private AlmacenamientoUsuarioCsv() {}

  static List<Usuario> leerTodos() {
    List<Usuario> usuarios = new ArrayList<>();
    try (BufferedReader lector = new BufferedReader(new FileReader(ARCHIVO))) {
      lector.readLine();
      String linea;
      while ((linea = lector.readLine()) != null) {
        if (linea.isBlank()) continue;
        String[] campos = linea.split(",", -1);
        if (campos.length >= 6) {
          usuarios.add(new Usuario(campos[0].trim(), campos[1].trim(), campos[2].trim(), Rol.valueOf(campos[3].trim()),
              campos[4].trim(), Boolean.parseBoolean(campos[5].trim())));
        }
      }
    } catch (FileNotFoundException e) {
      System.out.println("No se encontro " + ARCHIVO);
    } catch (IOException e) {
      e.printStackTrace();
    }
    return usuarios;
  }
}
