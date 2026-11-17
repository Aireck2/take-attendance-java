package almacenamiento.csv;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import models.BloqueSeccion;
import models.Carrera;
import models.Curso;
import models.EstadoMatricula;
import models.Estudiante;
import models.HorarioSeccion;
import models.Matricula;
import models.ModuloProfesional;
import models.PeriodoAcademico;
import models.PlanEstudios;
import models.Profesor;
import models.RegistroDeAsistencia;
import models.Salon;
import models.SeccionCurso;
import models.SesionClase;
import repositorio.RepositorioAsistencia;

/** Implementación CSV del repositorio de asistencia. */
public final class RepositorioAsistenciaCsv implements RepositorioAsistencia {
  @Override
  public List<RegistroDeAsistencia> listarTodos() {
    return AlmacenamientoAsistenciaCsv.leerTodos();
  }

  @Override
  public List<RegistroDeAsistencia> listarPorEstudiante(String idEstudiante) {
    return AlmacenamientoAsistenciaCsv.leerTodos().stream()
        .filter(registro -> registro.getIdEstudiante().equals(idEstudiante)).toList();
  }

  @Override
  public void guardarLote(List<RegistroDeAsistencia> registros) {
    PersistenciaAsistenciaCsv.guardar(registros);
  }

  @Override
  public Optional<SesionClase> buscarSesion(String idSesion) {
    if (idSesion == null) return Optional.empty();
    return AlmacenamientoProgramacionCsv.leerSesiones().stream()
        .filter(item -> item.getIdSesion().equals(idSesion)).findFirst();
  }

  @Override
  public Optional<SeccionCurso> buscarSeccionDeSesion(SesionClase sesion) {
    if (sesion == null) return Optional.empty();
    Optional<HorarioSeccion> horario = AlmacenamientoProgramacionCsv.leerHorarios().stream()
        .filter(item -> item.getIdHorario().equals(sesion.getIdHorario())).findFirst();
    if (horario.isEmpty()) return Optional.empty();
    return AlmacenamientoProgramacionCsv.leerSecciones().stream()
        .filter(item -> item.getIdSeccion().equals(horario.get().getIdSeccion())).findFirst();
  }

  @Override
  public Optional<Curso> buscarCurso(String idCurso) {
    if (idCurso == null) return Optional.empty();
    return AlmacenamientoCurriculoCsv.leerCursos().stream().filter(item -> item.getIdCurso().equals(idCurso)).findFirst();
  }

  @Override
  public Optional<Estudiante> buscarEstudiante(String idEstudiante) {
    if (idEstudiante == null) return Optional.empty();
    return AlmacenamientoEstudianteCsv.leerTodos().stream().filter(item -> item.getId().equals(idEstudiante)).findFirst();
  }

  @Override
  public Optional<Matricula> buscarMatriculaActiva(String idEstudiante, String idBloque) {
    if (idEstudiante == null || idBloque == null) return Optional.empty();
    return AlmacenamientoProgramacionCsv.leerMatriculas().stream().filter(item -> item.getIdEstudiante().equals(idEstudiante)
        && item.getIdBloque().equals(idBloque) && item.getEstado() == EstadoMatricula.ACTIVA).findFirst();
  }

  @Override
  public List<Estudiante> listarEstudiantesDeSesion(String idSesion) {
    List<Estudiante> resultado = new ArrayList<>();
    SesionClase sesion = buscarSesion(idSesion).orElse(null);
    SeccionCurso seccion = buscarSeccionDeSesion(sesion).orElse(null);
    if (seccion == null) return resultado;
    for (Matricula matricula : AlmacenamientoProgramacionCsv.leerMatriculas()) {
      if (matricula.getEstado() != EstadoMatricula.ACTIVA
          || !matricula.getIdBloque().equals(seccion.getIdBloque())) continue;
      buscarEstudiante(matricula.getIdEstudiante()).ifPresent(resultado::add);
    }
    return resultado;
  }

  @Override
  public List<SesionClase> listarSesionesDeCursoEnFecha(Curso curso, String fecha) {
    List<SesionClase> resultado = new ArrayList<>();
    if (curso == null || fecha == null) return resultado;
    List<HorarioSeccion> horarios = AlmacenamientoProgramacionCsv.leerHorarios();
    List<SeccionCurso> secciones = AlmacenamientoProgramacionCsv.leerSecciones();
    for (SesionClase sesion : AlmacenamientoProgramacionCsv.leerSesiones()) {
      if (!fecha.equals(sesion.getFecha())) continue;
      HorarioSeccion horario = horarios.stream()
          .filter(item -> item.getIdHorario().equals(sesion.getIdHorario())).findFirst().orElse(null);
      if (horario == null) continue;
      boolean pertenece = secciones.stream().anyMatch(item -> item.getIdSeccion().equals(horario.getIdSeccion())
          && item.getIdCurso().equals(curso.getIdCurso()));
      if (pertenece) resultado.add(sesion);
    }
    return resultado;
  }

  @Override
  public List<Curso> listarCursosDeProfesor(Profesor profesor) {
    List<Curso> resultado = new ArrayList<>();
    if (profesor == null) return resultado;
    List<SeccionCurso> secciones = AlmacenamientoProgramacionCsv.leerSecciones();
    List<Curso> cursos = AlmacenamientoCurriculoCsv.leerCursos();
    for (HorarioSeccion horario : AlmacenamientoProgramacionCsv.leerHorarios()) {
      if (!profesor.getId().equals(horario.getIdProfesor())) continue;
      SeccionCurso seccion = secciones.stream()
          .filter(item -> item.getIdSeccion().equals(horario.getIdSeccion())).findFirst().orElse(null);
      if (seccion == null) continue;
      cursos.stream().filter(item -> item.getIdCurso().equals(seccion.getIdCurso())).findFirst()
          .ifPresent(curso -> {
            if (resultado.stream().noneMatch(item -> item.getIdCurso().equals(curso.getIdCurso()))) {
              resultado.add(curso);
            }
          });
    }
    return resultado;
  }

  @Override
  public List<SeccionCurso> listarSeccionesDeProfesor(Profesor profesor, Curso curso) {
    List<SeccionCurso> resultado = new ArrayList<>();
    if (profesor == null || curso == null) return resultado;
    List<HorarioSeccion> horarios = AlmacenamientoProgramacionCsv.leerHorarios();
    for (SeccionCurso seccion : AlmacenamientoProgramacionCsv.leerSecciones()) {
      if (!curso.getIdCurso().equals(seccion.getIdCurso())) continue;
      if (horarios.stream().anyMatch(item -> seccion.getIdSeccion().equals(item.getIdSeccion())
          && profesor.getId().equals(item.getIdProfesor()))) resultado.add(seccion);
    }
    return resultado;
  }

  @Override
  public Optional<BloqueSeccion> buscarBloque(String idBloque) {
    if (idBloque == null) return Optional.empty();
    return AlmacenamientoProgramacionCsv.leerBloques().stream()
        .filter(item -> item.getIdBloque().equals(idBloque)).findFirst();
  }

  @Override
  public List<HorarioSeccion> listarHorariosDeSeccion(String idSeccion) {
    List<HorarioSeccion> resultado = new ArrayList<>();
    for (HorarioSeccion item : AlmacenamientoProgramacionCsv.leerHorarios()) {
      if (item.getIdSeccion().equals(idSeccion)) resultado.add(item);
    }
    return resultado;
  }

  @Override
  public Optional<Salon> buscarSalon(String idSalon) {
    if (idSalon == null) return Optional.empty();
    return AlmacenamientoProgramacionCsv.leerSalones().stream()
        .filter(item -> item.getIdSalon().equals(idSalon)).findFirst();
  }

  @Override
  public List<SesionClase> listarTodasLasSesiones() {
    return AlmacenamientoProgramacionCsv.leerSesiones();
  }

  @Override
  public List<Estudiante> listarEstudiantesDeCursoEnBloque(Curso curso, String idBloque) {
    List<Estudiante> resultado = new ArrayList<>();
    if (curso == null || idBloque == null) return resultado;
    boolean cursoEnBloque = AlmacenamientoProgramacionCsv.leerSecciones().stream()
        .anyMatch(item -> item.getIdCurso().equals(curso.getIdCurso()) && item.getIdBloque().equals(idBloque));
    if (!cursoEnBloque) return resultado;
    for (Matricula matricula : AlmacenamientoProgramacionCsv.leerMatriculas()) {
      if (matricula.getEstado() == EstadoMatricula.ACTIVA && matricula.getIdBloque().equals(idBloque)) {
        buscarEstudiante(matricula.getIdEstudiante()).ifPresent(resultado::add);
      }
    }
    return resultado;
  }

  @Override
  public List<Profesor> listarProfesoresDeSeccion(String idSeccion) {
    List<Profesor> resultado = new ArrayList<>();
    for (HorarioSeccion horario : listarHorariosDeSeccion(idSeccion)) {
      AlmacenamientoProfesorCsv.leerTodos().stream()
          .filter(item -> item.getId().equals(horario.getIdProfesor())).findFirst()
          .ifPresent(profesor -> {
            if (resultado.stream().noneMatch(item -> item.getId().equals(profesor.getId()))) resultado.add(profesor);
          });
    }
    return resultado;
  }

  @Override
  public Optional<Carrera> buscarCarreraDeCurso(Curso curso) {
    if (curso == null || curso.getIdModulo() == null) return Optional.empty();
    Optional<ModuloProfesional> modulo = AlmacenamientoCurriculoCsv.leerModulosProfesionales().stream()
        .filter(item -> item.getIdModulo().equals(curso.getIdModulo())).findFirst();
    if (modulo.isEmpty()) return Optional.empty();
    Optional<PlanEstudios> plan = AlmacenamientoCurriculoCsv.leerPlanesEstudios().stream()
        .filter(item -> item.getIdPlan().equals(modulo.get().getIdPlan())).findFirst();
    if (plan.isEmpty()) return Optional.empty();
    return AlmacenamientoCurriculoCsv.leerCarreras().stream()
        .filter(item -> item.getIdCarrera().equals(plan.get().getIdCarrera())).findFirst();
  }

  @Override
  public List<PeriodoAcademico> listarPeriodosAcademicos() {
    return AlmacenamientoCurriculoCsv.leerPeriodosAcademicos();
  }

  @Override
  public Optional<Profesor> buscarProfesor(String idProfesor) {
    if (idProfesor == null) return Optional.empty();
    return AlmacenamientoProfesorCsv.leerTodos().stream().filter(item -> item.getId().equals(idProfesor)).findFirst();
  }

  @Override
  public List<Matricula> listarMatriculasDeEstudiante(String idEstudiante) {
    List<Matricula> resultado = new ArrayList<>();
    for (Matricula item : AlmacenamientoProgramacionCsv.leerMatriculas()) {
      if (item.getIdEstudiante().equals(idEstudiante)) resultado.add(item);
    }
    return resultado;
  }

  @Override
  public List<SeccionCurso> listarSeccionesDeBloque(String idBloque) {
    List<SeccionCurso> resultado = new ArrayList<>();
    for (SeccionCurso item : AlmacenamientoProgramacionCsv.leerSecciones()) {
      if (item.getIdBloque().equals(idBloque)) resultado.add(item);
    }
    return resultado;
  }
}
