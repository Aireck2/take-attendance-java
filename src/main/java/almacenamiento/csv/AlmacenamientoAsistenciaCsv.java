package almacenamiento.csv;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import models.RegistroDeAsistencia;

/** Lee y agrega registros en el archivo CSV de asistencia. */
final class AlmacenamientoAsistenciaCsv {
  private static final String ARCHIVO = "data/registro_de_asistencia.csv";

  private AlmacenamientoAsistenciaCsv() {}

  static List<RegistroDeAsistencia> leerTodos() {
    List<RegistroDeAsistencia> registros = new ArrayList<>();
    try (BufferedReader lector = new BufferedReader(new FileReader(ARCHIVO))) {
      lector.readLine();
      String linea;
      while ((linea = lector.readLine()) != null) {
        if (linea.isBlank()) continue;
        String[] campos = linea.split(",", -1);
        if (campos.length < 6) continue;
        String idSesion = campos.length >= 7 ? campos[6].trim() : "";
        String idMatricula = campos.length >= 8 ? campos[7].trim() : "";
        registros.add(new RegistroDeAsistencia(campos[0].trim(), campos[1].trim(), campos[2].trim(),
            campos[3].trim(), Integer.parseInt(campos[4].trim()), esPresente(campos[5].trim()), idSesion,
            idMatricula));
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
    return registros;
  }

  static void agregar(List<RegistroDeAsistencia> registros) {
    try (PrintWriter escritor = new PrintWriter(new FileWriter(ARCHIVO, true))) {
      for (RegistroDeAsistencia registro : registros) escritor.println(registro.toCsv());
    } catch (IOException e) {
      System.out.println("Error al guardar asistencias.");
    }
  }

  private static boolean esPresente(String valor) {
    return "1".equals(valor) || "true".equalsIgnoreCase(valor);
  }
}
