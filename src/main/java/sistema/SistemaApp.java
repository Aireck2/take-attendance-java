package sistema;

import java.io.Console;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

import models.BloqueSeccion;
import models.Curso;
import models.DiaSemana;
import models.Estudiante;
import models.EstadoMatricula;
import models.EstadoPeriodo;
import models.HorarioSeccion;
import models.Matricula;
import models.PeriodoAcademico;
import models.Profesor;
import models.RegistroDeAsistencia;
import models.Salon;
import models.SeccionCurso;
import models.SesionClase;
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
  private static final int ANCHO_TITULO = 46;
  private final Scanner scanner = new Scanner(System.in);
  private final ServicioAcademico servicio = new ServicioAcademico();
  private final AutenticacionService autenticacion = new AutenticacionService();

  public static void main(String[] args) {
    new SistemaApp().iniciar();
  }

  /**
   * Bucle principal del menu. Solo main es estatico: la clase se instancia y
   * sus atributos y metodos auxiliares pertenecen a la instancia.
   */
  public void iniciar() {
    int opcion = 0;
    do {
      imprimirTitulo("SISTEMA DE ASISTENCIA - IESTP CHINCHA");
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
  private int leerOpcion(String mensaje) {
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
  private void iniciarSesion() {
    imprimirTitulo("INICIO DE SESIÓN");
    System.out.print("Correo: ");
    String correo = scanner.nextLine().trim();
    String contrasena = leerContrasena("Contraseña: ");

    Usuario usuario = autenticacion.autenticar(correo, contrasena);
    if (usuario == null) {
      System.out.println("Credenciales inválidas.");
      return;
    }

    System.out.println("Sesión iniciada como " + usuario.getRol() + ".");
    switch (usuario.getRol().menuSesion()) {
      case PROFESOR -> {
        Profesor profesor = servicio.buscarProfesor(usuario.getIdReferencia());
        if (profesor == null) {
          System.out.println("No se encontró el perfil del profesor asociado a la cuenta.");
          return;
        }
        System.out.println("Bienvenido, " + profesor.getNombreCompleto() + ".");
        menuProfesor(profesor);
      }
      case ESTUDIANTE -> {
        Estudiante estudiante = servicio.buscarEstudiante(usuario.getIdReferencia());
        if (estudiante == null) {
          System.out.println("No se encontró el perfil del estudiante asociado a la cuenta.");
          return;
        }
        System.out.println("Bienvenido, " + estudiante.getNombreCompleto() + ".");
        menuEstudiante(estudiante);
      }
      case NINGUNO -> {
        System.out.println("El rol '" + usuario.getRol() + "' no tiene un menú asignado. Sesión cerrada.");
        return;
      }
    }
    System.out.println("Sesión cerrada.");
  }

  /**
   * Lee la contraseña sin mostrarla cuando la terminal lo permite.
   * Si no hay consola interactiva (por ejemplo, entrada redirigida), usa el scanner.
   */
  private String leerContrasena(String mensaje) {
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
  private void menuProfesor(Profesor profActual) {
    int opcProf = 0;
    do {
      imprimirTitulo("MENÚ PROFESOR: " + profActual.getNombreCompleto());
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
  private void gestionarCursosProfesor(Profesor prof) {
    List<Curso> misCursos = servicio.listarCursosProfesor(prof);
    if (misCursos.isEmpty()) {
      System.out.println("No tiene cursos asignados.");
      return;
    }

    imprimirTitulo("CURSOS ASIGNADOS");
    List<SeccionCurso> seccionesAsignadas = mostrarSeccionesPeriodoActual(prof, misCursos);
    if (seccionesAsignadas.isEmpty()) {
      System.out.println("No hay secciones asignadas en el periodo activo.");
      return;
    }

    int idx = leerOpcion("Seleccione una sección para ver detalles: ") - 1;
    if (idx < 0 || idx >= seccionesAsignadas.size()) {
      System.out.println("Sección no válida.");
      return;
    }

    SeccionCurso seccionSeleccionada = seccionesAsignadas.get(idx);
    Curso cursoSel = misCursos.stream()
        .filter(curso -> curso.getIdCurso().equals(seccionSeleccionada.getIdCurso()))
        .findFirst().orElse(null);
    if (cursoSel == null) {
      System.out.println("No se encontró el curso asociado a esta sección.");
      return;
    }

    imprimirTitulo("DETALLE DEL CURSO");
    mostrarDetalleCurso(seccionSeleccionada, cursoSel);

    List<Estudiante> alumnosCurso = servicio.listarEstudiantesCurso(cursoSel).stream()
        .filter(estudiante -> servicio.buscarMatricula(estudiante.getId(), seccionSeleccionada.getIdBloque()) != null)
        .toList();
    if (alumnosCurso.isEmpty()) {
      System.out.println("No hay alumnos inscritos en este curso.");
      return;
    }

    System.out.println("Alumnos matriculados:");
    for (int i = 0; i < alumnosCurso.size(); i++) {
      Estudiante est = alumnosCurso.get(i);
      System.out.println(" " + (i + 1) + ". " + est.getNombreCompleto());
      System.out.println("    - Código: " + est.getCodigo());
      System.out.println("    - Correo: " + est.getCorreo());
    }

    String fecha = LocalDate.now().toString();
    List<SesionClase> sesionesHoy = servicio.listarSesionesCursoEnFecha(cursoSel, fecha).stream()
        .filter(sesion -> sesionPerteneceASeccion(sesion, seccionSeleccionada))
        .toList();
    if (sesionesHoy.isEmpty()) {
      System.out.println("No hay una sesión programada para este curso en la fecha de hoy.");
      System.out.println("0. Regresar al menú anterior");
      leerOpcion("Seleccione una opción: ");
      return;
    }

    int tomar = leerOpcion("\n¿Desea tomar asistencia para este curso hoy? (1. Sí / 2. No): ");
    if (tomar == 1) {
      SesionClase sesion = sesionesHoy.get(0);
      if (sesionesHoy.size() > 1) {
        System.out.println("Sesiones programadas para hoy:");
        for (int i = 0; i < sesionesHoy.size(); i++) {
          SesionClase opcion = sesionesHoy.get(i);
          System.out.println((i + 1) + ". " + opcion.getIdSesion() + " (" + opcion.getHorasProgramadas()
              + " horas)");
        }
        int seleccionSesion = leerOpcion("Seleccione una sesión: ") - 1;
        if (seleccionSesion < 0 || seleccionSesion >= sesionesHoy.size()) {
          System.out.println("Sesión no válida.");
          return;
        }
        sesion = sesionesHoy.get(seleccionSesion);
      }
      List<Estudiante> alumnosSesion = servicio.listarEstudiantesSesion(sesion.getIdSesion());
      if (alumnosSesion.isEmpty()) {
        System.out.println("No hay alumnos matriculados en el bloque de esta sesión.");
        return;
      }
      List<RegistroDeAsistencia> registros = new ArrayList<>();
      imprimirTitulo("REGISTRO DE ASISTENCIA");
      for (Estudiante est : alumnosSesion) {
        System.out.println("Alumno: " + est.getNombreCompleto() + " (" + est.getCodigo() + ")");
        int maxHoras = sesion.getHorasProgramadas();
        int horasAsistidas = leerOpcion("Horas asistidas (0 = ausente, máximo " + maxHoras + "): ");
        RegistroDeAsistencia registro = servicio.registrarAsistenciaSesion(sesion.getIdSesion(), est.getId(),
            horasAsistidas);
        if (registro != null) {
          registros.add(registro);
        }
      }
      servicio.guardarAsistencias(registros);
      System.out.println("¡Asistencia guardada exitosamente en registro_de_asistencia.csv!");
    }
  }

  /** Muestra directamente las secciones del docente pertenecientes al periodo activo. */
  private List<SeccionCurso> mostrarSeccionesPeriodoActual(Profesor prof, List<Curso> cursos) {
    PeriodoAcademico periodoActivo = servicio.listarPeriodosAcademicos().stream()
        .filter(periodo -> periodo.getEstado() == EstadoPeriodo.ACTIVO)
        .findFirst().orElse(null);
    if (periodoActivo == null) {
      return List.of();
    }

    System.out.println("Periodo activo: " + periodoActivo.getCodigo());
    List<SeccionCurso> secciones = new ArrayList<>();
    for (Curso curso : cursos) {
      for (SeccionCurso seccion : servicio.seccionesProfesor(prof, curso)) {
        BloqueSeccion bloque = servicio.buscarBloque(seccion.getIdBloque());
        if (bloque == null || !periodoActivo.getIdPeriodo().equals(bloque.getIdPeriodo())) {
          continue;
        }
        secciones.add(seccion);
        System.out.println(" " + secciones.size() + ". " + curso.getNombreCurso()
            + " [Sección: " + seccion.getIdSeccion() + "]");
        mostrarResumenSeccionAsignada(seccion);
      }
    }
    return secciones;
  }

  private void mostrarResumenSeccionAsignada(SeccionCurso seccion) {
    BloqueSeccion bloque = servicio.buscarBloque(seccion.getIdBloque());
    System.out.println("   - Ciclo correspondiente: " + (bloque != null ? bloque.getCiclo() : "No registrado"));
    System.out.println("   - Bloque de curso: " + seccion.getIdBloque());
    mostrarHorarioDeSeccion(seccion, "   - ", "      ");
    mostrarSesionesProgramadasHoy(seccion);
  }

  private void mostrarSesionesProgramadasHoy(SeccionCurso seccion) {
    String hoy = LocalDate.now().toString();
    for (SesionClase sesion : servicio.listarSesiones()) {
      if (!hoy.equals(sesion.getFecha()) || !sesionPerteneceASeccion(sesion, seccion)) {
        continue;
      }
      HorarioSeccion horario = servicio.listarHorariosSeccion(seccion.getIdSeccion()).stream()
          .filter(actual -> actual.getIdHorario().equals(sesion.getIdHorario())).findFirst().orElse(null);
      if (horario == null) {
        continue;
      }
      Salon salon = servicio.buscarSalon(horario.getIdSalon());
      String ambiente = salon != null ? salon.getNombre() : horario.getIdSalon();
      System.out.println("      - Sesión programada hoy: " + horario.getHoraInicio() + " - " + horario.getHoraFin()
          + " hrs (" + ambiente + ")");
    }
  }

  private PeriodoAcademico buscarPeriodo(String idPeriodo) {
    return servicio.listarPeriodosAcademicos().stream()
        .filter(periodo -> periodo.getIdPeriodo().equals(idPeriodo)).findFirst().orElse(null);
  }

  /**
   * Muestra ciclo, bloque, docentes y horario del curso seleccionado.
   * Un curso puede abrirse en mas de una seccion; se muestra una ficha por
   * seccion en la que el profesor dicta al menos una franja.
   */
  private void mostrarDetalleCurso(SeccionCurso seccion, Curso curso) {
    BloqueSeccion bloque = servicio.buscarBloque(seccion.getIdBloque());
    System.out.println("Curso: " + curso.getNombreCurso());
    System.out.println(" - Ciclo correspondiente: " + (bloque != null ? bloque.getCiclo() : "No registrado"));
    System.out.println(" - Bloque de curso: " + seccion.getIdBloque());
    mostrarSemanaActualCurso(bloque);
    mostrarDocentesDeSeccion(seccion);
    mostrarHorarioDeSeccion(seccion);
    System.out.println("Horas lectivas totales: " + curso.getTotalHorasLectivas() + " hrs");
    PeriodoAcademico periodo = bloque == null ? null : buscarPeriodo(bloque.getIdPeriodo());
    if (periodo != null && periodo.getSemanasLectivas() > 0) {
      System.out.printf("Horas semanales requeridas: %.2f hrs%n", periodo.calcularHorasSemanalesRequeridas(curso));
    }
  }

  /**
   * Muestra la semana lectiva vigente calculada desde el inicio del periodo.
   */
  private void mostrarSemanaActualCurso(BloqueSeccion bloque) {
    if (bloque == null) {
      System.out.println(" - Semana actual: No disponible (bloque no registrado)");
      return;
    }

    PeriodoAcademico periodo = servicio.listarPeriodosAcademicos().stream()
        .filter(p -> p.getIdPeriodo().equals(bloque.getIdPeriodo())).findFirst().orElse(null);
    if (periodo == null) {
      System.out.println(" - Semana actual: No disponible (periodo académico no registrado)");
      return;
    }

    int semana = servicio.calcularSemanaActualCurso(periodo, LocalDate.now());
    if (semana == 0) {
      System.out.println(" - Semana actual: No hay una semana de dictado vigente ("
          + periodo.getSemanasLectivas() + " semanas lectivas del periodo)");
      return;
    }
    System.out.println(" - Semana actual: Semana " + semana + " de " + periodo.getSemanasLectivas());
  }

  /**
   * Muestra los docentes de la seccion y su correo de contacto.
   */
  private void mostrarDocentesDeSeccion(SeccionCurso seccion) {
    List<Profesor> profesores = servicio.profesoresSeccion(seccion.getIdSeccion());
    if (profesores.isEmpty()) {
      System.out.println(" - Profesor(a): No asignado");
      return;
    }
    for (Profesor profesor : profesores) {
      System.out.println(" - Profesor(a): " + profesor.getNombreCompleto());
      System.out.println(" - Correo del profesor: " + profesor.getCorreo());
    }
  }

  /**
   * Muestra el horario semanal de la seccion con el ambiente de cada franja.
   */
  private void mostrarHorarioDeSeccion(SeccionCurso seccion) {
    mostrarHorarioDeSeccion(seccion, " - ", "   ");
  }

  private void mostrarHorarioDeSeccion(SeccionCurso seccion, String prefijo, String prefijoDetalle) {
    List<HorarioSeccion> horarios = servicio.listarHorariosSeccion(seccion.getIdSeccion());
    System.out.println(prefijo + "Horario de clases:");
    if (horarios.isEmpty()) {
      System.out.println(prefijoDetalle + "* Sin horario registrado");
      return;
    }
    for (HorarioSeccion horario : horarios) {
      Salon salon = servicio.buscarSalon(horario.getIdSalon());
      String ambiente = salon != null ? salon.getNombre() : horario.getIdSalon();
      DiaSemana diaActual = DiaSemana.values()[LocalDate.now().getDayOfWeek().getValue() - 1];
      String marcaHoy = horario.getDiaSemana() == diaActual
          ? " <-- ¡HOY!" : "";
      System.out.println(prefijoDetalle + "* " + horario.getDiaSemana().getNombre() + " " + horario.getHoraInicio() + " - "
          + horario.getHoraFin() + " hrs (" + ambiente + ")" + marcaHoy);
    }
  }

  private boolean sesionPerteneceASeccion(SesionClase sesion, SeccionCurso seccion) {
    return servicio.listarHorariosSeccion(seccion.getIdSeccion()).stream()
        .anyMatch(horario -> horario.getIdHorario().equals(sesion.getIdHorario()));
  }

  /**
   * Menu del estudiante: ver resumen de asistencia, ver perfil o cerrar sesión.
   */
  private void menuEstudiante(Estudiante estActual) {
    int opcEst = 0;
    do {
      imprimirTitulo("MENÚ ESTUDIANTE: " + estActual.getNombreCompleto());
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
  private void mostrarResumenAsistenciaEstudiante(Estudiante est) {
    List<RegistroDeAsistencia> misAsistencias = servicio.historialEstudiante(est);
    PeriodoAcademico periodoActivo = servicio.listarPeriodosAcademicos().stream()
        .filter(periodo -> periodo.getEstado() == EstadoPeriodo.ACTIVO)
        .findFirst().orElse(null);
    imprimirTitulo("RESUMEN DE ASISTENCIA Y CURSOS");
    if (periodoActivo == null) {
      System.out.println("No hay un periodo académico activo.");
      return;
    }

    Set<String> seccionesMostradas = new HashSet<>();
    for (Matricula matricula : servicio.listarMatriculasEstudiante(est.getId())) {
      if (matricula.getEstado() != EstadoMatricula.ACTIVA) {
        continue;
      }
      BloqueSeccion bloque = servicio.buscarBloque(matricula.getIdBloque());
      if (bloque == null || !periodoActivo.getIdPeriodo().equals(bloque.getIdPeriodo())) {
        continue;
      }

      for (SeccionCurso seccion : servicio.listarSeccionesBloque(bloque.getIdBloque())) {
        if (!seccionesMostradas.add(seccion.getIdSeccion())) {
          continue;
        }
        Curso curso = servicio.listarCursos().stream()
            .filter(actual -> actual.getIdCurso().equals(seccion.getIdCurso()))
            .findFirst().orElse(null);
        if (curso == null) {
          continue;
        }

        int horasDictadas = 0;
        int horasAsistidas = 0;
        for (RegistroDeAsistencia registro : misAsistencias) {
          if (registro.getIdCurso().equals(curso.getIdCurso())
              && matricula.getIdMatricula().equals(registro.getIdMatricula())) {
            horasDictadas += servicio.horasProgramadasDe(registro);
            if (registro.isPresente()) {
              horasAsistidas += registro.getHorasAsistidas();
            }
          }
        }

        System.out.println("Periodo: " + periodoActivo.getCodigo());
        System.out.println("Curso: " + curso.getNombreCurso());
        System.out.println(" - Bloque: " + bloque.getNombre() + " [" + bloque.getIdBloque() + "]");
        List<Profesor> profesores = servicio.profesoresSeccion(seccion.getIdSeccion());
        if (profesores.isEmpty()) {
          System.out.println(" - Profesor: No asignado");
          System.out.println(" - Correo: No registrado");
        } else {
          for (Profesor profesor : profesores) {
            System.out.println(" - Profesor: " + profesor.getNombreCompleto());
            System.out.println(" - Correo: " + profesor.getCorreo());
          }
        }
        mostrarHorarioDeSeccion(seccion);
        double porcentajeFaltas = servicio.calcularPorcentajeFaltas(horasDictadas, horasAsistidas);
        System.out.printf(" - Porcentaje de inasistencia (Faltas): %.2f%%\n", porcentajeFaltas);
        System.out.println("----------------------------------------------");
      }
    }

    if (seccionesMostradas.isEmpty()) {
      System.out.println("No hay cursos matriculados en el periodo activo.");
    }
  }

  private void imprimirTitulo(String titulo) {
    int ancho = Math.max(ANCHO_TITULO, titulo.length() + 4);
    int espaciosIniciales = (ancho - titulo.length()) / 2;
    String borde = "=".repeat(ancho);
    System.out.println("\n" + borde);
    System.out.println(" ".repeat(espaciosIniciales) + titulo);
    System.out.println(borde);
  }
}
