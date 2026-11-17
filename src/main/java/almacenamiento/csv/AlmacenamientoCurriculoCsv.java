package almacenamiento.csv;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import models.Carrera;
import models.Curso;
import models.EstadoPeriodo;
import models.EstadoPlan;
import models.ModuloProfesional;
import models.PeriodoAcademico;
import models.PlanEstudios;

/** Lee los catálogos curriculares utilizados por el flujo de asistencia. */
final class AlmacenamientoCurriculoCsv {
  private AlmacenamientoCurriculoCsv() {}

  static List<Carrera> leerCarreras() {
    List<Carrera> resultado = new ArrayList<>();
    try (BufferedReader lector = new BufferedReader(new FileReader("data/carrera.csv"))) {
      lector.readLine();
      String linea;
      while ((linea = lector.readLine()) != null) {
        if (linea.isBlank()) continue;
        String[] campos = linea.split(",", -1);
        if (campos.length >= 3) resultado.add(new Carrera(campos[0].trim(), campos[1].trim(), campos[2].trim()));
        else if (campos.length >= 2) resultado.add(new Carrera(campos[0].trim(), campos[1].trim(), "Educación Superior"));
      }
    } catch (FileNotFoundException e) {
      System.out.println("No se encontro data/carrera.csv");
    } catch (IOException e) {
      e.printStackTrace();
    }
    return resultado;
  }

  static List<PlanEstudios> leerPlanesEstudios() {
    List<PlanEstudios> resultado = new ArrayList<>();
    try (BufferedReader lector = new BufferedReader(new FileReader("data/plan_estudios.csv"))) {
      lector.readLine();
      String linea;
      while ((linea = lector.readLine()) != null) {
        if (linea.isBlank()) continue;
        String[] campos = linea.split(",", -1);
        if (campos.length >= 7) {
          resultado.add(new PlanEstudios(campos[0].trim(), campos[1].trim(), campos[2].trim(), campos[3].trim(),
              Integer.parseInt(campos[4].trim()), Integer.parseInt(campos[5].trim()), EstadoPlan.valueOf(campos[6].trim())));
        }
      }
    } catch (FileNotFoundException e) {
      System.out.println("No se encontro data/plan_estudios.csv");
    } catch (IOException e) {
      e.printStackTrace();
    }
    return resultado;
  }

  static List<ModuloProfesional> leerModulosProfesionales() {
    List<ModuloProfesional> resultado = new ArrayList<>();
    try (BufferedReader lector = new BufferedReader(new FileReader("data/modulo_profesional.csv"))) {
      lector.readLine();
      String linea;
      while ((linea = lector.readLine()) != null) {
        if (linea.isBlank()) continue;
        String[] campos = linea.split(",", -1);
        if (campos.length >= 5) {
          resultado.add(new ModuloProfesional(campos[0].trim(), campos[1].trim(), campos[2].trim(), campos[3].trim(),
              Integer.parseInt(campos[4].trim())));
        }
      }
    } catch (FileNotFoundException e) {
      System.out.println("No se encontro data/modulo_profesional.csv");
    } catch (IOException e) {
      e.printStackTrace();
    }
    return resultado;
  }

  static List<PeriodoAcademico> leerPeriodosAcademicos() {
    List<PeriodoAcademico> resultado = new ArrayList<>();
    try (BufferedReader lector = new BufferedReader(new FileReader("data/periodo_academico.csv"))) {
      lector.readLine();
      String linea;
      while ((linea = lector.readLine()) != null) {
        if (linea.isBlank()) continue;
        String[] campos = linea.split(",", -1);
        if (campos.length >= 6) {
          int semanas = campos.length >= 7 && !campos[6].isBlank()
              ? Integer.parseInt(campos[6].trim()) : PeriodoAcademico.DEFAULT_SEMANAS_LECTIVAS;
          resultado.add(new PeriodoAcademico(campos[0].trim(), campos[1].trim(), campos[2].trim(), campos[3].trim(),
              campos[4].trim(), EstadoPeriodo.valueOf(campos[5].trim()), semanas));
        }
      }
    } catch (FileNotFoundException e) {
      System.out.println("No se encontro data/periodo_academico.csv");
    } catch (IOException e) {
      e.printStackTrace();
    }
    return resultado;
  }

  static List<Curso> leerCursos() {
    List<Curso> resultado = new ArrayList<>();
    try (BufferedReader lector = new BufferedReader(new FileReader("data/curso.csv"))) {
      String cabecera = lector.readLine();
      boolean tieneTotalHorasLectivas = cabecera != null && cabecera.contains("total_horas_lectivas");
      String linea;
      while ((linea = lector.readLine()) != null) {
        if (linea.isBlank()) continue;
        String[] campos = linea.split(",", -1);
        if (tieneTotalHorasLectivas && campos.length >= 7) {
          resultado.add(new Curso(campos[0].trim(), campos[1].trim(), campos[2].trim(),
              Integer.parseInt(campos[3].trim()), Integer.parseInt(campos[4].trim()),
              Integer.parseInt(campos[5].trim()), Integer.parseInt(campos[6].trim())));
        } else if (campos.length >= 8) {
          resultado.add(new Curso(campos[0].trim(), campos[1].trim(), campos[2].trim(),
              Integer.parseInt(campos[3].trim()), Integer.parseInt(campos[4].trim()), Integer.parseInt(campos[5].trim()),
              Integer.parseInt(campos[6].trim()), Integer.parseInt(campos[7].trim())));
        } else if (campos.length >= 7) {
          resultado.add(new Curso(campos[0].trim(), campos[1].trim(), campos[2].trim(),
              Integer.parseInt(campos[3].trim()), Integer.parseInt(campos[4].trim()), Integer.parseInt(campos[5].trim()),
              Integer.parseInt(campos[4].trim()) + Integer.parseInt(campos[5].trim())));
        } else if (campos.length >= 5) {
          resultado.add(new Curso(campos[0].trim(), campos[1].trim(), Integer.parseInt(campos[2].trim()),
              Integer.parseInt(campos[4].trim())));
        }
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
    return resultado;
  }
}
