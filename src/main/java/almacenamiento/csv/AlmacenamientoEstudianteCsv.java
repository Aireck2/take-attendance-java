package almacenamiento.csv;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import models.Estudiante;
import models.EstadoEstudiante;

/** Lee estudiantes desde su archivo CSV. */
final class AlmacenamientoEstudianteCsv {
  private static final String ARCHIVO = "data/estudiante.csv";

  private AlmacenamientoEstudianteCsv() {}

  static List<Estudiante> leerTodos() {
    List<Estudiante> estudiantes = new ArrayList<>();
    try (BufferedReader lector = new BufferedReader(new FileReader(ARCHIVO))) {
      lector.readLine();
      String linea;
      while ((linea = lector.readLine()) != null) {
        String[] campos = linea.split(",");
        if (campos.length >= 7) {
          estudiantes.add(new Estudiante(campos[0], campos[1], campos[2], campos[3], campos[4], campos[5],
              EstadoEstudiante.valueOf(campos[6])));
        }
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
    return estudiantes;
  }
}
