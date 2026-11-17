package almacenamiento.csv;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import modelo.Profesor;

/** Lee profesores desde su archivo CSV. */
final class AlmacenamientoProfesorCsv {
  private static final String ARCHIVO = "data/profesor.csv";

  private AlmacenamientoProfesorCsv() {
  }

  static List<Profesor> leerTodos() {
    List<Profesor> profesores = new ArrayList<>();
    try (BufferedReader lector = new BufferedReader(new FileReader(ARCHIVO))) {
      lector.readLine();
      String linea;
      while ((linea = lector.readLine()) != null) {
        String[] campos = linea.split(",");
        if (campos.length >= 9) {
          profesores.add(new Profesor(campos[0], campos[1], campos[2], campos[3], campos[4], campos[5],
              Integer.parseInt(campos[6]), campos[7], campos[8]));
        }
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
    return profesores;
  }
}
