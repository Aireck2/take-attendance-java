package sistema;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import models.Curso;
import models.Estudiante;
import models.Profesor;
import models.RegistroDeAsistencia;
import service.ServicioAcademico;
import storage.ArchivoManager;

/**
 * Aplicacion de terminal para tomar asistencia de clases
 * en un sistema universitario.
 *
 * Orquesta el menu principal y delega la logica de negocio
 * a la capa de servicio.
 */
public class SistemaApp {
  private static final java.util.Scanner scanner = new java.util.Scanner(System.in);
  private static final ServicioAcademico servicio = new ServicioAcademico();

  public static void main(String[] args) {
    int opcion = 0;
    do {
      System.out.println("\n======================================");
      System.out.println("  SISTEMA DE ASISTENCIA - IESTP CHINCHA  ");
      System.out.println("======================================");
      System.out.println("1. Iniciar sesión como Profesor");
      System.out.println("2. Iniciar sesión como Estudiante");
      System.out.println("3. Salir");
      opcion = leerOpcion("Seleccione una opción: ");
      switch (opcion) {
        case 1 -> menuProfesor();
        case 2 -> menuEstudiante();
        case 3 -> System.out.println("Saliendo del sistema...");
        default -> System.out.println("Opción inválida.");
      }
    } while (opcion != 3);
  }

  /**
   * Solicita un número al usuario y devuelve la opción elegida.
   * Si la entrada no es numérica, descarta el token y vuelve a pedir.
   */
  private static int leerOpcion(String mensaje) {
    System.out.print(mensaje);
    while (!scanner.hasNextInt()) {
      scanner.next();
      System.out.print(mensaje);
    }
    int opcion = scanner.nextInt();
    scanner.nextLine();
    return opcion;
  }

  /**
   * Menu del profesor: ver cursos, ver perfil o regresar.
   */
  private static void menuProfesor() {
    List<Profesor> profesores = servicio.listarProfesores();
    System.out.println("\n--- SELECCIONE UN PROFESOR ---");
    for (int i = 0; i < profesores.size(); i++) {
      System.out
          .println((i + 1) + ". " + profesores.get(i).getNombreCompleto() + " (" + profesores.get(i).getId() + ")");
    }
    int idx = leerOpcion("Elija profesor (1-" + profesores.size() + "): ") - 1;
    if (idx < 0 || idx >= profesores.size()) {
      System.out.println("Profesor no válido.");
      return;
    }

    Profesor profActual = profesores.get(idx);
    int opcProf = 0;
    do {
      System.out.println("\n--- MENÚ PROFESOR: " + profActual.getNombreCompleto() + " ---");
      System.out.println("1. Ver mis cursos asignados");
      System.out.println("2. Ver mi perfil");
      System.out.println("3. Regresar al menú principal");
      opcProf = leerOpcion("Elija una opción: ");
      switch (opcProf) {
        case 1 -> gestionarCursosProfesor(profActual);
        case 2 -> profActual.mostrarPerfil();
        case 3 -> System.out.println("Regresando...");
        default -> System.out.println("Opción inválida.");
      }
    } while (opcProf != 3);
  }

  /**
   * Muestra los cursos del profesor, su detalle y permite tomar asistencia.
   */
  private static void gestionarCursosProfesor(Profesor prof) {
    List<Curso> misCursos = servicio.listarCursosProfesor(prof);
    if (misCursos.isEmpty()) {
      System.out.println("No tiene cursos asignados.");
      return;
    }

    System.out.println("\n--- SUS CURSOS ASIGNADOS ---");
    for (int i = 0; i < misCursos.size(); i++) {
      System.out
          .println((i + 1) + ". " + misCursos.get(i).getNombreCurso() + " [ID: " + misCursos.get(i).getIdCurso() + "]");
    }
    int idx = leerOpcion("Seleccione un curso para ver detalles / tomar asistencia: ") - 1;
    if (idx < 0 || idx >= misCursos.size()) {
      System.out.println("Curso no válido.");
      return;
    }

    Curso cursoSel = misCursos.get(idx);
    System.out.println("\n--- DETALLES DEL CURSO ---");
    System.out.println("Curso: " + cursoSel.getNombreCurso());
    System.out.println("Horas por sesión: " + cursoSel.getTotalHorasSesion() + " hrs");

    List<Estudiante> alumnosCurso = servicio.listarEstudiantesCurso(cursoSel);
    if (alumnosCurso.isEmpty()) {
      System.out.println("No hay alumnos inscritos en este curso.");
      return;
    }

    System.out.println("Alumnos matriculados:");
    for (Estudiante est : alumnosCurso) {
      System.out.println(" - " + est.getNombreCompleto() + " (Código: " + est.getCodigo() + ")");
    }

    int tomar = leerOpcion("\n¿Desea tomar asistencia para este curso hoy? (1. Sí / 2. No): ");
    if (tomar == 1) {
      String fecha = LocalDate.now().toString();
      List<RegistroDeAsistencia> registros = new ArrayList<>();
      System.out.println("\n--- REGISTRO DE ASISTENCIA ---");
      for (Estudiante est : alumnosCurso) {
        System.out.println("Alumno: " + est.getNombreCompleto() + " (" + est.getCodigo() + ")");
        int estado = leerOpcion("¿Asistió? (1: Presente / 0: Ausente): ");
        boolean presente = (estado == 1);
        int horasAsistidas = 0;
        if (presente) {
          int maxHoras = cursoSel.getTotalHorasSesion();
          horasAsistidas = leerOpcion("Horas asistidas (Máximo " + maxHoras + "): ");
          if (horasAsistidas > maxHoras) {
            horasAsistidas = maxHoras;
          }
        }
        registros.add(servicio.crearRegistro(cursoSel, est, fecha, horasAsistidas, presente));
      }
      servicio.guardarAsistencias(registros);
      System.out.println("¡Asistencia guardada exitosamente en asistencias.csv!");
    }
  }

  /**
   * Menu del estudiante: ver resumen de asistencia o ver perfil.
   */
  private static void menuEstudiante() {
    List<Estudiante> estudiantes = servicio.listarEstudiantes();
    System.out.println("\n--- SELECCIONE UN ESTUDIANTE ---");
    for (int i = 0; i < estudiantes.size(); i++) {
      System.out.println(
          (i + 1) + ". " + estudiantes.get(i).getNombreCompleto() + " (" + estudiantes.get(i).getCodigo() + ")");
    }
    int idx = leerOpcion("Elija estudiante (1-" + estudiantes.size() + "): ") - 1;
    if (idx < 0 || idx >= estudiantes.size()) {
      System.out.println("Estudiante no válido.");
      return;
    }

    Estudiante estActual = estudiantes.get(idx);
    int opcEst = 0;
    do {
      System.out.println("\n--- MENÚ ESTUDIANTE: " + estActual.getNombreCompleto() + " ---");
      System.out.println("1. Ver cursos asignados y estado de asistencia");
      System.out.println("2. Ver mi perfil");
      System.out.println("3. Regresar al menú principal");
      opcEst = leerOpcion("Elija una opción: ");
      switch (opcEst) {
        case 1 -> mostrarResumenAsistenciaEstudiante(estActual);
        case 2 -> estActual.mostrarPerfil();
        case 3 -> System.out.println("Regresando...");
        default -> System.out.println("Opción inválida.");
      }
    } while (opcEst != 3);
  }

  /**
   * Muestra el resumen de asistencia del estudiante: dias, horas, porcentaje de
   * faltas y alerta.
   */
  private static void mostrarResumenAsistenciaEstudiante(Estudiante est) {
    List<Curso> cursos = servicio.listarCursosEstudiante(est);
    List<RegistroDeAsistencia> misAsistencias = servicio.historialEstudiante(est);

    System.out.println("\n==============================================");
    System.out.println("       RESUMEN DE ASISTENCIA Y CURSOS         ");
    System.out.println("==============================================");

    for (Curso curso : cursos) {
      int clasesDictadas = 0;
      int clasesAsistidas = 0;
      int horasDictadas = 0;
      int horasAsistidas = 0;

      for (RegistroDeAsistencia reg : misAsistencias) {
        if (reg.getIdCurso().equals(curso.getIdCurso())) {
          clasesDictadas++;
          horasDictadas += curso.getTotalHorasSesion();
          if (reg.isPresente()) {
            clasesAsistidas++;
            horasAsistidas += reg.getHorasAsistidas();
          }
        }
      }

      // Total de horas del curso: duracion del curso (dias) * horas por sesion
      int totalHorasCurso = curso.getDuracionCurso() * curso.getTotalHorasSesion();

      double porcentajeFaltas = servicio.calcularPorcentajeFaltas(horasDictadas, horasAsistidas, totalHorasCurso);
      System.out.println("Curso: " + curso.getNombreCurso());
      System.out.println(" - Dias asistidos: " + clasesAsistidas + " / " + clasesDictadas + " (de "
          + curso.getDuracionCurso() + " dias totales)");
      System.out.println(" - Horas asistidas: " + horasAsistidas + " / " + horasDictadas + " hrs");
      System.out.printf(" - Porcentaje de inasistencia (Faltas): %.2f%%\n", porcentajeFaltas);

      if (porcentajeFaltas > ServicioAcademico.LIMITE_INASISTENCIA) {
        System.out.println("   [ALERTA: Límite de inasistencia superado]");
      }
      System.out.println("----------------------------------------------");
    }
  }
}
