package storage;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import models.BloqueSeccion;
import models.Carrera;
import models.Curso;
import models.DiaSemana;
import models.Estudiante;
import models.EstadoEstudiante;
import models.EstadoPeriodo;
import models.EstadoPlan;
import models.HorarioSeccion;
import models.ModalidadDictado;
import models.ModuloProfesional;
import models.PeriodoAcademico;
import models.PlanEstudios;
import models.Profesor;
import models.RegistroDeAsistencia;
import models.Rol;
import models.Salon;
import models.SeccionCurso;
import models.SesionClase;
import models.TipoClase;
import models.TipoSalon;
import models.Turno;
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

  public static List<Carrera> leerCarreras() {
    List<Carrera> lista = new ArrayList<>();
    try (BufferedReader br = new BufferedReader(new FileReader(CARPETA_DATOS + "/carreras.csv"))) {
      String linea = br.readLine(); // Saltar cabecera
      while ((linea = br.readLine()) != null) {
        if (linea.isBlank()) {
          continue;
        }
        String[] p = linea.split(",", -1);
        // id_carrera,nombre
        if (p.length >= 2) {
          lista.add(new Carrera(p[0].trim(), p[1].trim()));
        }
      }
    } catch (FileNotFoundException e) {
      System.out.println("No se encontro " + CARPETA_DATOS + "/carreras.csv");
    } catch (IOException e) {
      e.printStackTrace();
    }
    return lista;
  }

  public static List<PlanEstudios> leerPlanesEstudios() {
    List<PlanEstudios> lista = new ArrayList<>();
    try (BufferedReader br = new BufferedReader(new FileReader(CARPETA_DATOS + "/planes_estudios.csv"))) {
      String linea = br.readLine(); // Saltar cabecera
      while ((linea = br.readLine()) != null) {
        if (linea.isBlank()) {
          continue;
        }
        String[] p = linea.split(",", -1);
        // id_plan,id_carrera,codigo,nombre,creditos_totales,horas_totales,estado
        if (p.length >= 7) {
          lista.add(new PlanEstudios(p[0].trim(), p[1].trim(), p[2].trim(), p[3].trim(),
              Integer.parseInt(p[4].trim()), Integer.parseInt(p[5].trim()), EstadoPlan.valueOf(p[6].trim())));
        }
      }
    } catch (FileNotFoundException e) {
      System.out.println("No se encontro " + CARPETA_DATOS + "/planes_estudios.csv");
    } catch (IOException e) {
      e.printStackTrace();
    }
    return lista;
  }

  public static List<ModuloProfesional> leerModulosProfesionales() {
    List<ModuloProfesional> lista = new ArrayList<>();
    try (BufferedReader br = new BufferedReader(new FileReader(CARPETA_DATOS + "/modulos.csv"))) {
      String linea = br.readLine(); // Saltar cabecera
      while ((linea = br.readLine()) != null) {
        if (linea.isBlank()) {
          continue;
        }
        String[] p = linea.split(",", -1);
        // id_modulo,id_plan,nombre,competencia,creditos_modulo
        if (p.length >= 5) {
          lista.add(new ModuloProfesional(p[0].trim(), p[1].trim(), p[2].trim(), p[3].trim(),
              Integer.parseInt(p[4].trim())));
        }
      }
    } catch (FileNotFoundException e) {
      System.out.println("No se encontro " + CARPETA_DATOS + "/modulos.csv");
    } catch (IOException e) {
      e.printStackTrace();
    }
    return lista;
  }

  public static List<PeriodoAcademico> leerPeriodosAcademicos() {
    List<PeriodoAcademico> lista = new ArrayList<>();
    try (BufferedReader br = new BufferedReader(new FileReader(CARPETA_DATOS + "/periodos.csv"))) {
      String linea = br.readLine(); // Saltar cabecera
      while ((linea = br.readLine()) != null) {
        if (linea.isBlank()) {
          continue;
        }
        String[] p = linea.split(",", -1);
        // id_periodo,id_plan,codigo,fecha_inicio,fecha_fin,estado
        if (p.length >= 6) {
          lista.add(new PeriodoAcademico(p[0].trim(), p[1].trim(), p[2].trim(), p[3].trim(), p[4].trim(),
              EstadoPeriodo.valueOf(p[5].trim())));
        }
      }
    } catch (FileNotFoundException e) {
      System.out.println("No se encontro " + CARPETA_DATOS + "/periodos.csv");
    } catch (IOException e) {
      e.printStackTrace();
    }
    return lista;
  }

  public static List<BloqueSeccion> leerBloques() {
    List<BloqueSeccion> lista = new ArrayList<>();
    try (BufferedReader br = new BufferedReader(new FileReader(CARPETA_DATOS + "/bloques.csv"))) {
      String linea = br.readLine(); // Saltar cabecera
      while ((linea = br.readLine()) != null) {
        if (linea.isBlank()) {
          continue;
        }
        String[] p = linea.split(",", -1);
        // id_bloque,id_periodo,nombre,turno
        if (p.length >= 4) {
          lista.add(new BloqueSeccion(p[0].trim(), p[1].trim(), p[2].trim(), Turno.valueOf(p[3].trim())));
        }
      }
    } catch (FileNotFoundException e) {
      System.out.println("No se encontro " + CARPETA_DATOS + "/bloques.csv");
    } catch (IOException e) {
      e.printStackTrace();
    }
    return lista;
  }

  public static List<SeccionCurso> leerSecciones() {
    List<SeccionCurso> lista = new ArrayList<>();
    try (BufferedReader br = new BufferedReader(new FileReader(CARPETA_DATOS + "/secciones.csv"))) {
      String linea = br.readLine(); // Saltar cabecera
      while ((linea = br.readLine()) != null) {
        if (linea.isBlank()) {
          continue;
        }
        String[] p = linea.split(",", -1);
        // id_seccion,id_bloque,id_curso,modalidad,id_profesor,cupo_maximo
        if (p.length >= 6) {
          lista.add(new SeccionCurso(p[0].trim(), p[1].trim(), p[2].trim(), ModalidadDictado.valueOf(p[3].trim()),
              p[4].trim(), Integer.parseInt(p[5].trim())));
        }
      }
    } catch (FileNotFoundException e) {
      System.out.println("No se encontro " + CARPETA_DATOS + "/secciones.csv");
    } catch (IOException e) {
      e.printStackTrace();
    }
    return lista;
  }

  public static List<HorarioSeccion> leerHorarios() {
    List<HorarioSeccion> lista = new ArrayList<>();
    try (BufferedReader br = new BufferedReader(new FileReader(CARPETA_DATOS + "/horarios.csv"))) {
      String linea = br.readLine(); // Saltar cabecera
      while ((linea = br.readLine()) != null) {
        if (linea.isBlank()) {
          continue;
        }
        String[] p = linea.split(",", -1);
        // id_horario,id_seccion,dia_semana,hora_inicio,hora_fin,tipo_clase,id_profesor,id_salon
        if (p.length >= 8) {
          lista.add(new HorarioSeccion(p[0].trim(), p[1].trim(), DiaSemana.valueOf(p[2].trim()), p[3].trim(),
              p[4].trim(), TipoClase.valueOf(p[5].trim()), p[6].trim(), p[7].trim()));
        }
      }
    } catch (FileNotFoundException e) {
      System.out.println("No se encontro " + CARPETA_DATOS + "/horarios.csv");
    } catch (IOException e) {
      e.printStackTrace();
    }
    return lista;
  }

  public static List<SesionClase> leerSesiones() {
    List<SesionClase> lista = new ArrayList<>();
    try (BufferedReader br = new BufferedReader(new FileReader(CARPETA_DATOS + "/sesiones.csv"))) {
      String linea = br.readLine(); // Saltar cabecera
      while ((linea = br.readLine()) != null) {
        if (linea.isBlank()) {
          continue;
        }
        String[] p = linea.split(",", -1);
        // id_sesion,id_horario,fecha,horas_programadas,id_profesor,id_salon
        if (p.length >= 6) {
          lista.add(new SesionClase(p[0].trim(), p[1].trim(), p[2].trim(), Integer.parseInt(p[3].trim()), p[4].trim(),
              p[5].trim()));
        }
      }
    } catch (FileNotFoundException e) {
      System.out.println("No se encontro " + CARPETA_DATOS + "/sesiones.csv");
    } catch (IOException e) {
      e.printStackTrace();
    }
    return lista;
  }

  public static List<Salon> leerSalones() {
    List<Salon> lista = new ArrayList<>();
    try (BufferedReader br = new BufferedReader(new FileReader(CARPETA_DATOS + "/salones.csv"))) {
      String linea = br.readLine(); // Saltar cabecera
      while ((linea = br.readLine()) != null) {
        if (linea.isBlank()) {
          continue;
        }
        String[] p = linea.split(",", -1);
        // id_salon,nombre,tipo,capacidad
        if (p.length >= 4) {
          lista.add(new Salon(p[0].trim(), p[1].trim(), TipoSalon.valueOf(p[2].trim()),
              Integer.parseInt(p[3].trim())));
        }
      }
    } catch (FileNotFoundException e) {
      System.out.println("No se encontro " + CARPETA_DATOS + "/salones.csv");
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
        if (linea.isBlank()) {
          continue;
        }
        String[] p = linea.split(",", -1);
        // id_curso,nombre_curso,total_horas_sesion,id_profesor,duracion_curso
        // [,id_modulo,creditos,horas_teoricas,horas_practicas]
        if (p.length >= 5) {
          String idCurso = p[0].trim();
          String nombreCurso = p[1].trim();
          int totalHorasSesion = Integer.parseInt(p[2].trim());
          String idProfesor = p[3].trim();
          int duracionCurso = Integer.parseInt(p[4].trim());
          if (p.length >= 9) {
            lista.add(new Curso(idCurso, nombreCurso, totalHorasSesion, idProfesor, duracionCurso,
                p[5].trim(), Integer.parseInt(p[6].trim()), Integer.parseInt(p[7].trim()),
                Integer.parseInt(p[8].trim())));
          } else {
            lista.add(new Curso(idCurso, nombreCurso, totalHorasSesion, idProfesor, duracionCurso));
          }
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
