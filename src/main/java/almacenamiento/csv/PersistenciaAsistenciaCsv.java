package almacenamiento.csv;

import java.util.List;

import models.RegistroDeAsistencia;

/** Asigna identificadores y agrega lotes de asistencia al archivo CSV. */
final class PersistenciaAsistenciaCsv {
  private PersistenciaAsistenciaCsv() {}

  static void guardar(List<RegistroDeAsistencia> registros) {
    List<RegistroDeAsistencia> existentes = AlmacenamientoAsistenciaCsv.leerTodos();
    int maxId = 0;
    for (RegistroDeAsistencia reg : existentes) {
      String id = reg.getIdAsistencia();
      if (id != null && id.startsWith("REG-ASI-")) {
        try {
          maxId = Math.max(maxId, Integer.parseInt(id.substring("REG-ASI-".length())));
        } catch (NumberFormatException e) {
          // Ignora identificadores con formato distinto.
        }
      }
    }
    int siguienteId = maxId + 1;
    for (RegistroDeAsistencia reg : registros) {
      reg.setIdAsistencia(String.format("REG-ASI-%04d", siguienteId++));
    }
    AlmacenamientoAsistenciaCsv.agregar(registros);
  }
}
