package almacenamiento.csv;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import modelo.BloqueSeccion;
import modelo.Carrera;
import modelo.Curso;
import modelo.DiaSemana;
import modelo.EstadoMatricula;
import modelo.EstadoPeriodo;
import modelo.Estudiante;
import modelo.HorarioSeccion;
import modelo.Matricula;
import modelo.PeriodoAcademico;
import modelo.Profesor;
import modelo.RegistroDeAsistencia;
import modelo.Salon;
import modelo.SeccionCurso;
import modelo.SesionClase;
import repositorio.RepositorioAsistencia;

/** Implementación CSV del repositorio de asistencia. */
public final class RepositorioAsistenciaCsv implements RepositorioAsistencia {
  @Override
  public List<RegistroDeAsistencia> listarTodos() {
    return PersistenciaAsistenciaCsv.leerTodos();
  }

  @Override
  public List<RegistroDeAsistencia> listarPorEstudiante(String idEstudiante) {
    return PersistenciaAsistenciaCsv.leerTodos().stream()
        .filter(registro -> registro.getIdEstudiante().equals(idEstudiante)).toList();
  }

  @Override
  public void guardarLote(List<RegistroDeAsistencia> registros) {
    PersistenciaAsistenciaCsv.guardar(registros);
  }

  @Override
  public Optional<SesionClase> buscarSesion(String idSesion) {
    if (idSesion == null)
      return Optional.empty();
    return listarTodasLasSesiones().stream().filter(item -> item.getIdSesion().equals(idSesion)).findFirst();
  }

  @Override
  public Optional<SeccionCurso> buscarSeccionDeSesion(SesionClase sesion) {
    if (sesion == null)
      return Optional.empty();
    return leerHorarios().stream()
        .filter(item -> item.getIdHorario().equals(sesion.getIdHorario()))
        .findFirst()
        .flatMap(horario -> leerSecciones().stream()
            .filter(seccion -> seccion.getIdSeccion().equals(horario.getIdSeccion()))
            .findFirst());
  }

  @Override
  public Optional<Curso> buscarCurso(String idCurso) {
    if (idCurso == null)
      return Optional.empty();
    return leerCursos().stream().filter(item -> item.getIdCurso().equals(idCurso)).findFirst();
  }

  @Override
  public Optional<Estudiante> buscarEstudiante(String idEstudiante) {
    if (idEstudiante == null)
      return Optional.empty();
    return leerEstudiantes().stream().filter(item -> item.getId().equals(idEstudiante)).findFirst();
  }

  @Override
  public Optional<Matricula> buscarMatriculaActiva(String idEstudiante, String idBloque) {
    if (idEstudiante == null || idBloque == null)
      return Optional.empty();
    return leerMatriculas().stream()
        .filter(item -> item.getIdEstudiante().equals(idEstudiante)
            && item.getIdBloque().equals(idBloque) && item.getEstado() == EstadoMatricula.ACTIVA)
        .findFirst();
  }

  @Override
  public List<Estudiante> listarEstudiantesDeSesion(String idSesion) {
    SeccionCurso seccion = buscarSesion(idSesion).flatMap(this::buscarSeccionDeSesion).orElse(null);
    return seccion == null ? List.of() : listarEstudiantesActivosEnBloque(seccion.getIdBloque());
  }

  @Override
  public List<SesionClase> listarSesionesDeCursoEnFecha(Curso curso, String fecha) {
    if (curso == null || fecha == null)
      return List.of();
    List<HorarioSeccion> horarios = leerHorarios();
    List<SeccionCurso> secciones = leerSecciones();
    return listarTodasLasSesiones().stream()
        .filter(sesion -> fecha.equals(sesion.getFecha()))
        .filter(sesion -> horarios.stream()
            .filter(h -> h.getIdHorario().equals(sesion.getIdHorario()))
            .anyMatch(h -> secciones.stream()
                .anyMatch(s -> s.getIdSeccion().equals(h.getIdSeccion()) && s.getIdCurso().equals(curso.getIdCurso()))))
        .toList();
  }

  @Override
  public List<Curso> listarCursosDeProfesor(Profesor profesor) {
    if (profesor == null)
      return List.of();
    List<SeccionCurso> secciones = leerSecciones();
    List<Curso> cursos = leerCursos();
    List<Curso> resultado = new ArrayList<>();
    for (HorarioSeccion horario : leerHorarios()) {
      if (!profesor.getId().equals(horario.getIdProfesor()))
        continue;
      secciones.stream()
          .filter(s -> s.getIdSeccion().equals(horario.getIdSeccion()))
          .findFirst()
          .flatMap(s -> cursos.stream().filter(c -> c.getIdCurso().equals(s.getIdCurso())).findFirst())
          .ifPresent(curso -> {
            if (resultado.stream().noneMatch(c -> c.getIdCurso().equals(curso.getIdCurso())))
              resultado.add(curso);
          });
    }
    return resultado;
  }

  @Override
  public List<SeccionCurso> listarSeccionesDeProfesor(Profesor profesor, Curso curso) {
    if (profesor == null || curso == null)
      return List.of();
    List<HorarioSeccion> horarios = leerHorarios();
    return leerSecciones().stream()
        .filter(s -> curso.getIdCurso().equals(s.getIdCurso()))
        .filter(s -> horarios.stream()
            .anyMatch(h -> s.getIdSeccion().equals(h.getIdSeccion()) && profesor.getId().equals(h.getIdProfesor())))
        .toList();
  }

  @Override
  public Optional<BloqueSeccion> buscarBloque(String idBloque) {
    if (idBloque == null)
      return Optional.empty();
    return LectorCsv.leer("data/bloque_seccion.csv", c -> c.length >= 3
        ? new BloqueSeccion(c[0], c[1], c[2]) : null).stream()
        .filter(item -> item.getIdBloque().equals(idBloque)).findFirst();
  }

  @Override
  public List<HorarioSeccion> listarHorariosDeSeccion(String idSeccion) {
    return leerHorarios().stream().filter(item -> item.getIdSeccion().equals(idSeccion)).toList();
  }

  @Override
  public Optional<Salon> buscarSalon(String idSalon) {
    if (idSalon == null)
      return Optional.empty();
    return LectorCsv.leer("data/salon.csv", c -> c.length >= 2
        ? new Salon(c[0], c[1]) : null).stream()
        .filter(item -> item.getIdSalon().equals(idSalon)).findFirst();
  }

  @Override
  public List<SesionClase> listarTodasLasSesiones() {
    return LectorCsv.leer("data/sesion_clase.csv", c -> c.length >= 6
        ? new SesionClase(c[0], c[1], c[2], Integer.parseInt(c[3]), c[4], c[5]) : null);
  }

  @Override
  public List<Estudiante> listarEstudiantesDeCursoEnBloque(Curso curso, String idBloque) {
    if (curso == null || idBloque == null)
      return List.of();
    boolean cursoEnBloque = leerSecciones().stream()
        .anyMatch(s -> s.getIdCurso().equals(curso.getIdCurso()) && s.getIdBloque().equals(idBloque));
    return cursoEnBloque ? listarEstudiantesActivosEnBloque(idBloque) : List.of();
  }

  @Override
  public List<Profesor> listarProfesoresDeSeccion(String idSeccion) {
    List<Profesor> profesores = leerProfesores();
    List<Profesor> resultado = new ArrayList<>();
    for (HorarioSeccion horario : listarHorariosDeSeccion(idSeccion)) {
      profesores.stream()
          .filter(p -> p.getId().equals(horario.getIdProfesor()))
          .findFirst()
          .ifPresent(p -> {
            if (resultado.stream().noneMatch(existente -> existente.getId().equals(p.getId())))
              resultado.add(p);
          });
    }
    return resultado;
  }

  @Override
  public Optional<Carrera> buscarCarreraDeCurso(Curso curso) {
    if (curso == null || curso.getIdModulo() == null)
      return Optional.empty();
    return buscarValorPorClave("data/modulo_profesional.csv", curso.getIdModulo())
        .flatMap(idPlan -> buscarValorPorClave("data/plan_estudios.csv", idPlan))
        .flatMap(idCarrera -> LectorCsv.leer("data/carrera.csv", c -> c.length >= 2
            ? new Carrera(c[0], c[1]) : null).stream()
            .filter(carrera -> carrera.getIdCarrera().equals(idCarrera))
            .findFirst());
  }

  @Override
  public List<PeriodoAcademico> listarPeriodosAcademicos() {
    return LectorCsv.leer("data/periodo_academico.csv", c -> {
      if (c.length < 6)
        return null;
      int semanas = c.length >= 7 && !c[6].isEmpty()
          ? Integer.parseInt(c[6])
          : PeriodoAcademico.DEFAULT_SEMANAS_LECTIVAS;
      return new PeriodoAcademico(c[0], c[1], c[2], c[3], c[4], EstadoPeriodo.valueOf(c[5]), semanas);
    });
  }

  @Override
  public Optional<Profesor> buscarProfesor(String idProfesor) {
    if (idProfesor == null)
      return Optional.empty();
    return leerProfesores().stream().filter(p -> p.getId().equals(idProfesor)).findFirst();
  }

  @Override
  public List<Matricula> listarMatriculasDeEstudiante(String idEstudiante) {
    return leerMatriculas().stream().filter(m -> m.getIdEstudiante().equals(idEstudiante)).toList();
  }

  @Override
  public List<SeccionCurso> listarSeccionesDeBloque(String idBloque) {
    return leerSecciones().stream().filter(s -> s.getIdBloque().equals(idBloque)).toList();
  }

  private static Optional<String> buscarValorPorClave(String ruta, String clave) {
    return LectorCsv.leer(ruta, c -> c.length >= 2 && c[0].equals(clave) ? c[1] : null)
        .stream().findFirst();
  }

  private List<Estudiante> listarEstudiantesActivosEnBloque(String idBloque) {
    List<Estudiante> estudiantes = leerEstudiantes();
    List<Estudiante> resultado = new ArrayList<>();
    for (Matricula matricula : leerMatriculas()) {
      if (matricula.getEstado() == EstadoMatricula.ACTIVA && matricula.getIdBloque().equals(idBloque)) {
        estudiantes.stream().filter(e -> e.getId().equals(matricula.getIdEstudiante())).findFirst()
            .ifPresent(resultado::add);
      }
    }
    return resultado;
  }

  private static List<Estudiante> leerEstudiantes() {
    return LectorCsv.leer("data/estudiante.csv", c -> c.length >= 7
        ? new Estudiante(c[0], c[1], c[2], c[3], c[4], c[5], c[6]) : null);
  }

  private static List<Profesor> leerProfesores() {
    return LectorCsv.leer("data/profesor.csv", c -> c.length >= 9
        ? new Profesor(c[0], c[1], c[2], c[3], c[4], c[5], Integer.parseInt(c[6]), c[7], c[8]) : null);
  }

  private static List<HorarioSeccion> leerHorarios() {
    return LectorCsv.leer("data/horario_seccion.csv", c -> c.length >= 8
        ? new HorarioSeccion(c[0], c[1], DiaSemana.valueOf(c[2]), c[3], c[4], c[6], c[7])
        : null);
  }

  private static List<SeccionCurso> leerSecciones() {
    return LectorCsv.leer("data/seccion_curso.csv", c -> c.length >= 3
        ? new SeccionCurso(c[0], c[1], c[2]) : null);
  }

  private static List<Matricula> leerMatriculas() {
    return LectorCsv.leer("data/matricula.csv", c -> c.length >= 5
        ? new Matricula(c[0], c[1], c[2], c[3], EstadoMatricula.valueOf(c[4])) : null);
  }

  private static List<Curso> leerCursos() {
    return LectorCsv.leerConCabecera("data/curso.csv", (cabecera, c) -> {
      if (cabecera.contains("total_horas_lectivas") && c.length >= 7)
        return new Curso(c[0], c[1], c[2], Integer.parseInt(c[3]), Integer.parseInt(c[4]),
            Integer.parseInt(c[5]), Integer.parseInt(c[6]));
      if (c.length >= 8)
        return new Curso(c[0], c[1], c[2], Integer.parseInt(c[3]), Integer.parseInt(c[4]),
            Integer.parseInt(c[5]), Integer.parseInt(c[6]), Integer.parseInt(c[7]));
      if (c.length >= 7)
        return new Curso(c[0], c[1], c[2], Integer.parseInt(c[3]), Integer.parseInt(c[4]),
            Integer.parseInt(c[5]), Integer.parseInt(c[4]) + Integer.parseInt(c[5]));
      if (c.length >= 5)
        return new Curso(c[0], c[1], Integer.parseInt(c[2]), Integer.parseInt(c[4]));
      return null;
    });
  }
}
