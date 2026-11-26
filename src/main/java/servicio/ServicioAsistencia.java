package servicio;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.List;

import modelo.BloqueSeccion;
import modelo.Carrera;
import modelo.Curso;
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

/** Coordina el registro, consulta y persistencia de asistencia. */
public final class ServicioAsistencia {
  private final RepositorioAsistencia repositorio;

  public ServicioAsistencia(RepositorioAsistencia repositorio) {
    this.repositorio = repositorio;
  }

  public List<RegistroDeAsistencia> listarPorEstudiante(String idEstudiante) {
    return repositorio.listarPorEstudiante(idEstudiante);
  }

  public List<Estudiante> listarEstudiantesDeSesion(String idSesion) {
    return repositorio.listarEstudiantesDeSesion(idSesion);
  }

  public List<SesionClase> listarSesionesDeCursoEnFecha(Curso curso, String fecha) {
    return repositorio.listarSesionesDeCursoEnFecha(curso, fecha);
  }

  public List<Curso> listarCursosDeProfesor(Profesor profesor) {
    return repositorio.listarCursosDeProfesor(profesor);
  }

  public List<SeccionCurso> listarSeccionesDeProfesor(Profesor profesor, Curso curso) {
    return repositorio.listarSeccionesDeProfesor(profesor, curso);
  }

  public BloqueSeccion buscarBloque(String idBloque) {
    return repositorio.buscarBloque(idBloque).orElse(null);
  }

  public List<HorarioSeccion> listarHorariosDeSeccion(String idSeccion) {
    return repositorio.listarHorariosDeSeccion(idSeccion);
  }

  public Salon buscarSalon(String idSalon) {
    return repositorio.buscarSalon(idSalon).orElse(null);
  }

  public List<SesionClase> listarTodasLasSesiones() {
    return repositorio.listarTodasLasSesiones();
  }

  public List<Estudiante> listarEstudiantesDeCursoEnBloque(Curso curso, String idBloque) {
    return repositorio.listarEstudiantesDeCursoEnBloque(curso, idBloque);
  }

  public List<Profesor> listarProfesoresDeSeccion(String idSeccion) {
    return repositorio.listarProfesoresDeSeccion(idSeccion);
  }

  public Carrera buscarCarreraDeCurso(Curso curso) {
    return repositorio.buscarCarreraDeCurso(curso).orElse(null);
  }

  public List<PeriodoAcademico> listarPeriodosAcademicos() {
    return repositorio.listarPeriodosAcademicos();
  }

  public Profesor buscarProfesor(String idProfesor) {
    return repositorio.buscarProfesor(idProfesor).orElse(null);
  }

  public Estudiante buscarEstudiante(String idEstudiante) {
    return repositorio.buscarEstudiante(idEstudiante).orElse(null);
  }

  public List<Matricula> listarMatriculasDeEstudiante(String idEstudiante) {
    return repositorio.listarMatriculasDeEstudiante(idEstudiante);
  }

  public List<SeccionCurso> listarSeccionesDeBloque(String idBloque) {
    return repositorio.listarSeccionesDeBloque(idBloque);
  }

  public Curso buscarCurso(String idCurso) {
    return repositorio.buscarCurso(idCurso).orElse(null);
  }

  public int horasProgramadasDe(RegistroDeAsistencia registro) {
    if (registro == null || registro.getIdSesion() == null || registro.getIdSesion().isEmpty())
      return 0;
    return repositorio.buscarSesion(registro.getIdSesion())
        .map(SesionClase::getHorasProgramadas).orElse(0);
  }

  public double calcularPorcentajeFaltas(int horasDictadas, int horasAsistidas) {
    if (horasDictadas <= 0)
      return 0.0;
    int noAsistidas = Math.max(0, horasDictadas - horasAsistidas);
    return (double) noAsistidas / horasDictadas * 100.0;
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
    SesionClase sesion = repositorio.buscarSesion(idSesion).orElse(null);
    if (sesion == null)
      return null;
    SeccionCurso seccion = repositorio.buscarSeccionDeSesion(sesion).orElse(null);
    if (seccion == null)
      return null;
    Curso curso = repositorio.buscarCurso(seccion.getIdCurso()).orElse(null);
    Estudiante estudiante = repositorio.buscarEstudiante(idEstudiante).orElse(null);
    if (curso == null || estudiante == null)
      return null;
    Matricula matricula = repositorio.buscarMatriculaActiva(idEstudiante, seccion.getIdBloque()).orElse(null);
    if (matricula == null)
      return null;
    return construirRegistro(sesion, curso, estudiante, horasAsistidas, matricula.getIdMatricula());
  }

  public RegistroDeAsistencia crear(SesionClase sesion, Curso curso, Estudiante estudiante, int horasAsistidas) {
    return construirRegistro(sesion, curso, estudiante, horasAsistidas, null);
  }

  public void guardarLote(List<RegistroDeAsistencia> registros) {
    repositorio.guardarLote(registros);
  }

  private static RegistroDeAsistencia construirRegistro(SesionClase sesion, Curso curso, Estudiante estudiante,
      int horasAsistidas, String idMatricula) {
    if (sesion == null || curso == null || estudiante == null)
      return null;
    int horasValidas = Math.max(0, Math.min(horasAsistidas, sesion.getHorasProgramadas()));
    return new RegistroDeAsistencia(null, curso.getIdCurso(), estudiante.getId(), sesion.getFecha(), horasValidas,
        horasValidas > 0, sesion.getIdSesion(), idMatricula);
  }
}
