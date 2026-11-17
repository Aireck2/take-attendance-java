package repositorio;

import java.util.List;
import java.util.Optional;

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

/** Contrato de consulta y persistencia de registros de asistencia. */
public interface RepositorioAsistencia {
  List<RegistroDeAsistencia> listarTodos();

  List<RegistroDeAsistencia> listarPorEstudiante(String idEstudiante);

  void guardarLote(List<RegistroDeAsistencia> registros);

  Optional<SesionClase> buscarSesion(String idSesion);

  Optional<SeccionCurso> buscarSeccionDeSesion(SesionClase sesion);

  Optional<Curso> buscarCurso(String idCurso);

  Optional<Estudiante> buscarEstudiante(String idEstudiante);

  Optional<Matricula> buscarMatriculaActiva(String idEstudiante, String idBloque);

  List<Estudiante> listarEstudiantesDeSesion(String idSesion);

  List<SesionClase> listarSesionesDeCursoEnFecha(Curso curso, String fecha);

  List<Curso> listarCursosDeProfesor(Profesor profesor);

  List<SeccionCurso> listarSeccionesDeProfesor(Profesor profesor, Curso curso);

  Optional<BloqueSeccion> buscarBloque(String idBloque);

  List<HorarioSeccion> listarHorariosDeSeccion(String idSeccion);

  Optional<Salon> buscarSalon(String idSalon);

  List<SesionClase> listarTodasLasSesiones();

  List<Estudiante> listarEstudiantesDeCursoEnBloque(Curso curso, String idBloque);

  List<Profesor> listarProfesoresDeSeccion(String idSeccion);

  Optional<Carrera> buscarCarreraDeCurso(Curso curso);

  List<PeriodoAcademico> listarPeriodosAcademicos();

  Optional<Profesor> buscarProfesor(String idProfesor);

  List<Matricula> listarMatriculasDeEstudiante(String idEstudiante);

  List<SeccionCurso> listarSeccionesDeBloque(String idBloque);

}
