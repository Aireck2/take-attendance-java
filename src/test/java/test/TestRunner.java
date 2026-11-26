package test;

import java.util.List;
import java.util.Optional;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;

import app.App;
import almacenamiento.csv.LectorCsvTest;
import almacenamiento.csv.RepositorioAsistenciaCsv;
import modelo.Curso;
import modelo.EstadoPeriodo;
import modelo.Estudiante;
import modelo.HorarioSeccion;
import modelo.PeriodoAcademico;
import modelo.PersonaTest;
import modelo.Profesor;
import modelo.RegistroDeAsistencia;
import modelo.Rol;
import modelo.SeccionCurso;
import modelo.SesionClase;
import modelo.Usuario;
import security.PasswordHasher;
import servicio.AutenticacionService;
import servicio.ServicioAsistencia;

/**
 * Pruebas del flujo principal: acceso docente hasta la creación de asistencia.
 */
public final class TestRunner {
    private static int total;
    private static int fallos;

    private TestRunner() {
    }

    public static void main(String[] args) {
        LectorCsvTest.ejecutar();
        PersonaTest.ejecutar();
        probarAutenticacionConRepositorioInyectado();
        probarInicioSesionYResolucionDePerfil();
        probarRecorridoAcademicoHastaRegistro();
        probarCalculosYCreacionEnServicioAsistencia();
        probarCarreraEnListaDeAlumnosDelCurso();
        if (fallos == 0) {
            System.out.println("OK: " + total + " pruebas del flujo de asistencia pasaron.");
        } else {
            System.out.println("FALLARON " + fallos + " de " + total + " pruebas del flujo de asistencia.");
            System.exit(1);
        }
    }

    private static void probarAutenticacionConRepositorioInyectado() {
        Usuario usuario = new Usuario("prueba@ejemplo.test", PasswordHasher.hash("clave"),
                Rol.PROFESOR, "DOC-TEST", true);
        AutenticacionService servicio = new AutenticacionService(correo -> {
            verificar(correo.equals("prueba@ejemplo.test"), "normaliza el correo antes de consultar el repositorio");
            return Optional.of(usuario);
        });
        verificar(servicio.autenticar(" Prueba@Ejemplo.Test ", "clave") == usuario,
                "autentica usando el repositorio inyectado");
        verificar(servicio.autenticar("prueba@ejemplo.test", "incorrecta") == null,
                "rechaza una contraseña incorrecta para usuario activo");

        AutenticacionService sinUsuario = new AutenticacionService(correo -> Optional.empty());
        verificar(sinUsuario.autenticar("ausente@ejemplo.test", "clave") == null,
                "rechaza las credenciales de usuario inexistente");

        Usuario inactivo = new Usuario("inactivo@ejemplo.test", PasswordHasher.hash("clave"),
                Rol.PROFESOR, "DOC-INACTIVO", false);
        AutenticacionService servicioInactivo = new AutenticacionService(correo -> Optional.of(inactivo));
        verificar(servicioInactivo.autenticar("inactivo@ejemplo.test", "clave") == null,
                "rechaza el usuario inactivo aunque la contraseña coincida");
    }

    private static void probarInicioSesionYResolucionDePerfil() {
        Usuario usuario = new AutenticacionService().autenticar("carlos.mendoza@iestpchincha.edu.pe", "Docente2026*");
        verificar(usuario != null, "autentica al docente de demostración");
        if (usuario == null)
            return;
        ServicioAsistencia servicio = new ServicioAsistencia(new RepositorioAsistenciaCsv());
        Profesor profesor = servicio.buscarProfesor(usuario.getIdReferencia());
        verificar(profesor != null && profesor.getId().equals("DOC-001"), "resuelve el perfil docente autenticado");
    }

    private static void probarRecorridoAcademicoHastaRegistro() {
        ServicioAsistencia servicio = new ServicioAsistencia(new RepositorioAsistenciaCsv());
        Profesor profesor = servicio.buscarProfesor("DOC-001");
        verificar(profesor != null, "encuentra al docente asignado");
        if (profesor == null)
            return;

        List<Curso> cursos = servicio.listarCursosDeProfesor(profesor);
        verificar(!cursos.isEmpty(), "encuentra cursos asignados mediante horarios");
        Curso curso = cursos.stream().filter(item -> servicio.buscarCarreraDeCurso(item) != null)
                .findFirst().orElse(null);
        verificar(curso != null, "resuelve carrera a través de curso, módulo y plan");
        if (curso == null)
            return;
        verificar(servicio.buscarCarreraDeCurso(curso).getNombre() != null,
                "la carrera curricular tiene nombre para mostrar");

        SeccionCurso seccion = servicio.listarSeccionesDeProfesor(profesor, curso).stream().findFirst().orElse(null);
        verificar(seccion != null, "encuentra la sección asignada al docente");
        if (seccion == null)
            return;
        List<String> horarios = servicio.listarHorariosDeSeccion(seccion.getIdSeccion()).stream()
                .map(HorarioSeccion::getIdHorario).toList();
        SesionClase sesion = servicio.listarTodasLasSesiones().stream()
                .filter(item -> horarios.contains(item.getIdHorario())).findFirst().orElse(null);
        verificar(sesion != null, "resuelve una sesión desde el horario de la sección");
        if (sesion == null)
            return;

        List<Estudiante> estudiantes = servicio.listarEstudiantesDeSesion(sesion.getIdSesion());
        verificar(!estudiantes.isEmpty(), "lista alumnos con matrícula activa en la sesión");
        if (estudiantes.isEmpty())
            return;
        RegistroDeAsistencia registro = servicio.registrar(sesion.getIdSesion(), estudiantes.get(0).getId(),
                sesion.getHorasProgramadas() + 5);
        verificar(registro != null, "crea el registro para un alumno matriculado");
        verificar(registro != null && registro.getIdCurso().equals(curso.getIdCurso()),
                "el registro conserva el curso resuelto desde el plan");
        verificar(registro != null && registro.getIdMatricula() != null && !registro.getIdMatricula().isEmpty(),
                "el registro queda vinculado a la matrícula activa");
        verificar(registro != null && registro.getHorasAsistidas() == sesion.getHorasProgramadas(),
                "normaliza las horas al máximo permitido por la sesión");
        verificar(registro != null && servicio.horasProgramadasDe(registro) == sesion.getHorasProgramadas(),
                "resuelve las horas dictadas de la sesión para el resumen del estudiante");
    }

    private static void probarCalculosYCreacionEnServicioAsistencia() {
        ServicioAsistencia servicio = new ServicioAsistencia(new RepositorioAsistenciaCsv());
        PeriodoAcademico periodo = new PeriodoAcademico("PER-1", "2026-II", "2026-08-01", "2026-12-15",
                EstadoPeriodo.ACTIVO, 16);
        verificar(servicio.calcularSemanaLectiva(periodo, LocalDate.of(2026, 8, 1)) == 1,
                "calcula la primera semana lectiva al inicio del periodo");
        verificar(servicio.calcularSemanaLectiva(periodo, LocalDate.of(2026, 8, 8)) == 2,
                "calcula la segunda semana lectiva tras siete días");
        verificar(servicio.calcularSemanaLectiva(periodo, LocalDate.of(2026, 7, 31)) == 0,
                "devuelve cero cuando la fecha es previa al periodo");
        verificar(servicio.calcularPorcentajeFaltas(10, 8) == 20.0,
                "calcula el porcentaje de faltas sobre horas dictadas");
    }

    private static void probarCarreraEnListaDeAlumnosDelCurso() {
        var entradaOriginal = System.in;
        var salidaOriginal = System.out;
        ByteArrayOutputStream salida = new ByteArrayOutputStream();
        String entrada = String.join("\n", "1", "carlos.mendoza@iestpchincha.edu.pe", "Docente2026*", "1", "1",
                "2", "3", "2", "");
        try {
            System.setIn(new ByteArrayInputStream(entrada.getBytes(StandardCharsets.UTF_8)));
            System.setOut(new PrintStream(salida, true, StandardCharsets.UTF_8));
            new App().iniciar();
        } finally {
            System.setIn(entradaOriginal);
            System.setOut(salidaOriginal);
        }

        String resultado = salida.toString(StandardCharsets.UTF_8);
        long alumnos = resultado.lines().filter(linea -> linea.contains("- Código:")).count();
        long carreras = resultado.lines().filter(linea -> linea.contains("- Carrera:")).count();
        verificar(alumnos > 0 && carreras == alumnos,
                "muestra la carrera junto a cada alumno matriculado en el detalle del curso");
    }

    private static void verificar(boolean condicion, String descripcion) {
        total++;
        if (!condicion) {
            fallos++;
            System.out.println("FALLO: " + descripcion);
        }
    }
}
