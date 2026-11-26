package almacenamiento.csv;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import modelo.RegistroDeAsistencia;

/** Lee, asigna identificadores secuenciales y persiste lotes de asistencia en CSV. */
final class PersistenciaAsistenciaCsv {
  private static final String ARCHIVO = "data/registro_de_asistencia.csv";

  private PersistenciaAsistenciaCsv() {
  }

  static List<RegistroDeAsistencia> leerTodos() {
    return LectorCsv.leer(ARCHIVO, c -> {
      if (c.length < 6)
        return null;
      boolean presente = "1".equals(c[5]) || "true".equalsIgnoreCase(c[5]);
      String idSesion = c.length >= 7 ? c[6] : "";
      String idMatricula = c.length >= 8 ? c[7] : "";
      return new RegistroDeAsistencia(c[0], c[1], c[2], c[3], Integer.parseInt(c[4]), presente, idSesion, idMatricula);
    });
  }

  static void guardar(List<RegistroDeAsistencia> registros) {
    int maxId = 0;
    for (RegistroDeAsistencia reg : leerTodos()) {
      String id = reg.getIdAsistencia();
      if (id != null && id.startsWith("REG-ASI-")) {
        try {
          maxId = Math.max(maxId, Integer.parseInt(id.substring("REG-ASI-".length())));
        } catch (NumberFormatException ignored) {
        }
      }
    }
    int siguienteId = maxId + 1;
    for (RegistroDeAsistencia reg : registros) {
      reg.setIdAsistencia(String.format("REG-ASI-%04d", siguienteId++));
    }
    try (PrintWriter escritor = new PrintWriter(new FileWriter(ARCHIVO, true))) {
      for (RegistroDeAsistencia registro : registros)
        escritor.println(registro.toCsv());
    } catch (IOException e) {
      System.out.println("Error al guardar asistencias.");
    }
  }
}
