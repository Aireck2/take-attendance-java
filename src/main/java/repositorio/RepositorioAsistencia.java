package repositorio;

import java.util.List;

import models.RegistroDeAsistencia;
import models.SesionClase;
import models.SeccionCurso;
import models.Curso;
import models.Estudiante;
import models.Matricula;
import models.Profesor;
import models.BloqueSeccion;
import models.HorarioSeccion;
import models.Salon;
import models.Carrera;
import models.PeriodoAcademico;
import java.util.Optional;

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
