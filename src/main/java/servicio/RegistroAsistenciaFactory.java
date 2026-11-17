package servicio;

import modelo.Curso;
import modelo.Estudiante;
import modelo.RegistroDeAsistencia;
import modelo.SesionClase;

/** Creates validated attendance records without persistence side effects. */
final class RegistroAsistenciaFactory {
  private RegistroAsistenciaFactory() {
  }

  static RegistroDeAsistencia crear(SesionClase sesion, Curso curso, Estudiante estudiante,
      int horasAsistidas, String idMatricula) {
    if (sesion == null || curso == null || estudiante == null)
      return null;
    int horasValidas = Math.max(0, Math.min(horasAsistidas, sesion.getHorasProgramadas()));
    return new RegistroDeAsistencia(null, curso.getIdCurso(), estudiante.getId(), sesion.getFecha(), horasValidas,
        horasValidas > 0, sesion.getIdSesion(), idMatricula);
  }
}
