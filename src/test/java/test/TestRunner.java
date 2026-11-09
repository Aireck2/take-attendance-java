package test;

import java.util.List;

import models.Carrera;
import models.Curso;
import models.Estudiante;
import models.EstadoEstudiante;
import models.EstadoPeriodo;
import models.EstadoPlan;
import models.ModuloProfesional;
import models.PeriodoAcademico;
import models.PlanEstudios;
import models.Profesor;
import models.RegistroDeAsistencia;
import models.Rol;
import models.Usuario;
import security.PasswordHasher;
import service.AutenticacionService;
import service.ServicioAcademico;
import storage.ArchivoManager;

/**
 * Suite de pruebas del sistema, sin dependencias externas.
 * Se ejecuta con: java -cp out test.TestRunner
 */
public class TestRunner {
  private static int total = 0;
  private static int fallos = 0;

  public static void main(String[] args) {
    probarHashDeContrasenas();
    probarLecturaDeUsuarios();
    probarInicioDeSesion();
    probarPerfilesVinculados();
    probarRegistroDeAsistencia();
    probarFundacionesAcademicas();
    probarUnidadDidactica();

    System.out.println();
    if (fallos == 0) {
      System.out.println("OK: " + total + " pruebas pasaron.");
    } else {
      System.out.println("FALLARON " + fallos + " de " + total + " pruebas.");
      System.exit(1);
    }
  }

  private static void probarHashDeContrasenas() {
    String hash1 = PasswordHasher.hash("secreto");
    String hash2 = PasswordHasher.hash("secreto");

    afirmar(hash1.startsWith("pbkdf2_sha256$"), "el hash usa el prefijo pbkdf2_sha256");
    afirmar(!hash1.equals(hash2), "dos hashes de la misma contrasena usan sales distintas");
    afirmar(PasswordHasher.verificar("secreto", hash1), "verifica la contrasena correcta");
    afirmar(!PasswordHasher.verificar("otra", hash1), "rechaza la contrasena incorrecta");
    afirmar(!PasswordHasher.verificar("secreto", "formato-invalido"), "rechaza un hash con formato invalido");
    afirmar(!PasswordHasher.verificar("secreto", null), "rechaza un hash nulo");
  }

  private static void probarLecturaDeUsuarios() {
    List<Usuario> usuarios = ArchivoManager.leerUsuarios();

    afirmar(usuarios.size() == 10, "lee los 10 usuarios sembrados (encontrados: " + usuarios.size() + ")");
    afirmar(usuarios.stream().allMatch(u -> u.getHashContrasena().startsWith("pbkdf2_sha256$")),
        "ningun usuario guarda la contrasena en texto plano");
  }

  private static void probarInicioDeSesion() {
    AutenticacionService auth = new AutenticacionService();

    Usuario docente = auth.autenticar("carlos.mendoza@usil.pe", "Docente2026*");
    afirmar(docente != null, "autentica al docente con credenciales correctas");
    afirmar(docente != null && docente.getRol() == Rol.PROFESOR, "el docente recibe el rol PROFESOR");

    Usuario alumno = auth.autenticar("  ERICK.ESCRIBA@IESTP.PE  ", "Alumno2026*");
    afirmar(alumno != null, "normaliza el correo (mayusculas y espacios)");
    afirmar(alumno != null && alumno.getRol() == Rol.ESTUDIANTE, "el alumno recibe el rol ESTUDIANTE");

    afirmar(auth.autenticar("carlos.mendoza@usil.pe", "incorrecta") == null, "rechaza una contrasena incorrecta");
    afirmar(auth.autenticar("no.existe@iestp.pe", "Docente2026*") == null, "rechaza un correo desconocido");
    afirmar(auth.autenticar("", "") == null, "rechaza credenciales vacias");
  }

  private static void probarPerfilesVinculados() {
    ServicioAcademico servicio = new ServicioAcademico();

    Profesor profesor = servicio.buscarProfesor("P1");
    Estudiante estudiante = servicio.buscarEstudiante("E1");

    afirmar(profesor != null && profesor.getNombreCompleto().equals("Carlos Mendoza"),
        "resuelve el perfil del docente por id_referencia");
    afirmar(estudiante != null && estudiante.getNombreCompleto().equals("Erick Escriba"),
        "resuelve el perfil del estudiante por id_referencia");
  }

  private static void probarRegistroDeAsistencia() {
    ServicioAcademico servicio = new ServicioAcademico();
    Curso curso = new Curso("C-TEST", "Curso de prueba", 4, "P1", 16);
    Estudiante estudiante = new Estudiante("E-TEST", "U-TEST", "Ana", "Prueba", "ana@test.pe", "Sistemas",
        EstadoEstudiante.REGULAR);

    RegistroDeAsistencia presente = servicio.crearRegistro(curso, estudiante, "2026-11-09", 3);
    afirmar(presente.isPresente(), "marca presente cuando hay horas asistidas");
    afirmar(presente.getHorasAsistidas() == 3, "conserva las horas asistidas");

    RegistroDeAsistencia ausente = servicio.crearRegistro(curso, estudiante, "2026-11-10", 0);
    afirmar(!ausente.isPresente(), "marca ausente cuando las horas son 0");
    afirmar(ausente.getHorasAsistidas() == 0, "guarda 0 horas cuando esta ausente");

    RegistroDeAsistencia tope = servicio.crearRegistro(curso, estudiante, "2026-11-11", 99);
    afirmar(tope.getHorasAsistidas() == 4, "limita las horas al maximo de la sesion");

    RegistroDeAsistencia negativo = servicio.crearRegistro(curso, estudiante, "2026-11-12", -2);
    afirmar(negativo.getHorasAsistidas() == 0 && !negativo.isPresente(),
        "normaliza horas negativas a 0 y marca ausente");
  }

  private static void probarFundacionesAcademicas() {
    ServicioAcademico servicio = new ServicioAcademico();

    List<Carrera> carreras = servicio.listarCarreras();
    afirmar(carreras.size() == 1, "lee la carrera sembrada (encontradas: " + carreras.size() + ")");
    afirmar(!carreras.isEmpty() && carreras.get(0).getNombre().equals("Desarrollo de Sistemas de Informacion"),
        "conserva el nombre de la carrera");

    List<PlanEstudios> planes = servicio.listarPlanesEstudios();
    afirmar(planes.size() == 2, "lee los planes vigente e historico");
    afirmar(!planes.isEmpty() && planes.get(0).getIdCarrera().equals("K1"), "el plan pertenece a la carrera");
    afirmar(planes.stream().anyMatch(p -> p.getEstado() == EstadoPlan.VIGENTE), "marca el plan vigente");
    afirmar(planes.stream().anyMatch(p -> p.getEstado() == EstadoPlan.HISTORICO), "conserva el plan historico");

    List<ModuloProfesional> modulos = servicio.listarModulosProfesionales();
    afirmar(modulos.size() == 3, "lee los modulos profesionales");
    afirmar(modulos.stream().allMatch(m -> m.getIdPlan().equals("PL1")), "todos los modulos pertenecen al plan");

    List<PeriodoAcademico> periodos = servicio.listarPeriodosAcademicos();
    afirmar(periodos.size() == 2, "lee los periodos academicos");
    afirmar(!periodos.isEmpty() && periodos.get(0).getCodigo().equals("2026-I"), "conserva el codigo del periodo");
    afirmar(!periodos.isEmpty() && periodos.get(0).getEstado() == EstadoPeriodo.ACTIVO, "marca el periodo activo");
  }

  private static void probarUnidadDidactica() {
    List<Curso> cursos = ArchivoManager.leerCursos();
    afirmar(cursos.size() == 5, "lee los 5 cursos sembrados (encontrados: " + cursos.size() + ")");

    Curso c1 = cursos.stream().filter(c -> c.getIdCurso().equals("C1")).findFirst().orElse(null);
    afirmar(c1 != null, "encuentra el curso C1");
    afirmar(c1 != null && c1.getIdModulo().equals("M1"), "el curso pertenece a un modulo profesional");
    afirmar(c1 != null && c1.getCreditos() == 4, "conserva los creditos del curso");
    afirmar(c1 != null && c1.getHorasLectivasTotales() == 64, "suma las horas teoricas y practicas");

    Curso legado = new Curso("C-OLD", "Curso legado", 4, "P1", 16);
    afirmar(legado.getIdModulo().isEmpty(), "un curso legado no declara modulo");
    afirmar(legado.getCreditos() == 0, "un curso legado no declara creditos");
    afirmar(legado.getHorasLectivasTotales() == 64, "deriva las horas lectivas de duracion por horas de sesion");
  }

  private static void afirmar(boolean condicion, String descripcion) {
    total++;
    if (!condicion) {
      fallos++;
      System.out.println("FALLO: " + descripcion);
    }
  }
}
