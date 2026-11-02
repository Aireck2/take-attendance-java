package service;

import java.util.ArrayList;
import java.util.List;

import models.Curso;
import models.Estudiante;
import models.Profesor;
import models.RegistroDeAsistencia;
import storage.ArchivoManager;

/**
 * Contiene la logica de negocio del sistema de asistencia:
 * consultas entre entidades, calculos de porcentajes y registro de asistencias.
 */
public class ServicioAcademico {

  /**
   * Limite de inasistencia (porcentaje de faltas) antes de generar alerta.
   */
  public static final double LIMITE_INASISTENCIA = 15.0;

  /**
   * Lista todos los profesores registrados en el sistema.
   */
  public List<Profesor> listarProfesores() {
    return ArchivoManager.leerProfesores();
  }

  /**
   * Lista todos los estudiantes registrados en el sistema.
   */
  public List<Estudiante> listarEstudiantes() {
    return ArchivoManager.leerEstudiantes();
  }

  /**
   * Lista todos los cursos registrados en el sistema.
   */
  public List<Curso> listarCursos() {
    return ArchivoManager.leerCursos();
  }

  /**
   * Devuelve los cursos asignados a un profesor.
   */
  public List<Curso> listarCursosProfesor(Profesor profesor) {
    List<Curso> cursos = new ArrayList<>();
    for (Curso curso : ArchivoManager.leerCursos()) {
      if (curso.getIdProfesor().equals(profesor.getId())) {
        cursos.add(curso);
      }
    }
    return cursos;
  }

  /**
   * Devuelve los estudiantes inscritos en un curso.
   */
  public List<Estudiante> listarEstudiantesCurso(Curso curso) {
    List<Estudiante> alumnos = new ArrayList<>();
    for (String idEstudiante : ArchivoManager.leerInscripcionesCurso(curso.getIdCurso())) {
      for (Estudiante estudiante : ArchivoManager.leerEstudiantes()) {
        if (estudiante.getId().equals(idEstudiante)) {
          alumnos.add(estudiante);
        }
      }
    }
    return alumnos;
  }

  /**
   * Devuelve los cursos en los que esta inscrito un estudiante.
   */
  public List<Curso> listarCursosEstudiante(Estudiante estudiante) {
    List<Curso> cursos = new ArrayList<>();
    for (Curso curso : ArchivoManager.leerCursos()) {
      if (ArchivoManager.leerInscripcionesCurso(curso.getIdCurso()).contains(estudiante.getId())) {
        cursos.add(curso);
      }
    }
    return cursos;
  }

  /**
   * Devuelve el historial de asistencias de un estudiante.
   */
  public List<RegistroDeAsistencia> historialEstudiante(Estudiante estudiante) {
    return ArchivoManager.leerAsistenciasEstudiante(estudiante.getId());
  }

  /**
   * Guarda el lote de registros de asistencia en el archivo CSV.
   * Asigna un identificador consecutivo a cada registro del lote antes de guardar.
   */
  public void guardarAsistencias(List<RegistroDeAsistencia> registros) {
    int maxId = 0;
    for (RegistroDeAsistencia reg : ArchivoManager.leerTodasAsistencias()) {
      try {
        maxId = Math.max(maxId, Integer.parseInt(reg.getIdAsistencia().replace("A", "")));
      } catch (NumberFormatException e) {
        // Ignorar identificadores con formato distinto
      }
    }
    int siguienteId = maxId + 1;
    for (RegistroDeAsistencia reg : registros) {
      reg.setIdAsistencia("A" + siguienteId);
      siguienteId++;
    }
    ArchivoManager.guardarAsistencia(registros);
  }

  /**
   * Crea un registro de asistencia. El identificador se asigna al guardar el lote.
   */
  public RegistroDeAsistencia crearRegistro(Curso curso, Estudiante estudiante, String fecha, int horasAsistidas,
      boolean presente) {
    return new RegistroDeAsistencia(null, curso.getIdCurso(), estudiante.getId(), fecha, horasAsistidas, presente);
  }

  /**
   * Calcula el porcentaje de inasistencia global con base en el total del curso:
   * (horas dictadas hasta la fecha - horas asistidas) / (horas totales del curso) * 100.
   */
  public double calcularPorcentajeFaltas(int horasDictadas, int horasAsistidas, int totalHorasCurso) {
    if (totalHorasCurso <= 0) {
      return 0.0;
    }
    int horasNoAsistidas = horasDictadas - horasAsistidas;
    if (horasNoAsistidas < 0) {
      horasNoAsistidas = 0;
    }
    return ((double) horasNoAsistidas / totalHorasCurso) * 100;
  }
}
