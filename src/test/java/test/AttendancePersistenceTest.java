package test;

import java.util.Comparator;
import java.util.List;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

import models.RegistroDeAsistencia;
import models.Curso;
import models.Estudiante;
import models.Profesor;
import models.SeccionCurso;
import models.SesionClase;
import service.ServicioAsistencia;
import almacenamiento.csv.RepositorioAsistenciaCsv;

/** Run only from an isolated working directory containing a disposable data/ copy. */
public final class AttendancePersistenceTest {
  private AttendancePersistenceTest() {}

  public static void main(String[] args) {
    RepositorioAsistenciaCsv repositorio = new RepositorioAsistenciaCsv();
    ServicioAsistencia servicio = new ServicioAsistencia(repositorio);
    Profesor profesor = repositorio.buscarProfesor("DOC-001").orElse(null);
    if (profesor == null) throw new AssertionError("No se encontró al profesor asignado");
    Curso curso = servicio.listarCursosDeProfesor(profesor).stream()
        .filter(item -> item.getIdCurso().equals("UD-ETHHACK")).findFirst().orElseThrow();
    if (!"Arquitectura de Plataformas y Servicios de Tecnologías de Información"
        .equals(servicio.buscarCarreraDeCurso(curso).getNombre())) {
      throw new AssertionError("No se resolvió la carrera desde la cadena curricular del curso");
    }
    SeccionCurso seccion = servicio.listarSeccionesDeProfesor(profesor, curso).stream().findFirst().orElseThrow();
    List<String> horarios = servicio.listarHorariosDeSeccion(seccion.getIdSeccion()).stream()
        .map(item -> item.getIdHorario()).toList();
    SesionClase sesion = servicio.listarTodasLasSesiones().stream()
        .filter(item -> horarios.contains(item.getIdHorario())).findFirst().orElseThrow();
    Estudiante estudiante = servicio.listarEstudiantesDeSesion(sesion.getIdSesion()).stream()
        .findFirst().orElseThrow();
    RegistroDeAsistencia registroFlujo = servicio.registrar(sesion.getIdSesion(), estudiante.getId(), 1);
    if (registroFlujo == null || !curso.getIdCurso().equals(registroFlujo.getIdCurso())
        || registroFlujo.getIdMatricula().isEmpty()) {
      throw new AssertionError("El flujo no resolvió el curso y la matrícula de la asistencia");
    }
    Path archivoAsistencia = Path.of("data", "registro_de_asistencia.csv");
    try {
      Files.writeString(archivoAsistencia,
          "REG-LEGACY,UD-SEGREDES,ALU-2024101,2026-01-01,2,true\n", StandardOpenOption.APPEND);
    } catch (java.io.IOException e) {
      throw new AssertionError("No se pudo agregar la fila legada de asistencia", e);
    }
    List<RegistroDeAsistencia> existentes = repositorio.listarTodos();
    RegistroDeAsistencia legado = existentes.stream().filter(item -> item.getIdAsistencia().equals("REG-LEGACY"))
        .findFirst().orElseThrow();
    if (!legado.getIdSesion().isEmpty() || !legado.getIdMatricula().isEmpty() || !legado.isPresente()) {
      throw new AssertionError("Falló la compatibilidad con la fila legada de asistencia");
    }
    String ultimoId = existentes.stream().map(RegistroDeAsistencia::getIdAsistencia)
        .filter(id -> id != null && id.startsWith("REG-ASI-"))
        .max(Comparator.comparingInt(id -> Integer.parseInt(id.substring("REG-ASI-".length()))))
        .orElse("REG-ASI-0000");
    int siguiente = Integer.parseInt(ultimoId.substring("REG-ASI-".length())) + 1;
    RegistroDeAsistencia nuevo = new RegistroDeAsistencia(null, "UD-SEGREDES", "ALU-2024101", "2026-12-31", 1,
        true, "SES-TEST-PERSISTENCIA", "MAT-20261-001");
    repositorio.guardarLote(List.of(registroFlujo, nuevo));
    List<RegistroDeAsistencia> guardados = repositorio.listarTodos();
    if (guardados.size() != existentes.size() + 2
        || !registroFlujo.getIdAsistencia().equals(String.format("REG-ASI-%04d", siguiente))
        || !nuevo.getIdAsistencia().equals(String.format("REG-ASI-%04d", siguiente + 1))) {
      throw new AssertionError("Falló la verificación del append o de los identificadores secuenciales");
    }
    System.out.println("Pruebas aisladas del flujo y persistencia de asistencia aprobadas.");
  }
}
