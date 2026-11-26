package almacenamiento.csv;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
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

/** Adaptador CSV del repositorio de asistencia y catalogos academicos. */
public final class RepositorioAsistenciaCsv implements RepositorioAsistencia {
  private static final String ARCHIVO_ASISTENCIA = "data/registro_de_asistencia.csv";

  @Override
  public List<RegistroDeAsistencia> listarTodos() {
    return LectorCsv.leer(ARCHIVO_ASISTENCIA, c -> {
      if (c.length < 6)
        return null;
      boolean presente = "1".equals(c[5]) || "true".equalsIgnoreCase(c[5]);
      String idSesion = c.length >= 7 ? c[6] : "";
      String idMatricula = c.length >= 8 ? c[7] : "";
      return new RegistroDeAsistencia(c[0], c[1], c[2], c[3], Integer.parseInt(c[4]), presente, idSesion, idMatricula);
    });
  }

  @Override
  public void guardarLote(List<RegistroDeAsistencia> registros) {
    int maxId = 0;
    for (RegistroDeAsistencia reg : listarTodos()) {
      String id = reg.getIdAsistencia();
      if (id != null && id.startsWith("REG-ASI-")) {
        try {
          maxId = Math.max(maxId, Integer.parseInt(id.substring("REG-ASI-".length())));
        } catch (NumberFormatException ignored) {
        }
      }
    }
    int siguienteId = maxId + 1;
    for (RegistroDeAsistencia reg : registros) {
      reg.setIdAsistencia(String.format("REG-ASI-%04d", siguienteId++));
    }
    try (PrintWriter escritor = new PrintWriter(new FileWriter(ARCHIVO_ASISTENCIA, true))) {
      for (RegistroDeAsistencia registro : registros)
        escritor.println(registro.toCsv());
    } catch (IOException e) {
      System.out.println("Error al guardar asistencias.");
    }
  }

  @Override
  public List<Estudiante> listarEstudiantes() {
    return LectorCsv.leer("data/estudiante.csv", c -> c.length >= 7
        ? new Estudiante(c[0], c[1], c[2], c[3], c[4], c[5], c[6]) : null);
  }

  @Override
  public List<Profesor> listarProfesores() {
    return LectorCsv.leer("data/profesor.csv", c -> c.length >= 9
        ? new Profesor(c[0], c[1], c[2], c[3], c[4], c[5], Integer.parseInt(c[6]), c[7], c[8]) : null);
  }

  @Override
  public List<Curso> listarCursos() {
    return LectorCsv.leerConCabecera("data/curso.csv", (cabecera, c) -> {
      if (c.length < 7)
        return null;
      int totalHoras = cabecera.contains("total_horas_lectivas")
          ? Integer.parseInt(c[6])
          : Integer.parseInt(c[4]) + Integer.parseInt(c[5]);
      return new Curso(c[0], c[1], c[2], totalHoras);
    });
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
      return new PeriodoAcademico(c[0], c[2], c[3], c[4], EstadoPeriodo.valueOf(c[5]), semanas);
    });
  }

  @Override
  public List<BloqueSeccion> listarBloques() {
    return LectorCsv.leer("data/bloque_seccion.csv", c -> c.length >= 3
        ? new BloqueSeccion(c[0], c[1], c[2]) : null);
  }

  @Override
  public List<SeccionCurso> listarSecciones() {
    return LectorCsv.leer("data/seccion_curso.csv", c -> c.length >= 3
        ? new SeccionCurso(c[0], c[1], c[2]) : null);
  }

  @Override
  public List<HorarioSeccion> listarHorarios() {
    return LectorCsv.leer("data/horario_seccion.csv", c -> c.length >= 8
        ? new HorarioSeccion(c[0], c[1], DiaSemana.valueOf(c[2]), c[3], c[4], c[6], c[7]) : null);
  }

  @Override
  public List<SesionClase> listarSesiones() {
    return LectorCsv.leer("data/sesion_clase.csv", c -> c.length >= 4
        ? new SesionClase(c[0], c[1], c[2], Integer.parseInt(c[3])) : null);
  }

  @Override
  public List<Salon> listarSalones() {
    return LectorCsv.leer("data/salon.csv", c -> c.length >= 2
        ? new Salon(c[0], c[1]) : null);
  }

  @Override
  public List<Matricula> listarMatriculas() {
    return LectorCsv.leer("data/matricula.csv", c -> c.length >= 5
        ? new Matricula(c[0], c[1], c[2], EstadoMatricula.valueOf(c[4])) : null);
  }

  private static Optional<String> buscarValorPorClave(String ruta, String clave) {
    return LectorCsv.leer(ruta, c -> c.length >= 2 && c[0].equals(clave) ? c[1] : null)
        .stream().findFirst();
  }
}
