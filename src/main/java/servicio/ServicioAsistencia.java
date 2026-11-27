package servicio;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import modelo.BloqueSeccion;
import modelo.Carrera;
import modelo.Curso;
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

/** Coordina las consultas academicas, el registro y la persistencia de asistencia. */
public final class ServicioAsistencia {
  private final RepositorioAsistencia repositorio;

  public ServicioAsistencia(RepositorioAsistencia repositorio) {
    this.repositorio = repositorio;
  }

  public List<RegistroDeAsistencia> listarPorEstudiante(String idEstudiante) {
    return repositorio.listarTodos().stream()
        .filter(registro -> registro.getIdEstudiante().equals(idEstudiante)).toList();
  }

  public void guardarLote(List<RegistroDeAsistencia> registros) {
    repositorio.guardarLote(registros);
  }

  public Profesor buscarProfesor(String idProfesor) {
    return repositorio.buscarProfesor(idProfesor).orElse(null);
  }

  public Estudiante buscarEstudiante(String idEstudiante) {
    if (idEstudiante == null)
      return null;
    return repositorio.listarEstudiantes().stream()
        .filter(e -> e.getId().equals(idEstudiante)).findFirst().orElse(null);
  }

  public Curso buscarCurso(String idCurso) {
    if (idCurso == null)
      return null;
    return repositorio.listarCursos().stream()
        .filter(c -> c.getIdCurso().equals(idCurso)).findFirst().orElse(null);
  }

  public Carrera buscarCarreraDeCurso(Curso curso) {
    return repositorio.buscarCarreraDeCurso(curso).orElse(null);
  }

  public BloqueSeccion buscarBloque(String idBloque) {
    if (idBloque == null)
      return null;
    return repositorio.listarBloques().stream()
        .filter(b -> b.getIdBloque().equals(idBloque)).findFirst().orElse(null);
  }

  public Salon buscarSalon(String idSalon) {
    if (idSalon == null)
      return null;
    return repositorio.listarSalones().stream()
        .filter(s -> s.getIdSalon().equals(idSalon)).findFirst().orElse(null);
  }

  public PeriodoAcademico buscarPeriodoActivo() {
    return repositorio.listarPeriodosAcademicos().stream()
        .filter(p -> p.getEstado() == EstadoPeriodo.ACTIVO).findFirst().orElse(null);
  }

  public PeriodoAcademico buscarPeriodo(String idPeriodo) {
    if (idPeriodo == null)
      return null;
    return repositorio.listarPeriodosAcademicos().stream()
        .filter(p -> p.getIdPeriodo().equals(idPeriodo)).findFirst().orElse(null);
  }

  public List<Curso> listarCursosDeProfesor(Profesor profesor) {
    if (profesor == null)
      return List.of();
    List<SeccionCurso> secciones = repositorio.listarSecciones();
    List<Curso> cursos = repositorio.listarCursos();
    List<Curso> resultado = new ArrayList<>();
    for (HorarioSeccion horario : repositorio.listarHorarios()) {
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

  public List<SeccionCurso> listarSeccionesDeProfesor(Profesor profesor, Curso curso) {
    if (profesor == null || curso == null)
      return List.of();
    List<HorarioSeccion> horarios = repositorio.listarHorarios();
    return repositorio.listarSecciones().stream()
        .filter(s -> curso.getIdCurso().equals(s.getIdCurso()))
        .filter(s -> horarios.stream()
            .anyMatch(h -> s.getIdSeccion().equals(h.getIdSeccion()) && profesor.getId().equals(h.getIdProfesor())))
        .toList();
  }

  public List<SeccionCurso> listarSeccionesDeBloque(String idBloque) {
    return repositorio.listarSecciones().stream()
        .filter(s -> s.getIdBloque().equals(idBloque)).toList();
  }

  public List<HorarioSeccion> listarHorariosDeSeccion(String idSeccion) {
    return repositorio.listarHorarios().stream()
        .filter(h -> h.getIdSeccion().equals(idSeccion)).toList();
  }

  public List<Profesor> listarProfesoresDeSeccion(String idSeccion) {
    List<Profesor> profesores = repositorio.listarProfesores();
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

  public List<SesionClase> listarTodasLasSesiones() {
    return repositorio.listarSesiones();
  }

  public List<SesionClase> listarSesionesDeSeccionEnFecha(String idSeccion, String fecha) {
    if (idSeccion == null || fecha == null)
      return List.of();
    List<HorarioSeccion> horarios = listarHorariosDeSeccion(idSeccion);
    return repositorio.listarSesiones().stream()
        .filter(s -> fecha.equals(s.getFecha()))
        .filter(s -> horarios.stream().anyMatch(h -> h.getIdHorario().equals(s.getIdHorario())))
        .toList();
  }

  public List<Estudiante> listarEstudiantesDeSesion(String idSesion) {
    SeccionCurso seccion = buscarSesion(idSesion).flatMap(this::buscarSeccionDeSesion).orElse(null);
    return seccion == null ? List.of() : listarEstudiantesActivosEnBloque(seccion.getIdBloque());
  }

  public List<Estudiante> listarEstudiantesDeCursoEnBloque(Curso curso, String idBloque) {
    if (curso == null || idBloque == null)
      return List.of();
    boolean cursoEnBloque = repositorio.listarSecciones().stream()
        .anyMatch(s -> s.getIdCurso().equals(curso.getIdCurso()) && s.getIdBloque().equals(idBloque));
    return cursoEnBloque ? listarEstudiantesActivosEnBloque(idBloque) : List.of();
  }

  public List<Matricula> listarMatriculasDeEstudiante(String idEstudiante) {
    return repositorio.listarMatriculas().stream()
        .filter(m -> m.getIdEstudiante().equals(idEstudiante)).toList();
  }

  public int horasProgramadasDe(RegistroDeAsistencia registro) {
    if (registro == null || registro.getIdSesion().isEmpty())
      return 0;
    return buscarSesion(registro.getIdSesion()).map(SesionClase::getHorasProgramadas).orElse(0);
  }

  public double calcularPorcentajeFaltas(int horasDictadas, int horasAsistidas, int totalHorasLectivas) {
    if (totalHorasLectivas <= 0 || horasDictadas <= 0)
      return 0.0;
    int noAsistidas = Math.max(0, horasDictadas - horasAsistidas);
    return (double) noAsistidas / totalHorasLectivas * 100.0;
  }

  public double calcularPorcentajeFaltasDeCurso(List<RegistroDeAsistencia> asistencias, String idCurso,
      String idMatricula) {
    Curso curso = buscarCurso(idCurso);
    if (curso == null || curso.getTotalHorasLectivas() <= 0)
      return 0.0;
    int horasDictadas = 0;
    int horasAsistidas = 0;
    for (RegistroDeAsistencia registro : asistencias) {
      if (registro.getIdCurso().equals(idCurso) && idMatricula.equals(registro.getIdMatricula())) {
        horasDictadas += horasProgramadasDe(registro);
        if (registro.isPresente()) {
          horasAsistidas += registro.getHorasAsistidas();
        }
      }
    }
    return calcularPorcentajeFaltas(horasDictadas, horasAsistidas, curso.getTotalHorasLectivas());
  }

  public int calcularSemanaLectiva(PeriodoAcademico periodo, LocalDate fecha) {
    if (periodo == null || fecha == null || periodo.getSemanasLectivas() <= 0)
      return 0;
    if (periodo.getFechaInicio() == null || periodo.getFechaFin() == null
        || periodo.getFechaInicio().isBlank() || periodo.getFechaFin().isBlank())
      return 0;
    try {
      LocalDate inicio = LocalDate.parse(periodo.getFechaInicio());
      LocalDate fin = LocalDate.parse(periodo.getFechaFin());
      if (fecha.isBefore(inicio) || fecha.isAfter(fin))
        return 0;
      long dias = ChronoUnit.DAYS.between(inicio, fecha);
      if (dias >= (long) periodo.getSemanasLectivas() * 7)
        return 0;
      return (int) (dias / 7) + 1;
    } catch (DateTimeParseException e) {
      return 0;
    }
  }

  public RegistroDeAsistencia registrar(String idSesion, String idEstudiante, int horasAsistidas) {
    SesionClase sesion = buscarSesion(idSesion).orElse(null);
    SeccionCurso seccion = sesion == null ? null : buscarSeccionDeSesion(sesion).orElse(null);
    if (seccion == null)
      return null;
    Curso curso = buscarCurso(seccion.getIdCurso());
    Estudiante estudiante = buscarEstudiante(idEstudiante);
    if (curso == null || estudiante == null)
      return null;
    Matricula matricula = repositorio.listarMatriculas().stream()
        .filter(m -> m.getIdEstudiante().equals(idEstudiante)
            && m.getIdBloque().equals(seccion.getIdBloque())
            && m.getEstado() == EstadoMatricula.ACTIVA)
        .findFirst().orElse(null);
    if (matricula == null)
      return null;
    int horasValidas = Math.max(0, Math.min(horasAsistidas, sesion.getHorasProgramadas()));
    return new RegistroDeAsistencia(null, curso.getIdCurso(), estudiante.getId(), sesion.getFecha(), horasValidas,
        horasValidas > 0, sesion.getIdSesion(), matricula.getIdMatricula());
  }

  private Optional<SesionClase> buscarSesion(String idSesion) {
    if (idSesion == null)
      return Optional.empty();
    return repositorio.listarSesiones().stream().filter(s -> s.getIdSesion().equals(idSesion)).findFirst();
  }

  private Optional<SeccionCurso> buscarSeccionDeSesion(SesionClase sesion) {
    return repositorio.listarHorarios().stream()
        .filter(h -> h.getIdHorario().equals(sesion.getIdHorario()))
        .findFirst()
        .flatMap(h -> repositorio.listarSecciones().stream()
            .filter(s -> s.getIdSeccion().equals(h.getIdSeccion()))
            .findFirst());
  }

  private List<Estudiante> listarEstudiantesActivosEnBloque(String idBloque) {
    List<Estudiante> estudiantes = repositorio.listarEstudiantes();
    List<Estudiante> resultado = new ArrayList<>();
    for (Matricula matricula : repositorio.listarMatriculas()) {
      if (matricula.getEstado() == EstadoMatricula.ACTIVA && matricula.getIdBloque().equals(idBloque)) {
        estudiantes.stream().filter(e -> e.getId().equals(matricula.getIdEstudiante())).findFirst()
            .ifPresent(resultado::add);
      }
    }
    return resultado;
  }
}
