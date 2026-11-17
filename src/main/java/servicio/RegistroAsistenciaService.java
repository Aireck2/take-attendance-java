package servicio;

import modelo.Curso;
import modelo.Estudiante;
import modelo.Matricula;
import modelo.RegistroDeAsistencia;
import modelo.SeccionCurso;
import modelo.SesionClase;
import repositorio.RepositorioAsistencia;

/** Resolves attendance registration inputs and enforces active enrollment. */
final class RegistroAsistenciaService {
  private final RepositorioAsistencia repositorio;

  RegistroAsistenciaService(RepositorioAsistencia repositorio) {
    this.repositorio = repositorio;
  }

  RegistroDeAsistencia registrar(String idSesion, String idEstudiante, int horasAsistidas) {
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
    return RegistroAsistenciaFactory.crear(sesion, curso, estudiante, horasAsistidas, matricula.getIdMatricula());
  }
}
