package sistema;

import java.io.Console;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import models.Curso;
import models.Estudiante;
import models.Profesor;
import models.RegistroDeAsistencia;
import models.Rol;
import models.Usuario;
import service.AutenticacionService;
import service.ServicioAcademico;

/**
 * Aplicacion de terminal para tomar asistencia de clases
 * en un sistema universitario.
 *
 * Orquesta el inicio de sesion, los menus por rol y delega la logica
 * de negocio a la capa de servicio.
 */
public class SistemaApp {
  private static final Scanner scanner = new Scanner(System.in);
  private static final ServicioAcademico servicio = new ServicioAcademico();
  private static final AutenticacionService autenticacion = new AutenticacionService();

  public static void main(String[] args) {
    int opcion = 0;
    do {
      System.out.println("\n======================================");
      System.out.println("  SISTEMA DE ASISTENCIA - IESTP CHINCHA  ");
      System.out.println("======================================");
      System.out.println("1. Iniciar sesión");
      System.out.println("2. Salir");
      opcion = leerOpcion("Seleccione una opción: ");
      switch (opcion) {
        case 1 -> iniciarSesion();
        case 2 -> System.out.println("Saliendo del sistema...");
        default -> System.out.println("Opción inválida.");
      }
    } while (opcion != 2);
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
   * Pide correo y contraseña, valida las credenciales y abre el menú del rol.
   * Si las credenciales son incorrectas, vuelve al menú principal.
   */
  private static void iniciarSesion() {
    System.out.println("\n--- INICIO DE SESIÓN ---");
    System.out.print("Correo: ");
    String correo = scanner.nextLine().trim();
    String contrasena = leerContrasena("Contraseña: ");

    Usuario usuario = autenticacion.autenticar(correo, contrasena);
    if (usuario == null) {
      System.out.println("Credenciales inválidas.");
      return;
    }

    System.out.println("Sesión iniciada como " + usuario.getRol() + ".");
    if (usuario.getRol() == Rol.PROFESOR) {
      Profesor profesor = servicio.buscarProfesor(usuario.getIdReferencia());
      if (profesor == null) {
        System.out.println("No se encontró el perfil del profesor asociado a la cuenta.");
        return;
      }
      System.out.println("Bienvenido, " + profesor.getNombreCompleto() + ".");
      menuProfesor(profesor);
    } else {
      Estudiante estudiante = servicio.buscarEstudiante(usuario.getIdReferencia());
      if (estudiante == null) {
        System.out.println("No se encontró el perfil del estudiante asociado a la cuenta.");
        return;
      }
      System.out.println("Bienvenido, " + estudiante.getNombreCompleto() + ".");
      menuEstudiante(estudiante);
    }
    System.out.println("Sesión cerrada.");
  }

  /**
   * Lee la contraseña sin mostrarla cuando la terminal lo permite.
   * Si no hay consola interactiva (por ejemplo, entrada redirigida), usa el scanner.
   */
  private static String leerContrasena(String mensaje) {
    System.out.print(mensaje);
    Console console = System.console();
    if (console != null) {
      char[] caracteres = console.readPassword();
      return caracteres == null ? "" : new String(caracteres);
    }
    return scanner.nextLine();
  }

  /**
   * Menu del profesor: ver cursos, ver perfil o cerrar sesión.
   */
  private static void menuProfesor(Profesor profActual) {
    int opcProf = 0;
    do {
      System.out.println("\n--- MENÚ PROFESOR: " + profActual.getNombreCompleto() + " ---");
      System.out.println("1. Ver mis cursos asignados");
      System.out.println("2. Ver mi perfil");
      System.out.println("3. Cerrar sesión");
      opcProf = leerOpcion("Elija una opción: ");
      switch (opcProf) {
        case 1 -> gestionarCursosProfesor(profActual);
        case 2 -> profActual.mostrarPerfil();
        case 3 -> {
        }
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
        int maxHoras = cursoSel.getTotalHorasSesion();
        int horasAsistidas = leerOpcion("Horas asistidas (0 = ausente, máximo " + maxHoras + "): ");
        registros.add(servicio.crearRegistro(cursoSel, est, fecha, horasAsistidas));
      }
      servicio.guardarAsistencias(registros);
      System.out.println("¡Asistencia guardada exitosamente en registro_de_asistencia.csv!");
    }
  }

  /**
   * Menu del estudiante: ver resumen de asistencia, ver perfil o cerrar sesión.
   */
  private static void menuEstudiante(Estudiante estActual) {
    int opcEst = 0;
    do {
      System.out.println("\n--- MENÚ ESTUDIANTE: " + estActual.getNombreCompleto() + " ---");
      System.out.println("1. Ver cursos asignados y estado de asistencia");
      System.out.println("2. Ver mi perfil");
      System.out.println("3. Cerrar sesión");
      opcEst = leerOpcion("Elija una opción: ");
      switch (opcEst) {
        case 1 -> mostrarResumenAsistenciaEstudiante(estActual);
        case 2 -> estActual.mostrarPerfil();
        case 3 -> {
        }
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
