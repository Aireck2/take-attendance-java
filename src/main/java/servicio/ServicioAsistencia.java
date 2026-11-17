package servicio;

import java.time.LocalDate;
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
  private final RegistroAsistenciaService registro;

  public ServicioAsistencia(RepositorioAsistencia repositorio) {
    this.repositorio = repositorio;
    this.registro = new RegistroAsistenciaService(repositorio);
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
    return CalendarioAcademico.semanaLectiva(periodo, fecha);
  }

  public RegistroDeAsistencia registrar(String idSesion, String idEstudiante, int horasAsistidas) {
    return registro.registrar(idSesion, idEstudiante, horasAsistidas);
  }

  public RegistroDeAsistencia crear(SesionClase sesion, Curso curso, Estudiante estudiante, int horasAsistidas) {
    return RegistroAsistenciaFactory.crear(sesion, curso, estudiante, horasAsistidas, null);
  }

  public void guardarLote(List<RegistroDeAsistencia> registros) {
    repositorio.guardarLote(registros);
  }
}
