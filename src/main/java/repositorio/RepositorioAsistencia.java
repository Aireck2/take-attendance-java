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

/** Contrato de acceso a datos de asistencia y catalogos academicos. */
public interface RepositorioAsistencia {
  List<RegistroDeAsistencia> listarTodos();

  void guardarLote(List<RegistroDeAsistencia> registros);

  List<Estudiante> listarEstudiantes();

  List<Profesor> listarProfesores();

  default Optional<Profesor> buscarProfesor(String idProfesor) {
    return idProfesor == null ? Optional.empty()
        : listarProfesores().stream().filter(p -> p.getId().equals(idProfesor)).findFirst();
  }

  List<Curso> listarCursos();

  Optional<Carrera> buscarCarreraDeCurso(Curso curso);

  List<PeriodoAcademico> listarPeriodosAcademicos();

  List<BloqueSeccion> listarBloques();

  List<SeccionCurso> listarSecciones();

  List<HorarioSeccion> listarHorarios();

  List<SesionClase> listarSesiones();

  List<Salon> listarSalones();

  List<Matricula> listarMatriculas();
}
