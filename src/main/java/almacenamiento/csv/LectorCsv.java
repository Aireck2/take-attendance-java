package almacenamiento.csv;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/** Utilidad genérica para leer archivos CSV ignorando cabecera y líneas vacías. */
final class LectorCsv {
  private LectorCsv() {
  }

  static <T> List<T> leer(String ruta, Function<String[], T> mapeador) {
    List<T> resultado = new ArrayList<>();
    try (BufferedReader lector = new BufferedReader(new FileReader(ruta))) {
      if (lector.readLine() == null)
        return resultado;
      String linea;
      while ((linea = lector.readLine()) != null) {
        if (linea.isBlank())
          continue;
        String[] campos = linea.split(",", -1);
        for (int i = 0; i < campos.length; i++)
          campos[i] = campos[i].trim();
        T item = mapeador.apply(campos);
        if (item != null)
          resultado.add(item);
      }
    } catch (FileNotFoundException e) {
      System.out.println("No se encontro " + ruta);
    } catch (IOException e) {
      e.printStackTrace();
    }
    return resultado;
  }
}
