package app;

import java.io.Console;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

import almacenamiento.csv.RepositorioAsistenciaCsv;
import almacenamiento.csv.RepositorioUsuarioCsv;
import modelo.BloqueSeccion;
import modelo.Carrera;
import modelo.Curso;
import modelo.DiaSemana;
import modelo.EstadoMatricula;
import modelo.Estudiante;
import modelo.HorarioSeccion;
import modelo.Matricula;
import modelo.PeriodoAcademico;
import modelo.Profesor;
import modelo.RegistroDeAsistencia;
import modelo.Salon;
import modelo.SeccionCurso;
import modelo.SesionClase;
import modelo.Usuario;
import servicio.AutenticacionService;
import servicio.ServicioAsistencia;

/**
 * Aplicacion de terminal para el control de asistencia de clases.
 * Orquesta el inicio de sesion, los menus por rol y delega la logica a la capa de servicio.
 */
public class App {
  private static final int ANCHO_TITULO = 46;
  private final Scanner scanner = new Scanner(System.in);
  private final ServicioAsistencia servicioAsistencia = new ServicioAsistencia(new RepositorioAsistenciaCsv());
  private final AutenticacionService autenticacion = new AutenticacionService(new RepositorioUsuarioCsv());

  public static void main(String[] args) {
    new App().iniciar();
  }

  public void iniciar() {
    int opcion;
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
    switch (usuario.getRol()) {
      case PROFESOR -> {
        Profesor profesor = servicioAsistencia.buscarProfesor(usuario.getIdReferencia());
        if (profesor == null) {
          System.out.println("No se encontró el perfil del profesor asociado a la cuenta.");
          return;
        }
        System.out.println("Bienvenido, " + profesor.getNombreCompleto() + ".");
        menuProfesor(profesor);
      }
      case ESTUDIANTE -> {
        Estudiante estudiante = servicioAsistencia.buscarEstudiante(usuario.getIdReferencia());
        if (estudiante == null) {
          System.out.println("No se encontró el perfil del estudiante asociado a la cuenta.");
          return;
        }
        System.out.println("Bienvenido, " + estudiante.getNombreCompleto() + ".");
        menuEstudiante(estudiante);
      }
    }
    System.out.println("Sesión cerrada.");
  }

  private String leerContrasena(String mensaje) {
    System.out.print(mensaje);
    Console console = System.console();
    if (console != null) {
      char[] caracteres = console.readPassword();
      return caracteres == null ? "" : new String(caracteres);
    }
    return scanner.nextLine();
  }

  private void menuProfesor(Profesor profActual) {
    int opcProf;
    do {
      imprimirTitulo("MENÚ PROFESOR: " + profActual.getNombreCompleto());
      System.out.println("1. Ver mis cursos asignados");
      System.out.println("2. Ver mi perfil");
      System.out.println("3. Cerrar sesión");
      opcProf = leerOpcion("Elija una opción: ");
      switch (opcProf) {
        case 1 -> gestionarCursosProfesor(profActual);
        case 2 -> mostrarPerfilProfesor(profActual);
        case 3 -> {
        }
        default -> System.out.println("Opción inválida.");
      }
    } while (opcProf != 3);
  }

  private void gestionarCursosProfesor(Profesor prof) {
    List<Curso> misCursos = servicioAsistencia.listarCursosDeProfesor(prof);
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
    String carreraCurso = mostrarDetalleCurso(seccionSeleccionada, cursoSel);

    List<Estudiante> alumnosCurso = servicioAsistencia.listarEstudiantesDeCursoEnBloque(cursoSel,
        seccionSeleccionada.getIdBloque());
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
      System.out.println("    - Carrera: " + carreraCurso);
    }

    List<SesionClase> sesionesHoy = servicioAsistencia.listarSesionesDeSeccionEnFecha(
        seccionSeleccionada.getIdSeccion(), LocalDate.now().toString());
    if (sesionesHoy.isEmpty()) {
      System.out.println("No hay una sesión programada para este curso en la fecha de hoy.");
      System.out.println("0. Regresar al menú anterior");
      leerOpcion("Seleccione una opción: ");
      return;
    }

    if (leerOpcion("\n¿Desea tomar asistencia para este curso hoy? (1. Sí / 2. No): ") == 1) {
      registrarAsistenciaDeSesionHoy(sesionesHoy);
    }
  }

  private void registrarAsistenciaDeSesionHoy(List<SesionClase> sesionesHoy) {
    SesionClase sesion = sesionesHoy.get(0);
    if (sesionesHoy.size() > 1) {
      System.out.println("Sesiones programadas para hoy:");
      for (int i = 0; i < sesionesHoy.size(); i++) {
        SesionClase opcion = sesionesHoy.get(i);
        System.out.println((i + 1) + ". " + opcion.getIdSesion() + " (" + opcion.getHorasProgramadas() + " horas)");
      }
      int seleccion = leerOpcion("Seleccione una sesión: ") - 1;
      if (seleccion < 0 || seleccion >= sesionesHoy.size()) {
        System.out.println("Sesión no válida.");
        return;
      }
      sesion = sesionesHoy.get(seleccion);
    }

    List<Estudiante> alumnosSesion = servicioAsistencia.listarEstudiantesDeSesion(sesion.getIdSesion());
    if (alumnosSesion.isEmpty()) {
      System.out.println("No hay alumnos matriculados en el bloque de esta sesión.");
      return;
    }

    List<RegistroDeAsistencia> registros = new ArrayList<>();
    imprimirTitulo("REGISTRO DE ASISTENCIA");
    for (Estudiante est : alumnosSesion) {
      System.out.println("Alumno: " + est.getNombreCompleto() + " (" + est.getCodigo() + ")");
      int horas = leerOpcion("Horas asistidas (0 = ausente, máximo " + sesion.getHorasProgramadas() + "): ");
      RegistroDeAsistencia registro = servicioAsistencia.registrar(sesion.getIdSesion(), est.getId(), horas);
      if (registro != null) {
        registros.add(registro);
      }
    }
    servicioAsistencia.guardarLote(registros);
    System.out.println("¡Asistencia guardada exitosamente en registro_de_asistencia.csv!");
  }

  private List<SeccionCurso> mostrarSeccionesPeriodoActual(Profesor prof, List<Curso> cursos) {
    PeriodoAcademico periodoActivo = servicioAsistencia.buscarPeriodoActivo();
    if (periodoActivo == null) {
      return List.of();
    }

    System.out.println("Periodo activo: " + periodoActivo.getCodigo());
    List<SeccionCurso> secciones = new ArrayList<>();
    for (Curso curso : cursos) {
      for (SeccionCurso seccion : servicioAsistencia.listarSeccionesDeProfesor(prof, curso)) {
        BloqueSeccion bloque = servicioAsistencia.buscarBloque(seccion.getIdBloque());
        if (bloque == null || !periodoActivo.getIdPeriodo().equals(bloque.getIdPeriodo())) {
          continue;
        }
        secciones.add(seccion);
        System.out.println(" " + secciones.size() + ". " + curso.getNombreCurso()
            + " [Sección: " + seccion.getIdSeccion() + "]");
        System.out.println("   - Ciclo correspondiente: " + bloque.getCiclo());
        System.out.println("   - Bloque de curso: " + seccion.getIdBloque());
        mostrarHorarioDeSeccion(seccion, "   - ", "      ");
        mostrarSesionesProgramadasHoy(seccion);
      }
    }
    return secciones;
  }

  private void mostrarSesionesProgramadasHoy(SeccionCurso seccion) {
    List<HorarioSeccion> horarios = servicioAsistencia.listarHorariosDeSeccion(seccion.getIdSeccion());
    for (SesionClase sesion : servicioAsistencia.listarSesionesDeSeccionEnFecha(
        seccion.getIdSeccion(), LocalDate.now().toString())) {
      horarios.stream()
          .filter(h -> h.getIdHorario().equals(sesion.getIdHorario()))
          .findFirst()
          .ifPresent(h -> System.out.println("      - Sesión programada hoy: " + h.getHoraInicio() + " - "
              + h.getHoraFin() + " hrs (" + nombreAmbiente(h.getIdSalon()) + ")"));
    }
  }

  private String mostrarDetalleCurso(SeccionCurso seccion, Curso curso) {
    BloqueSeccion bloque = servicioAsistencia.buscarBloque(seccion.getIdBloque());
    Carrera carreraCurso = servicioAsistencia.buscarCarreraDeCurso(curso);
    String carrera = carreraCurso == null ? "No registrada" : carreraCurso.getNombre();
    PeriodoAcademico periodo = bloque == null ? null : servicioAsistencia.buscarPeriodo(bloque.getIdPeriodo());

    System.out.println("Curso: " + curso.getNombreCurso());
    System.out.println(" - Carrera del plan: " + carrera);
    System.out.println(" - Ciclo correspondiente: " + (bloque != null ? bloque.getCiclo() : "No registrado"));
    System.out.println(" - Bloque de curso: " + seccion.getIdBloque());
    mostrarSemanaActualCurso(bloque, periodo);
    mostrarDocentesDeSeccion(seccion.getIdSeccion(), "Profesor(a)", "Correo del profesor", "No asignado");
    mostrarHorarioDeSeccion(seccion, " - ", "   ");
    System.out.println("Horas lectivas totales: " + curso.getTotalHorasLectivas() + " hrs");
    if (periodo != null && periodo.getSemanasLectivas() > 0) {
      System.out.printf("Horas semanales requeridas: %.2f hrs%n", periodo.calcularHorasSemanalesRequeridas(curso));
    }
    return carrera;
  }

  private void mostrarSemanaActualCurso(BloqueSeccion bloque, PeriodoAcademico periodo) {
    if (bloque == null) {
      System.out.println(" - Semana actual: No disponible (bloque no registrado)");
      return;
    }
    if (periodo == null) {
      System.out.println(" - Semana actual: No disponible (periodo académico no registrado)");
      return;
    }
    int semana = servicioAsistencia.calcularSemanaLectiva(periodo, LocalDate.now());
    if (semana == 0) {
      System.out.println(" - Semana actual: No hay una semana de dictado vigente ("
          + periodo.getSemanasLectivas() + " semanas lectivas del periodo)");
      return;
    }
    System.out.println(" - Semana actual: Semana " + semana + " de " + periodo.getSemanasLectivas());
  }

  private void mostrarDocentesDeSeccion(String idSeccion, String etiquetaDocente, String etiquetaCorreo,
      String sinCorreo) {
    List<Profesor> profesores = servicioAsistencia.listarProfesoresDeSeccion(idSeccion);
    if (profesores.isEmpty()) {
      System.out.println(" - " + etiquetaDocente + ": No asignado");
      if (sinCorreo != null) {
        System.out.println(" - " + etiquetaCorreo + ": " + sinCorreo);
      }
      return;
    }
    for (Profesor profesor : profesores) {
      System.out.println(" - " + etiquetaDocente + ": " + profesor.getNombreCompleto());
      System.out.println(" - " + etiquetaCorreo + ": " + profesor.getCorreo());
    }
  }

  private void mostrarHorarioDeSeccion(SeccionCurso seccion, String prefijo, String prefijoDetalle) {
    List<HorarioSeccion> horarios = servicioAsistencia.listarHorariosDeSeccion(seccion.getIdSeccion());
    System.out.println(prefijo + "Horario de clases:");
    if (horarios.isEmpty()) {
      System.out.println(prefijoDetalle + "* Sin horario registrado");
      return;
    }
    DiaSemana diaActual = DiaSemana.values()[LocalDate.now().getDayOfWeek().getValue() - 1];
    for (HorarioSeccion horario : horarios) {
      String marcaHoy = horario.getDiaSemana() == diaActual ? " <-- ¡HOY!" : "";
      System.out.println(prefijoDetalle + "* " + horario.getDiaSemana().getNombre() + " " + horario.getHoraInicio()
          + " - " + horario.getHoraFin() + " hrs (" + nombreAmbiente(horario.getIdSalon()) + ")" + marcaHoy);
    }
  }

  private String nombreAmbiente(String idSalon) {
    Salon salon = servicioAsistencia.buscarSalon(idSalon);
    return salon != null ? salon.getNombre() : idSalon;
  }

  private void menuEstudiante(Estudiante estActual) {
    int opcEst;
    do {
      imprimirTitulo("MENÚ ESTUDIANTE: " + estActual.getNombreCompleto());
      System.out.println("1. Ver cursos asignados y estado de asistencia");
      System.out.println("2. Ver mi perfil");
      System.out.println("3. Cerrar sesión");
      opcEst = leerOpcion("Elija una opción: ");
      switch (opcEst) {
        case 1 -> mostrarResumenAsistenciaEstudiante(estActual);
        case 2 -> mostrarPerfilEstudiante(estActual);
        case 3 -> {
        }
        default -> System.out.println("Opción inválida.");
      }
    } while (opcEst != 3);
  }

  private void mostrarResumenAsistenciaEstudiante(Estudiante est) {
    List<RegistroDeAsistencia> misAsistencias = servicioAsistencia.listarPorEstudiante(est.getId());
    PeriodoAcademico periodoActivo = servicioAsistencia.buscarPeriodoActivo();
    imprimirTitulo("RESUMEN DE ASISTENCIA Y CURSOS");
    if (periodoActivo == null) {
      System.out.println("No hay un periodo académico activo.");
      return;
    }

    Set<String> seccionesMostradas = new HashSet<>();
    for (Matricula matricula : servicioAsistencia.listarMatriculasDeEstudiante(est.getId())) {
      if (matricula.getEstado() != EstadoMatricula.ACTIVA) {
        continue;
      }
      BloqueSeccion bloque = servicioAsistencia.buscarBloque(matricula.getIdBloque());
      if (bloque == null || !periodoActivo.getIdPeriodo().equals(bloque.getIdPeriodo())) {
        continue;
      }

      for (SeccionCurso seccion : servicioAsistencia.listarSeccionesDeBloque(bloque.getIdBloque())) {
        if (!seccionesMostradas.add(seccion.getIdSeccion())) {
          continue;
        }
        Curso curso = servicioAsistencia.buscarCurso(seccion.getIdCurso());
        if (curso == null) {
          continue;
        }

        System.out.println("Periodo: " + periodoActivo.getCodigo());
        System.out.println("Curso: " + curso.getNombreCurso());
        System.out.println(" - Bloque: " + bloque.getNombre() + " [" + bloque.getIdBloque() + "]");
        mostrarDocentesDeSeccion(seccion.getIdSeccion(), "Profesor", "Correo", "No registrado");
        mostrarHorarioDeSeccion(seccion, " - ", "   ");
        double faltas = servicioAsistencia.calcularPorcentajeFaltasDeCurso(
            misAsistencias, curso.getIdCurso(), matricula.getIdMatricula());
        System.out.printf(" - Porcentaje de inasistencia (Faltas): %.2f%%%n", faltas);
        System.out.println("----------------------------------------------");
      }
    }

    if (seccionesMostradas.isEmpty()) {
      System.out.println("No hay cursos matriculados en el periodo activo.");
    }
  }

  private void mostrarPerfilProfesor(Profesor profesor) {
    imprimirTitulo("PERFIL DEL PROFESOR");
    System.out.println("ID: " + profesor.getId());
    System.out.println("Nombre: " + profesor.getNombres());
    System.out.println("Apellido: " + profesor.getApellidos());
    System.out.println("Correo: " + profesor.getCorreo());
    System.out.println("Telefono: " + profesor.getTelefono());
    System.out.println("Tipo de documento: " + profesor.getTipoDeDocumento());
    System.out.println("Nro de documento: " + profesor.getNroDocumento());
    System.out.println("Departamento academico: " + profesor.getDepartamentoAcademico());
    System.out.println("Especialidad: " + profesor.getEspecialidad());
  }

  private void mostrarPerfilEstudiante(Estudiante estudiante) {
    imprimirTitulo("PERFIL DEL ESTUDIANTE");
    System.out.println("ID: " + estudiante.getId());
    System.out.println("Codigo: " + estudiante.getCodigo());
    System.out.println("Nombres: " + estudiante.getNombres());
    System.out.println("Apellidos: " + estudiante.getApellidos());
    System.out.println("DNI: " + estudiante.getDni());
    System.out.println("Correo: " + estudiante.getCorreo());
    System.out.println("Estado: " + estudiante.getEstado());
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
