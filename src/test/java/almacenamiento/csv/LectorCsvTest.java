package almacenamiento.csv;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/** Pruebas unitarias para la utilidad genérica de lectura CSV. */
public final class LectorCsvTest {
  private LectorCsvTest() {
  }

  public static void ejecutar() {
    probarLecturaIgnorandoCabeceraLineasVaciasYNulos();
    probarArchivoInexistenteDevuelveListaVacia();
  }

  private static void probarLecturaIgnorandoCabeceraLineasVaciasYNulos() {
    Path archivo = crearTemporal("id,nombre\n\n 1 , Ana \nfila_invalida\n2,Luis\n");
    try {
      List<String> resultado = LectorCsv.leer(archivo.toString(),
          campos -> campos.length >= 2 ? campos[0] + ":" + campos[1] : null);
      if (!List.of("1:Ana", "2:Luis").equals(resultado)) {
        throw new AssertionError("LectorCsv no filtró o recortó correctamente: " + resultado);
      }
    } finally {
      borrarSilencioso(archivo);
    }
  }

  private static void probarArchivoInexistenteDevuelveListaVacia() {
    PrintStream original = System.out;
    ByteArrayOutputStream captura = new ByteArrayOutputStream();
    List<String> resultado;
    try {
      System.setOut(new PrintStream(captura, true, StandardCharsets.UTF_8));
      resultado = LectorCsv.leer("data/no_existe_archivo_prueba.csv", campos -> campos[0]);
    } finally {
      System.setOut(original);
    }
    if (!resultado.isEmpty() || !captura.toString(StandardCharsets.UTF_8).contains("No se encontro")) {
      throw new AssertionError("Se esperaba lista vacía y aviso para archivo inexistente");
    }
  }

  private static Path crearTemporal(String contenido) {
    try {
      Path archivo = Files.createTempFile("lector-csv-test-", ".csv");
      Files.writeString(archivo, contenido);
      return archivo;
    } catch (IOException e) {
      throw new AssertionError("No se pudo crear archivo temporal de prueba", e);
    }
  }

  private static void borrarSilencioso(Path archivo) {
    try {
      Files.deleteIfExists(archivo);
    } catch (IOException ignored) {
    }
  }
}
