package service;

import java.util.ArrayList;
import java.util.List;

import models.BloqueSeccion;
import models.Carrera;
import models.Curso;
import models.Estudiante;
import models.HorarioSeccion;
import models.ModuloProfesional;
import models.PeriodoAcademico;
import models.PlanEstudios;
import models.Profesor;
import models.RegistroDeAsistencia;
import models.Salon;
import models.SeccionCurso;
import models.SesionClase;
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
   * Busca un profesor por su identificador. Devuelve null si no existe.
   */
  public Profesor buscarProfesor(String idProfesor) {
    if (idProfesor == null) {
      return null;
    }
    for (Profesor profesor : ArchivoManager.leerProfesores()) {
      if (profesor.getId().equals(idProfesor)) {
        return profesor;
      }
    }
    return null;
  }

  /**
   * Busca un estudiante por su identificador. Devuelve null si no existe.
   */
  public Estudiante buscarEstudiante(String idEstudiante) {
    if (idEstudiante == null) {
      return null;
    }
    for (Estudiante estudiante : ArchivoManager.leerEstudiantes()) {
      if (estudiante.getId().equals(idEstudiante)) {
        return estudiante;
      }
    }
    return null;
  }

  /**
   * Lista todos los cursos registrados en el sistema.
   */
  public List<Curso> listarCursos() {
    return ArchivoManager.leerCursos();
  }

  /**
   * Lista las carreras / programas de estudio registrados.
   */
  public List<Carrera> listarCarreras() {
    return ArchivoManager.leerCarreras();
  }

  /**
   * Lista los planes de estudio registrados.
   */
  public List<PlanEstudios> listarPlanesEstudios() {
    return ArchivoManager.leerPlanesEstudios();
  }

  /**
   * Lista los modulos profesionales registrados.
   */
  public List<ModuloProfesional> listarModulosProfesionales() {
    return ArchivoManager.leerModulosProfesionales();
  }

  /**
   * Lista los periodos academicos registrados.
   */
  public List<PeriodoAcademico> listarPeriodosAcademicos() {
    return ArchivoManager.leerPeriodosAcademicos();
  }

  /**
   * Lista los bloques de seccion / turno registrados.
   */
  public List<BloqueSeccion> listarBloques() {
    return ArchivoManager.leerBloques();
  }

  /**
   * Lista las secciones de curso registradas.
   */
  public List<SeccionCurso> listarSecciones() {
    return ArchivoManager.leerSecciones();
  }

  /**
   * Devuelve las secciones que pertenecen a un bloque.
   */
  public List<SeccionCurso> listarSeccionesBloque(String idBloque) {
    List<SeccionCurso> secciones = new ArrayList<>();
    for (SeccionCurso seccion : ArchivoManager.leerSecciones()) {
      if (seccion.getIdBloque().equals(idBloque)) {
        secciones.add(seccion);
      }
    }
    return secciones;
  }

  /**
   * Lista las franjas horarias registradas.
   */
  public List<HorarioSeccion> listarHorarios() {
    return ArchivoManager.leerHorarios();
  }

  /**
   * Devuelve las franjas horarias de una seccion.
   */
  public List<HorarioSeccion> listarHorariosSeccion(String idSeccion) {
    List<HorarioSeccion> horarios = new ArrayList<>();
    for (HorarioSeccion horario : ArchivoManager.leerHorarios()) {
      if (horario.getIdSeccion().equals(idSeccion)) {
        horarios.add(horario);
      }
    }
    return horarios;
  }

  /**
   * Lista las sesiones de clase registradas.
   */
  public List<SesionClase> listarSesiones() {
    return ArchivoManager.leerSesiones();
  }

  /**
   * Devuelve las sesiones generadas por una franja horaria.
   */
  public List<SesionClase> listarSesionesHorario(String idHorario) {
    List<SesionClase> sesiones = new ArrayList<>();
    for (SesionClase sesion : ArchivoManager.leerSesiones()) {
      if (sesion.getIdHorario().equals(idHorario)) {
        sesiones.add(sesion);
      }
    }
    return sesiones;
  }

  /**
   * Lista los salones, laboratorios y talleres registrados.
   */
  public List<Salon> listarSalones() {
    return ArchivoManager.leerSalones();
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
   * Crea un registro de asistencia a partir de las horas asistidas.
   * Las horas se limitan entre 0 y el maximo de la sesion; 0 significa ausente.
   * El identificador se asigna al guardar el lote.
   */
  public RegistroDeAsistencia crearRegistro(Curso curso, Estudiante estudiante, String fecha, int horasAsistidas) {
    int horasValidas = Math.max(0, Math.min(horasAsistidas, curso.getTotalHorasSesion()));
    boolean presente = horasValidas > 0;
    return new RegistroDeAsistencia(null, curso.getIdCurso(), estudiante.getId(), fecha, horasValidas, presente);
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
