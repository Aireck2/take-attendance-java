package storage;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import models.Curso;
import models.Estudiante;
import models.EstadoEstudiante;
import models.Profesor;
import models.RegistroDeAsistencia;
import models.Rol;
import models.Usuario;

/**
 * Administra la persistencia del sistema de asistencia en archivos CSV.
 * Los datos se mantienen en la carpeta 'data'.
 */
public class ArchivoManager {

  private static final String CARPETA_DATOS = "data";

  public static List<Profesor> leerProfesores() {
    List<Profesor> lista = new ArrayList<>();
    try (BufferedReader br = new BufferedReader(new FileReader(CARPETA_DATOS + "/profesores.csv"))) {
      String linea = br.readLine(); // Saltar cabecera
      while ((linea = br.readLine()) != null) {
        String[] p = linea.split(",");
        // id,nombre,apellido,correo,tipo_de_documento,nro_documento,telefono,departamento_academico,especialidad
        if (p.length >= 9) {
          lista.add(new Profesor(p[0], p[1], p[2], p[3], p[4], p[5], Integer.parseInt(p[6]), p[7], p[8]));
        }
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
    return lista;
  }

  public static List<Estudiante> leerEstudiantes() {
    List<Estudiante> lista = new ArrayList<>();
    try (BufferedReader br = new BufferedReader(new FileReader(CARPETA_DATOS + "/estudiantes.csv"))) {
      String linea = br.readLine();
      while ((linea = br.readLine()) != null) {
        String[] p = linea.split(",");
        // id_estudiante,codigo,nombres,apellidos,correo,escuela_profesional,estado
        if (p.length >= 7) {
          lista.add(new Estudiante(p[0], p[1], p[2], p[3], p[4], p[5], EstadoEstudiante.valueOf(p[6])));
        }
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
    return lista;
  }

  public static List<Usuario> leerUsuarios() {
    List<Usuario> lista = new ArrayList<>();
    try (BufferedReader br = new BufferedReader(new FileReader(CARPETA_DATOS + "/usuarios.csv"))) {
      String linea = br.readLine(); // Saltar cabecera
      while ((linea = br.readLine()) != null) {
        if (linea.isBlank()) {
          continue;
        }
        String[] p = linea.split(",", -1);
        // id,correo,hash_contrasena,rol,id_referencia,activo
        if (p.length >= 6) {
          lista.add(new Usuario(p[0].trim(), p[1].trim(), p[2].trim(), Rol.valueOf(p[3].trim()), p[4].trim(),
              Boolean.parseBoolean(p[5].trim())));
        }
      }
    } catch (FileNotFoundException e) {
      System.out.println("No se encontro " + CARPETA_DATOS + "/usuarios.csv");
    } catch (IOException e) {
      e.printStackTrace();
    }
    return lista;
  }

  public static List<Curso> leerCursos() {
    List<Curso> lista = new ArrayList<>();
    try (BufferedReader br = new BufferedReader(new FileReader(CARPETA_DATOS + "/cursos.csv"))) {
      String linea = br.readLine();
      while ((linea = br.readLine()) != null) {
        String[] p = linea.split(",");
        // id_curso,nombre_curso,total_horas_sesion,id_profesor,duracion_curso
        if (p.length >= 5) {
          lista.add(new Curso(p[0], p[1], Integer.parseInt(p[2]), p[3], Integer.parseInt(p[4])));
        }
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
    return lista;
  }

  public static List<String> leerInscripcionesCurso(String idCurso) {
    List<String> estudiantesIds = new ArrayList<>();
    try (BufferedReader br = new BufferedReader(new FileReader(CARPETA_DATOS + "/inscripciones.csv"))) {
      String linea = br.readLine();
      while ((linea = br.readLine()) != null) {
        String[] p = linea.split(",");
        if (p.length >= 2 && p[0].equals(idCurso)) {
          estudiantesIds.add(p[1]);
        }
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
    return estudiantesIds;
  }

  public static void guardarAsistencia(List<RegistroDeAsistencia> registros) {
    try (PrintWriter pw = new PrintWriter(new FileWriter(CARPETA_DATOS + "/asistencias.csv", true))) {
      for (RegistroDeAsistencia reg : registros) {
        pw.println(reg.toCsv());
      }
    } catch (IOException e) {
      System.out.println("Error al guardar asistencias.");
    }
  }

  public static List<RegistroDeAsistencia> leerAsistenciasEstudiante(String idEstudiante) {
    List<RegistroDeAsistencia> lista = new ArrayList<>();
    try (BufferedReader br = new BufferedReader(new FileReader(CARPETA_DATOS + "/asistencias.csv"))) {
      String linea = br.readLine();
      while ((linea = br.readLine()) != null) {
        String[] p = linea.split(",");
        // id_asistencia,id_curso,id_estudiante,fecha,horas_asistidas,presente
        if (p.length >= 6 && p[2].equals(idEstudiante)) {
          lista.add(new RegistroDeAsistencia(p[0], p[1], p[2], p[3], Integer.parseInt(p[4]), esPresente(p[5])));
        }
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
    return lista;
  }

  public static List<RegistroDeAsistencia> leerTodasAsistencias() {
    List<RegistroDeAsistencia> lista = new ArrayList<>();
    try (BufferedReader br = new BufferedReader(new FileReader(CARPETA_DATOS + "/asistencias.csv"))) {
      String linea = br.readLine();
      while ((linea = br.readLine()) != null) {
        String[] p = linea.split(",");
        if (p.length >= 6) {
          lista.add(new RegistroDeAsistencia(p[0], p[1], p[2], p[3], Integer.parseInt(p[4]), esPresente(p[5])));
        }
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
    return lista;
  }

  /**
   * Determina si un valor de la columna 'presente' corresponde a asistencia.
   * Acepta los valores 1/0 (enteros) y true/false (texto).
   */
  private static boolean esPresente(String valor) {
    return "1".equals(valor) || "true".equalsIgnoreCase(valor);
  }
}
