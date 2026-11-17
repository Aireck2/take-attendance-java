package almacenamiento.csv;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import models.BloqueSeccion;
import models.DiaSemana;
import models.EstadoMatricula;
import models.HorarioSeccion;
import models.Matricula;
import models.ModalidadDictado;
import models.Salon;
import models.SeccionCurso;
import models.SesionClase;
import models.TipoClase;
import models.TipoSalon;
import models.Turno;

/** Lee bloques, secciones, horarios, sesiones y matrículas desde CSV. */
final class AlmacenamientoProgramacionCsv {
  private AlmacenamientoProgramacionCsv() {}

  static List<BloqueSeccion> leerBloques() {
    List<BloqueSeccion> resultado = new ArrayList<>();
    try (BufferedReader lector = lector("bloque_seccion.csv")) {
      lector.readLine();
      String linea;
      while ((linea = lector.readLine()) != null) {
        if (linea.isBlank()) continue;
        String[] campos = linea.split(",", -1);
        if (campos.length >= 4) resultado.add(new BloqueSeccion(campos[0].trim(), campos[1].trim(), campos[2].trim(),
            Turno.valueOf(campos[3].trim())));
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
    return resultado;
  }

  static List<SeccionCurso> leerSecciones() {
    List<SeccionCurso> resultado = new ArrayList<>();
    try (BufferedReader lector = lector("seccion_curso.csv")) {
      lector.readLine();
      String linea;
      while ((linea = lector.readLine()) != null) {
        if (linea.isBlank()) continue;
        String[] campos = linea.split(",", -1);
        if (campos.length >= 5) resultado.add(new SeccionCurso(campos[0].trim(), campos[1].trim(), campos[2].trim(),
            ModalidadDictado.valueOf(campos[3].trim()), Integer.parseInt(campos[4].trim())));
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
    return resultado;
  }

  static List<HorarioSeccion> leerHorarios() {
    List<HorarioSeccion> resultado = new ArrayList<>();
    try (BufferedReader lector = lector("horario_seccion.csv")) {
      lector.readLine();
      String linea;
      while ((linea = lector.readLine()) != null) {
        if (linea.isBlank()) continue;
        String[] campos = linea.split(",", -1);
        if (campos.length >= 8) resultado.add(new HorarioSeccion(campos[0].trim(), campos[1].trim(),
            DiaSemana.valueOf(campos[2].trim()), campos[3].trim(), campos[4].trim(), TipoClase.valueOf(campos[5].trim()),
            campos[6].trim(), campos[7].trim()));
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
    return resultado;
  }

  static List<SesionClase> leerSesiones() {
    List<SesionClase> resultado = new ArrayList<>();
    try (BufferedReader lector = lector("sesion_clase.csv")) {
      lector.readLine();
      String linea;
      while ((linea = lector.readLine()) != null) {
        if (linea.isBlank()) continue;
        String[] campos = linea.split(",", -1);
        if (campos.length >= 6) resultado.add(new SesionClase(campos[0].trim(), campos[1].trim(), campos[2].trim(),
            Integer.parseInt(campos[3].trim()), campos[4].trim(), campos[5].trim()));
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
    return resultado;
  }

  static List<Salon> leerSalones() {
    List<Salon> resultado = new ArrayList<>();
    try (BufferedReader lector = lector("salon.csv")) {
      lector.readLine();
      String linea;
      while ((linea = lector.readLine()) != null) {
        if (linea.isBlank()) continue;
        String[] campos = linea.split(",", -1);
        if (campos.length >= 4) resultado.add(new Salon(campos[0].trim(), campos[1].trim(),
            TipoSalon.valueOf(campos[2].trim()), Integer.parseInt(campos[3].trim())));
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
    return resultado;
  }

  static List<Matricula> leerMatriculas() {
    List<Matricula> resultado = new ArrayList<>();
    try (BufferedReader lector = lector("matricula.csv")) {
      lector.readLine();
      String linea;
      while ((linea = lector.readLine()) != null) {
        if (linea.isBlank()) continue;
        String[] campos = linea.split(",", -1);
        if (campos.length >= 5) resultado.add(new Matricula(campos[0].trim(), campos[1].trim(), campos[2].trim(),
            campos[3].trim(), EstadoMatricula.valueOf(campos[4].trim())));
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
    return resultado;
  }

  private static BufferedReader lector(String archivo) throws IOException {
    String ruta = "data/" + archivo;
    try {
      return new BufferedReader(new FileReader(ruta));
    } catch (FileNotFoundException e) {
      System.out.println("No se encontro " + ruta);
      return new BufferedReader(new StringReader(""));
    }
  }
}
