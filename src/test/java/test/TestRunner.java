package test;

import java.util.List;

import models.BloqueSeccion;
import models.Carrera;
import models.ComprobantePago;
import models.Curso;
import models.DerechoPago;
import models.DiaSemana;
import models.Estudiante;
import models.EstadoComprobante;
import models.EstadoEstudiante;
import models.EstadoMatricula;
import models.EstadoPeriodo;
import models.EstadoPlan;
import models.HorarioSeccion;
import models.IndicadorLogro;
import models.Matricula;
import models.MenuSesion;
import models.ModalidadDictado;
import models.ModuloProfesional;
import models.PeriodoAcademico;
import models.PlanEstudios;
import models.Profesor;
import models.RegistroDeAsistencia;
import models.RegistroNota;
import models.Rol;
import models.Salon;
import models.SeccionCurso;
import models.SesionClase;
import models.TipoClase;
import models.TipoSalon;
import models.Turno;
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
    probarRedireccionPorRol();
    probarPorcentajeDeInasistencia();
    probarPerfilesVinculados();
    probarRegistroDeAsistencia();
    probarFundacionesAcademicas();
    probarUnidadDidactica();
    probarOfertaAcademica();
    probarMatriculaYReglaNormativa();
    probarEvaluacionYFinanzas();
    probarCursosDeProfesor();
    probarNominaDesdeMatricula();
    probarDatasetEjemploInstitucional();

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
    afirmar(usuarios.stream().allMatch(u -> u.getId().startsWith("USR-")),
        "todos los usuarios usan la nomenclatura USR-");
  }

  private static void probarInicioDeSesion() {
    AutenticacionService auth = new AutenticacionService();

    Usuario docente = auth.autenticar("carlos.mendoza@iestpchincha.edu.pe", "Docente2026*");
    afirmar(docente != null, "autentica al docente con credenciales correctas");
    afirmar(docente != null && docente.getRol() == Rol.PROFESOR, "el docente recibe el rol PROFESOR");

    Usuario alumno = auth.autenticar("  ERICK.ESCRIBA@IESTPCHINCHA.EDU.PE  ", "Alumno2026*");
    afirmar(alumno != null, "normaliza el correo (mayusculas y espacios)");
    afirmar(alumno != null && alumno.getRol() == Rol.ESTUDIANTE, "el alumno recibe el rol ESTUDIANTE");

    afirmar(auth.autenticar("carlos.mendoza@iestpchincha.edu.pe", "incorrecta") == null, "rechaza una contrasena incorrecta");
    afirmar(auth.autenticar("no.existe@iestpchincha.edu.pe", "Docente2026*") == null, "rechaza un correo desconocido");
    afirmar(auth.autenticar("", "") == null, "rechaza credenciales vacias");
  }

  private static void probarRedireccionPorRol() {
    for (Rol rol : Rol.values()) {
      afirmar(rol.menuSesion() != MenuSesion.NINGUNO,
          "todo rol del enum tiene un menu asignado (rol " + rol + ")");
    }
    afirmar(Rol.PROFESOR.menuSesion() == MenuSesion.PROFESOR, "el rol PROFESOR abre el menu de profesor");
    afirmar(Rol.ESTUDIANTE.menuSesion() == MenuSesion.ESTUDIANTE, "el rol ESTUDIANTE abre el menu de estudiante");

    Usuario docente = new Usuario("USR-T1", "docente@test.pe", "hash", Rol.PROFESOR, "DOC-001", true);
    Usuario alumno = new Usuario("USR-T2", "alumno@test.pe", "hash", Rol.ESTUDIANTE, "ALU-2024101", true);
    afirmar(docente.getRol().menuSesion() == MenuSesion.PROFESOR, "el docente se redirige a su propio menu");
    afirmar(alumno.getRol().menuSesion() == MenuSesion.ESTUDIANTE, "el alumno se redirige a su propio menu");
  }

  private static void probarPorcentajeDeInasistencia() {
    ServicioAcademico servicio = new ServicioAcademico();

    afirmar(servicio.calcularPorcentajeFaltas(10, 8) == 20.0,
        "el porcentaje preventivo se mide sobre las horas ya dictadas");
    afirmar(servicio.calcularPorcentajeFaltas(16, 16) == 0.0, "sin faltas no hay inasistencia que medir");
    afirmar(servicio.calcularPorcentajeFaltas(0, 0) == 0.0, "sin horas dictadas el porcentaje es 0");
    afirmar(servicio.calcularPorcentajeFaltas(10, 12) == 0.0,
        "no reporta inasistencia negativa cuando se asiste mas de lo dictado");

    Curso curso = servicio.listarCursos().stream()
        .filter(c -> c.getIdCurso().equals("UD-SEGREDES")).findFirst().orElse(null);
    Estudiante alumno = servicio.buscarEstudiante("ALU-2024101");
    List<RegistroDeAsistencia> historial = servicio.historialEstudiante(alumno);

    RegistroDeAsistencia conSesion = historial.stream()
        .filter(r -> r.getIdSesion().equals("SES-SEGREDES-20260309")).findFirst().orElse(null);
    afirmar(conSesion != null, "el alumno tiene un registro enlazado a una sesion");
    afirmar(conSesion != null && servicio.horasProgramadasDe(conSesion, curso) == 2,
        "usa las horas programadas de la sesion enlazada");
    afirmar(conSesion != null && servicio.horasProgramadasDe(conSesion, curso) != curso.getTotalHorasSesion(),
        "la sesion programada puede durar menos que las horas por sesion del curso");

    RegistroDeAsistencia legado = historial.stream()
        .filter(r -> r.getIdSesion().isEmpty()).findFirst().orElse(null);
    afirmar(legado != null, "el alumno tiene una fila legada sin sesion enlazada");
    afirmar(legado != null && servicio.horasProgramadasDe(legado, curso) == curso.getTotalHorasSesion(),
        "la fila legada usa las horas por sesion del curso");
  }

  private static void probarPerfilesVinculados() {
    ServicioAcademico servicio = new ServicioAcademico();

    Profesor profesor = servicio.buscarProfesor("DOC-001");
    Estudiante estudiante = servicio.buscarEstudiante("ALU-2024101");

    afirmar(profesor != null && profesor.getNombreCompleto().equals("Carlos Mendoza"),
        "resuelve el perfil del docente por id_referencia");
    afirmar(estudiante != null && estudiante.getNombreCompleto().equals("Erick Escriba"),
        "resuelve el perfil del estudiante por id_referencia");
    afirmar(estudiante != null && estudiante.getDni().equals("71234501"),
        "el estudiante expone su DNI");
  }

  private static void probarRegistroDeAsistencia() {
    ServicioAcademico servicio = new ServicioAcademico();
    Curso curso = new Curso("C-TEST", "Curso de prueba", 4, 16);
    Estudiante estudiante = new Estudiante("E-TEST", "U-TEST", "Ana", "Prueba", "70000001", "ana@test.pe", "Sistemas",
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
    afirmar(carreras.size() == 4, "lee las carreras sembradas (encontradas: " + carreras.size() + ")");
    afirmar(carreras.stream().anyMatch(c -> c.getIdCarrera().equals("PROG-DSI")
        && c.getNombre().equals("Desarrollo de Sistemas de Información")),
        "conserva la carrera del programa de estudios");
    afirmar(carreras.stream().allMatch(c -> c.getTipo().equals("Educación Superior")),
        "todas las carreras son de tipo Educacion Superior");

    List<PlanEstudios> planes = servicio.listarPlanesEstudios();
    afirmar(planes.size() == 3, "lee los planes vigente, historico y por competencias");
    afirmar(!planes.isEmpty() && planes.get(0).getIdCarrera().equals("PROG-APSTI"), "el plan pertenece a la carrera");
    afirmar(planes.stream().anyMatch(p -> p.getEstado() == EstadoPlan.VIGENTE), "marca el plan vigente");
    afirmar(planes.stream().anyMatch(p -> p.getEstado() == EstadoPlan.HISTORICO), "conserva el plan historico");
    afirmar(planes.stream().anyMatch(p -> p.getIdPlan().equals("PLAN-DSI-2023")
        && p.getCreditosTotales() == 120 && p.getHorasTotales() == 2200),
        "conserva creditos y horas del plan por competencias");

    List<ModuloProfesional> modulos = servicio.listarModulosProfesionales();
    afirmar(modulos.size() == 4, "lee los modulos profesionales");
    afirmar(modulos.stream().filter(m -> m.getIdPlan().equals("PLAN-APSTI-2026")).count() == 3,
        "conserva los modulos del plan base");
    afirmar(modulos.stream().anyMatch(m -> m.getIdModulo().equals("MP-01")
        && m.getIdPlan().equals("PLAN-DSI-2023") && m.getCreditosModulo() == 20),
        "conserva el modulo profesional del plan por competencias");

    List<PeriodoAcademico> periodos = servicio.listarPeriodosAcademicos();
    afirmar(periodos.size() == 3, "lee los periodos academicos");
    afirmar(!periodos.isEmpty() && periodos.get(0).getCodigo().equals("APSTI-2026-I"),
        "conserva el codigo del periodo");
    afirmar(!periodos.isEmpty() && periodos.get(0).getEstado() == EstadoPeriodo.ACTIVO, "marca el periodo activo");
    afirmar(periodos.stream().map(PeriodoAcademico::getCodigo).distinct().count() == periodos.size(),
        "no duplica codigos de periodo");
  }

  private static void probarUnidadDidactica() {
    List<Curso> cursos = ArchivoManager.leerCursos();
    afirmar(cursos.size() == 6, "lee los 6 cursos sembrados (encontrados: " + cursos.size() + ")");

    Curso c1 = cursos.stream().filter(c -> c.getIdCurso().equals("UD-SEGREDES")).findFirst().orElse(null);
    afirmar(c1 != null, "encuentra el curso UD-SEGREDES");
    afirmar(c1 != null && c1.getIdModulo().equals("MP-APSTI-01"), "el curso pertenece a un modulo profesional");
    afirmar(c1 != null && c1.getCreditos() == 4, "conserva los creditos del curso");
    afirmar(c1 != null && c1.getHorasLectivasTotales() == 64, "suma las horas teoricas y practicas");

    Curso legado = new Curso("C-OLD", "Curso legado", 4, 16);
    afirmar(legado.getIdModulo().isEmpty(), "un curso legado no declara modulo");
    afirmar(legado.getCreditos() == 0, "un curso legado no declara creditos");
    afirmar(legado.getHorasLectivasTotales() == 64, "deriva las horas lectivas de duracion por horas de sesion");
  }

  private static void probarOfertaAcademica() {
    ServicioAcademico servicio = new ServicioAcademico();

    List<BloqueSeccion> bloques = servicio.listarBloques();
    afirmar(bloques.size() == 3, "lee los bloques de seccion");
    afirmar(!bloques.isEmpty() && bloques.get(0).getIdPeriodo().equals("PER-APSTI-2026-1"),
        "el bloque pertenece al periodo");
    afirmar(!bloques.isEmpty() && bloques.get(0).getTurno() == Turno.NOCHE, "conserva el turno del bloque");

    List<SeccionCurso> secciones = servicio.listarSecciones();
    afirmar(secciones.size() == 6, "lee las secciones de curso");
    SeccionCurso sec1 = secciones.stream().filter(s -> s.getIdSeccion().equals("SEC-SEGREDES-3N")).findFirst()
        .orElse(null);
    afirmar(sec1 != null && sec1.getIdCurso().equals("UD-SEGREDES"), "la seccion ejecuta una unidad didactica");
    afirmar(sec1 != null && sec1.getModalidad() == ModalidadDictado.PRESENCIAL, "conserva la modalidad");
    afirmar(sec1 != null && sec1.getCupoMaximo() == 30, "conserva el cupo de la seccion");
    afirmar(servicio.listarSeccionesBloque("BLQ-APSTI-3N").size() == 3, "filtra secciones por bloque");

    List<HorarioSeccion> horarios = servicio.listarHorarios();
    afirmar(horarios.size() == 8, "lee las franjas horarias");
    HorarioSeccion h1 = horarios.stream().filter(h -> h.getIdHorario().equals("SLOT-SEGREDES-01")).findFirst()
        .orElse(null);
    afirmar(h1 != null && h1.getDiaSemana() == DiaSemana.LUNES && h1.getTipoClase() == TipoClase.TEORIA,
        "conserva dia y tipo de clase");
    afirmar(h1 != null && h1.getHoraInicio().equals("18:00") && h1.getHoraFin().equals("20:00"),
        "conserva el rango horario");
    afirmar(h1 != null && h1.getIdSalon().equals("AMB-AULA301"), "reserva el ambiente de la franja");
    afirmar(servicio.listarHorariosSeccion("SEC-SEGREDES-3N").size() == 2, "filtra franjas por seccion");

    List<SesionClase> sesiones = servicio.listarSesiones();
    afirmar(sesiones.size() == 8, "lee las sesiones de clase");
    SesionClase ses1 = sesiones.stream().filter(s -> s.getIdSesion().equals("SES-SEGREDES-20260309")).findFirst()
        .orElse(null);
    afirmar(ses1 != null && ses1.getIdHorario().equals("SLOT-SEGREDES-01")
        && ses1.getFecha().equals("2026-03-09"), "la sesion nace de una franja en una fecha");
    afirmar(ses1 != null && ses1.getHorasProgramadas() == 2, "conserva las horas programadas");
    afirmar(servicio.listarSesionesHorario("SLOT-SEGREDES-01").size() == 2, "filtra sesiones por franja");

    List<Salon> salones = servicio.listarSalones();
    afirmar(salones.size() == 5, "lee los ambientes");
    afirmar(salones.stream().anyMatch(s -> s.getIdSalon().equals("AMB-LABREDES")
        && s.getTipo() == TipoSalon.LABORATORIO && s.getCapacidad() == 30),
        "conserva tipo y capacidad del ambiente");

    List<String> idsBloque = bloques.stream().map(BloqueSeccion::getIdBloque).toList();
    afirmar(secciones.stream().allMatch(s -> idsBloque.contains(s.getIdBloque())),
        "toda seccion referencia un bloque existente");
    List<String> idsSeccion = secciones.stream().map(SeccionCurso::getIdSeccion).toList();
    afirmar(horarios.stream().allMatch(h -> idsSeccion.contains(h.getIdSeccion())),
        "toda franja referencia una seccion existente");
    List<String> idsHorario = horarios.stream().map(HorarioSeccion::getIdHorario).toList();
    afirmar(sesiones.stream().allMatch(s -> idsHorario.contains(s.getIdHorario())),
        "toda sesion referencia una franja existente");
    List<String> idsSalon = salones.stream().map(Salon::getIdSalon).toList();
    afirmar(horarios.stream().allMatch(h -> idsSalon.contains(h.getIdSalon())),
        "toda franja referencia un ambiente existente");
    afirmar(sesiones.stream().allMatch(s -> idsSalon.contains(s.getIdSalon())),
        "toda sesion referencia un ambiente existente");
  }

  private static void probarMatriculaYReglaNormativa() {
    ServicioAcademico servicio = new ServicioAcademico();

    List<Matricula> matriculas = servicio.listarMatriculas();
    afirmar(matriculas.size() == 6, "lee las matriculas sembradas (encontradas: " + matriculas.size() + ")");
    Matricula m1 = servicio.buscarMatricula("ALU-2024101", "BLQ-APSTI-3N");
    afirmar(m1 != null && m1.getIdMatricula().equals("MAT-20261-001"),
        "resuelve la matricula activa del estudiante");
    afirmar(m1 != null && m1.getEstado() == EstadoMatricula.ACTIVA, "la matricula sembrada esta activa");
    afirmar(servicio.listarMatriculasBloque("BLQ-APSTI-3N").size() == 5, "filtra matriculas por bloque");
    afirmar(servicio.listarMatriculasBloque("BLQ-APSTI-5M").isEmpty(),
        "no inventa matriculas en bloques sin alumnos");
    Matricula repetida = servicio.matricularEstudiante("ALU-2024101", "BLQ-APSTI-3N", "2026-03-01");
    afirmar(repetida != null && repetida.getIdMatricula().equals("MAT-20261-001"),
        "no duplica una matricula activa");
    afirmar(servicio.matricularEstudiante("ALU-9999", "BLQ-APSTI-3N", "2026-03-01") == null,
        "rechaza un estudiante inexistente");
    afirmar(servicio.matricularEstudiante("ALU-2024101", "BLQ-9999", "2026-03-01") == null,
        "rechaza un bloque inexistente");

    afirmar(servicio.listarSesionesBloque("BLQ-APSTI-3N").size() == 5, "resuelve las sesiones del bloque");
    afirmar(servicio.listarSesionesBloque("BLQ-APSTI-5M").size() == 2, "resuelve las sesiones del otro bloque");

    RegistroDeAsistencia sesionReg = servicio.registrarAsistenciaSesion("SES-SEGREDES-20260309", "ALU-2024101", 99);
    afirmar(sesionReg != null && sesionReg.getIdSesion().equals("SES-SEGREDES-20260309"),
        "vincula el registro con la sesion");
    afirmar(sesionReg != null && sesionReg.getIdCurso().equals("UD-SEGREDES"),
        "deriva la unidad didactica de la sesion");
    afirmar(sesionReg != null && sesionReg.getHorasAsistidas() == 2 && sesionReg.isPresente(),
        "limita las horas a las programadas de la sesion");
    afirmar(sesionReg != null && sesionReg.getIdMatricula().equals("MAT-20261-001"),
        "vincula el registro con la matricula del alumno");
    afirmar(servicio.registrarAsistenciaSesion("SES-SEGREDES-20260309", "ALU-9999", 2) == null,
        "rechaza la asistencia de un estudiante sin matricula");
    afirmar(servicio.registrarAsistenciaSesion("SES-9999", "ALU-2024101", 2) == null,
        "rechaza una sesion inexistente");

    List<RegistroDeAsistencia> historialE1 = servicio.historialEstudiante(servicio.buscarEstudiante("ALU-2024101"));
    afirmar(historialE1.stream().anyMatch(r -> r.getIdSesion().equals("SES-SEGREDES-20260309")),
        "lee el id_sesion de las filas nuevas");
    afirmar(historialE1.stream().anyMatch(r -> r.getIdSesion().isEmpty()),
        "tolera filas legadas sin id_sesion");
    afirmar(historialE1.stream().allMatch(r -> r.getIdMatricula().equals("MAT-20261-001")),
        "lee el id_matricula de las filas nuevas");

    Curso c1 = servicio.listarCursos().stream().filter(c -> c.getIdCurso().equals("UD-SEGREDES")).findFirst()
        .orElse(null);
    afirmar(c1 != null && servicio.horasFaltadasUnidadDidactica(
        servicio.buscarMatricula("ALU-2024101", "BLQ-APSTI-3N"), c1) == 0,
        "no acumula faltas en sesiones asistidas completas");
    afirmar(c1 != null && servicio.horasFaltadasUnidadDidactica(
        servicio.buscarMatricula("ALU-2024102", "BLQ-APSTI-3N"), c1) == 2,
        "acumula las horas no asistidas de la sesion tomada");

    Curso curso = new Curso("C-NORM", "Curso normativo", "MP-APSTI-01", 1, 6, 4, 2, 5);
    Estudiante est = new Estudiante("E-NORM", "U-NORM", "Ana", "Norma", "70000002", "ana@norm.pe", "Sistemas",
        EstadoEstudiante.REGULAR);
    SesionClase s1 = new SesionClase("S-N1", "H-N1", "2026-03-02", 2, "DOC-001", "AMB-AULA301");
    SesionClase s2 = new SesionClase("S-N2", "H-N2", "2026-03-03", 2, "DOC-001", "AMB-AULA301");
    List<SesionClase> sesiones = List.of(s1, s2);

    RegistroDeAsistencia r1 = servicio.crearRegistroSesion(s1, curso, est, 2);
    RegistroDeAsistencia r2 = servicio.crearRegistroSesion(s2, curso, est, 0);
    afirmar(servicio.calcularHorasFaltadas(sesiones, List.of(r1, r2)) == 2, "suma programadas menos asistidas");
    afirmar(servicio.calcularHorasFaltadas(sesiones, List.of(r1)) == 0,
        "ignora las sesiones sin asistencia tomada");
    afirmar(servicio.calcularPorcentajeInasistencia(2, 10) == 20.0, "calcula el porcentaje sobre horas lectivas");
    afirmar(!servicio.estaInhabilitadoPorInasistencia(2, 10), "no inhabilita por debajo del 30%");

    RegistroDeAsistencia r3 = servicio.crearRegistroSesion(s1, curso, est, 0);
    afirmar(servicio.calcularHorasFaltadas(sesiones, List.of(r3, r2)) == 4, "acumula todas las horas faltadas");
    afirmar(servicio.calcularPorcentajeInasistencia(4, 10) == 40.0, "calcula el 40% de inasistencia");
    afirmar(servicio.estaInhabilitadoPorInasistencia(4, 10), "inhabilita al superar el 30%");
    afirmar(!servicio.estaInhabilitadoPorInasistencia(3, 10), "el 30% exacto no inhabilita");
    afirmar(!servicio.estaInhabilitadoPorInasistencia(0, 0), "sin horas lectivas no inhabilita");
    afirmar(servicio.calcularPorcentajeInasistencia(-5, 10) == 0.0, "normaliza faltas negativas a 0%");

    Matricula activa = new Matricula("MAT-NORM", "E-NORM", "BLQ-APSTI-3N", "2026-03-01", EstadoMatricula.ACTIVA);
    Matricula retirada = new Matricula("MAT-RET", "E-NORM", "BLQ-APSTI-3N", "2026-03-01", EstadoMatricula.RETIRADA);
    afirmar(servicio.puedeRegistrarNotas(activa, curso), "permite notas con matricula activa y 0% de faltas");
    afirmar(!servicio.puedeRegistrarNotas(retirada, curso), "bloquea notas con matricula retirada");
  }

  private static void probarEvaluacionYFinanzas() {
    ServicioAcademico servicio = new ServicioAcademico();

    List<IndicadorLogro> indicadores = servicio.listarIndicadores();
    afirmar(indicadores.size() == 5, "lee los indicadores de logro sembrados (encontrados: " + indicadores.size() + ")");
    afirmar(servicio.listarIndicadoresSeccion("SEC-SEGREDES-3N").size() == 3, "filtra indicadores por seccion");
    afirmar(servicio.listarIndicadoresSeccion("SEC-9999").isEmpty(),
        "no inventa indicadores en secciones sin configurar");
    IndicadorLogro i1 = indicadores.stream().filter(i -> i.getIdIndicador().equals("IND-02")).findFirst().orElse(null);
    afirmar(i1 != null && i1.getPeso() == 30 && i1.getIdSeccion().equals("SEC-SEGREDES-3N"),
        "conserva peso y seccion del indicador");

    afirmar(servicio.listarNotas().size() == 3, "lee las notas sembradas");
    afirmar(servicio.listarNotasEstudiante("ALU-2024101").size() == 1, "filtra notas por estudiante");
    afirmar(servicio.listarNotasIndicador("IND-02").size() == 1, "filtra notas por indicador");
    RegistroNota n1 = servicio.listarNotasEstudiante("ALU-2024101").stream().findFirst().orElse(null);
    afirmar(n1 != null && n1.getNota() == 16 && n1.getIdIndicador().equals("IND-02"),
        "conserva la nota vigesimal sembrada");

    afirmar(servicio.puedeRegistrarNota("IND-02", "ALU-2024101"),
        "permite notas con matricula activa y asistencia regular");
    afirmar(servicio.puedeRegistrarNota("IND-04", "ALU-2024102"), "permite notas dentro del limite normativo");
    afirmar(!servicio.puedeRegistrarNota("IND-02", "ALU-9999"), "rechaza un estudiante sin matricula");
    afirmar(!servicio.puedeRegistrarNota("IND-99", "ALU-2024101"), "rechaza un indicador inexistente");

    Curso curso = new Curso("C-NOTA", "Curso notas", "MP-APSTI-01", 1, 6, 4, 2, 5);
    Matricula activa = new Matricula("MAT-NOTA", "ALU-2024101", "BLQ-APSTI-3N", "2026-03-01",
        EstadoMatricula.ACTIVA);
    Estudiante est = new Estudiante("ALU-2024101", "U2024101", "Erick", "Escriba", "71234501",
        "erick.escriba@iestpchincha.edu.pe", "Ingenieria de Software", EstadoEstudiante.REGULAR);
    SesionClase s1 = new SesionClase("S-N1", "H-N1", "2026-03-02", 2, "DOC-001", "AMB-AULA301");
    SesionClase s2 = new SesionClase("S-N2", "H-N2", "2026-03-03", 2, "DOC-001", "AMB-AULA301");
    RegistroDeAsistencia r1 = servicio.crearRegistroSesion(s1, curso, est, 2);
    RegistroDeAsistencia r2 = servicio.crearRegistroSesion(s2, curso, est, 0);
    afirmar(servicio.puedeRegistrarNota(activa, curso, List.of(s1, s2), List.of(r1, r2)),
        "consume la regla del 30% al habilitar notas");
    RegistroDeAsistencia r3 = servicio.crearRegistroSesion(s1, curso, est, 0);
    afirmar(!servicio.puedeRegistrarNota(activa, curso, List.of(s1, s2), List.of(r3, r2)),
        "bloquea notas al superar el 30% de inasistencia");

    RegistroNota nueva = servicio.crearRegistroNota("IND-02", "ALU-2024101", 18, "2026-04-25");
    afirmar(nueva != null && nueva.getNota() == 18 && nueva.getIdIndicador().equals("IND-02"),
        "crea una nota valida sin persistir");
    afirmar(nueva != null && nueva.getIdRegistroNota() == null, "la nota nueva aun no tiene identificador");
    afirmar(servicio.crearRegistroNota("IND-02", "ALU-2024101", 21, "2026-04-25") == null,
        "rechaza notas mayores a 20");
    afirmar(servicio.crearRegistroNota("IND-02", "ALU-2024101", -1, "2026-04-25") == null,
        "rechaza notas negativas");
    afirmar(servicio.crearRegistroNota("IND-99", "ALU-2024101", 15, "2026-04-25") == null,
        "rechaza un indicador inexistente");
    afirmar(servicio.crearRegistroNota("IND-02", "ALU-9999", 15, "2026-04-25") == null,
        "rechaza un estudiante sin matricula");

    afirmar(servicio.listarDerechosPago().size() == 3, "lee los derechos de pago sembrados");
    afirmar(servicio.listarComprobantes().size() == 3, "lee los comprobantes sembrados");
    afirmar(servicio.listarComprobantesEstudiante("ALU-2024101").size() == 2,
        "filtra comprobantes por estudiante");
    afirmar(servicio.saldoPendiente("ALU-2024101", "TAS-MAT-APSTI-2026") == 0.0, "el derecho pagado no deja saldo");
    afirmar(servicio.saldoPendiente("ALU-2024102", "TAS-MAT-APSTI-2026") == 350.0,
        "el derecho impago mantiene el saldo");
    afirmar(servicio.saldoPendiente("ALU-2024101", "TAS-9999") == 0.0, "sin derecho no hay saldo");

    ComprobantePago nuevoPago = servicio.crearComprobantePago("ALU-2024102", "TAS-MAT-APSTI-2026", "2026-04-25",
        100.0);
    afirmar(nuevoPago != null && nuevoPago.getMonto() == 100.0, "crea un comprobante dentro del saldo");
    afirmar(nuevoPago != null && nuevoPago.getEstado() == EstadoComprobante.PAGADO,
        "el comprobante nuevo se emite como PAGADO");
    afirmar(nuevoPago != null && nuevoPago.getIdComprobante() == null,
        "el comprobante nuevo aun no tiene identificador");
    afirmar(servicio.crearComprobantePago("ALU-2024102", "TAS-MAT-APSTI-2026", "2026-04-25", 400.0) == null,
        "rechaza un pago mayor al saldo pendiente");
    afirmar(servicio.crearComprobantePago("ALU-2024102", "TAS-MAT-APSTI-2026", "2026-04-25", 0.0) == null,
        "rechaza un pago no positivo");
    afirmar(servicio.crearComprobantePago("ALU-9999", "TAS-MAT-APSTI-2026", "2026-04-25", 10.0) == null,
        "rechaza un estudiante inexistente");
    afirmar(servicio.crearComprobantePago("ALU-2024101", "TAS-9999", "2026-04-25", 10.0) == null,
        "rechaza un derecho inexistente");
  }

  private static void probarCursosDeProfesor() {
    ServicioAcademico servicio = new ServicioAcademico();

    Profesor docente = servicio.buscarProfesor("DOC-001");
    List<Curso> cursos = servicio.listarCursosProfesor(docente);
    afirmar(cursos.size() == 3, "deriva los cursos del docente desde sus franjas (encontrados: " + cursos.size() + ")");
    afirmar(cursos.stream().anyMatch(c -> c.getIdCurso().equals("UD-SEGREDES")),
        "incluye la unidad didactica dictada en el bloque");
    afirmar(cursos.stream().anyMatch(c -> c.getIdCurso().equals("UD-REDES1")),
        "incluye la unidad didactica del plan por competencias");

    Profesor laboratorio = servicio.buscarProfesor("DOC-002");
    afirmar(servicio.listarCursosProfesor(laboratorio).size() == 1,
        "resuelve el curso del docente que solo dicta practica");
  }

  private static void probarNominaDesdeMatricula() {
    ServicioAcademico servicio = new ServicioAcademico();

    Curso redes = servicio.listarCursos().stream()
        .filter(c -> c.getIdCurso().equals("UD-REDES1")).findFirst().orElse(null);
    Curso segRedes = servicio.listarCursos().stream()
        .filter(c -> c.getIdCurso().equals("UD-SEGREDES")).findFirst().orElse(null);

    List<Estudiante> nominaRedes = servicio.listarEstudiantesCurso(redes);
    afirmar(nominaRedes.size() == 1, "deriva la nomina de UD-REDES1 desde la matricula del bloque");
    afirmar(nominaRedes.stream().anyMatch(e -> e.getId().equals("ALU-2026045")),
        "incluye al alumno matriculado en BLQ-3N-DSI");

    List<Estudiante> nominaSegRedes = servicio.listarEstudiantesCurso(segRedes);
    afirmar(nominaSegRedes.size() == 5, "deriva la nomina de UD-SEGREDES desde el bloque completo");
    afirmar(nominaSegRedes.stream().map(Estudiante::getId).distinct().count() == nominaSegRedes.size(),
        "no duplica alumnos en la nomina");

    Curso sinSecciones = new Curso("UD-INEXISTENTE", "Curso sin secciones", 2, 8);
    afirmar(servicio.listarEstudiantesCurso(sinSecciones).isEmpty(),
        "un curso sin secciones no tiene nomina");
    afirmar(servicio.listarEstudiantesCurso(null).isEmpty(), "un curso nulo no tiene nomina");

    Estudiante juan = servicio.buscarEstudiante("ALU-2026045");
    List<Curso> cursosJuan = servicio.listarCursosEstudiante(juan);
    afirmar(cursosJuan.size() == 1 && cursosJuan.get(0).getIdCurso().equals("UD-REDES1"),
        "deriva los cursos del alumno desde su matricula en el bloque");

    Estudiante erick = servicio.buscarEstudiante("ALU-2024101");
    List<Curso> cursosErick = servicio.listarCursosEstudiante(erick);
    afirmar(cursosErick.size() == 3, "el bloque BLQ-APSTI-3N aporta sus tres secciones");
    afirmar(cursosErick.stream().map(Curso::getIdCurso).distinct().count() == cursosErick.size(),
        "no duplica cursos del alumno");
    afirmar(cursosErick.stream().anyMatch(c -> c.getIdCurso().equals("UD-SEGREDES"))
        && cursosErick.stream().anyMatch(c -> c.getIdCurso().equals("UD-ETHHACK"))
        && cursosErick.stream().anyMatch(c -> c.getIdCurso().equals("UD-ROUTING")),
        "incluye las unidades didacticas del bloque");

    Estudiante sinMatricula = new Estudiante("ALU-9999", "U9999", "Sin", "Matricula", "70009999",
        "sin.matricula@iestpchincha.edu.pe", "Sistemas", EstadoEstudiante.REGULAR);
    afirmar(servicio.listarCursosEstudiante(sinMatricula).isEmpty(),
        "un alumno sin matricula no tiene cursos");
    afirmar(servicio.listarCursosEstudiante(null).isEmpty(), "un alumno nulo no tiene cursos");
  }

  private static void probarDatasetEjemploInstitucional() {
    ServicioAcademico servicio = new ServicioAcademico();

    Carrera carrera = servicio.listarCarreras().stream()
        .filter(c -> c.getIdCarrera().equals("PROG-DSI")).findFirst().orElse(null);
    afirmar(carrera != null && carrera.getNombre().equals("Desarrollo de Sistemas de Información")
        && carrera.getTipo().equals("Educación Superior"), "existe la carrera PROG-DSI de Educacion Superior");

    PlanEstudios plan = servicio.listarPlanesEstudios().stream()
        .filter(p -> p.getIdPlan().equals("PLAN-DSI-2023")).findFirst().orElse(null);
    afirmar(plan != null && plan.getIdCarrera().equals("PROG-DSI") && plan.getCreditosTotales() == 120,
        "el plan PLAN-DSI-2023 pertenece a PROG-DSI con 120 creditos");

    ModuloProfesional modulo = servicio.listarModulosProfesionales().stream()
        .filter(m -> m.getIdModulo().equals("MP-01")).findFirst().orElse(null);
    afirmar(modulo != null && modulo.getIdPlan().equals("PLAN-DSI-2023"),
        "el modulo MP-01 pertenece al plan PLAN-DSI-2023");

    Curso curso = servicio.listarCursos().stream()
        .filter(c -> c.getIdCurso().equals("UD-REDES1")).findFirst().orElse(null);
    afirmar(curso != null && curso.getIdModulo().equals("MP-01") && curso.getHorasLectivasTotales() == 64,
        "la unidad didactica UD-REDES1 tiene 64 horas lectivas");

    PeriodoAcademico periodo = servicio.listarPeriodosAcademicos().stream()
        .filter(p -> p.getIdPeriodo().equals("PER-2026-1")).findFirst().orElse(null);
    afirmar(periodo != null && periodo.getIdPlan().equals("PLAN-DSI-2023")
        && periodo.getFechaInicio().equals("2026-03-16") && periodo.getFechaFin().equals("2026-07-17"),
        "el periodo PER-2026-1 conserva sus fechas");

    BloqueSeccion bloque = servicio.listarBloques().stream()
        .filter(b -> b.getIdBloque().equals("BLQ-3N-DSI")).findFirst().orElse(null);
    afirmar(bloque != null && bloque.getIdPeriodo().equals("PER-2026-1") && bloque.getTurno() == Turno.NOCHE,
        "el bloque BLQ-3N-DSI pertenece al periodo en turno noche");

    SeccionCurso seccion = servicio.listarSecciones().stream()
        .filter(s -> s.getIdSeccion().equals("SEC-REDES1-3N")).findFirst().orElse(null);
    afirmar(seccion != null && seccion.getIdBloque().equals("BLQ-3N-DSI")
        && seccion.getIdCurso().equals("UD-REDES1"),
        "la seccion SEC-REDES1-3N relaciona bloque y curso");

    HorarioSeccion slot1 = servicio.listarHorarios().stream()
        .filter(h -> h.getIdHorario().equals("SLOT-01")).findFirst().orElse(null);
    HorarioSeccion slot2 = servicio.listarHorarios().stream()
        .filter(h -> h.getIdHorario().equals("SLOT-02")).findFirst().orElse(null);
    afirmar(slot1 != null && slot1.getIdSeccion().equals("SEC-REDES1-3N") && slot1.getDiaSemana() == DiaSemana.LUNES
        && slot1.getTipoClase() == TipoClase.TEORIA && slot1.getIdSalon().equals("AMB-AULA102")
        && slot1.getIdProfesor().equals("DOC-001"), "la franja SLOT-01 dicta teoria el lunes en el aula");
    afirmar(slot2 != null && slot2.getDiaSemana() == DiaSemana.JUEVES
        && slot2.getTipoClase() == TipoClase.PRACTICA && slot2.getIdSalon().equals("AMB-LAB02")
        && slot2.getIdProfesor().equals("DOC-002"), "la franja SLOT-02 dicta practica el jueves en el laboratorio");

    SesionClase sesion = servicio.listarSesiones().stream()
        .filter(s -> s.getIdSesion().equals("SES-20260511-RED")).findFirst().orElse(null);
    afirmar(sesion != null && sesion.getIdHorario().equals("SLOT-01") && sesion.getFecha().equals("2026-05-11")
        && sesion.getHorasProgramadas() == 2, "la sesion SES-20260511-RED nace de SLOT-01 el 11/05/2026");

    Estudiante alumno = servicio.buscarEstudiante("ALU-2026045");
    afirmar(alumno != null && alumno.getCorreo().equals("juan.perez@iestpchincha.edu.pe"),
        "existe el alumno ALU-2026045 con correo institucional");
    afirmar(alumno != null && alumno.getDni().equals("72819034"), "el alumno ALU-2026045 declara su DNI");

    Matricula matricula = servicio.buscarMatricula("ALU-2026045", "BLQ-3N-DSI");
    afirmar(matricula != null && matricula.getIdMatricula().equals("MAT-20261-045")
        && matricula.getEstado() == EstadoMatricula.ACTIVA, "la matricula MAT-20261-045 esta activa en el bloque");

    List<RegistroDeAsistencia> asistencia = servicio.historialEstudiante(alumno);
    afirmar(asistencia.stream().anyMatch(r -> r.getIdAsistencia().equals("REG-ASI-0892")
        && r.getIdSesion().equals("SES-20260511-RED") && r.getIdMatricula().equals("MAT-20261-045")
        && r.getHorasAsistidas() == 2 && r.isPresente()),
        "el registro REG-ASI-0892 asiste completo a la sesion");

    IndicadorLogro indicador = servicio.listarIndicadores().stream()
        .filter(i -> i.getIdIndicador().equals("IND-01")).findFirst().orElse(null);
    afirmar(indicador != null && indicador.getIdSeccion().equals("SEC-REDES1-3N") && indicador.getPeso() == 30,
        "el indicador IND-01 pondera 30% en la seccion");

    RegistroNota nota = servicio.listarNotas().stream()
        .filter(n -> n.getIdRegistroNota().equals("NOT-2026-012")).findFirst().orElse(null);
    afirmar(nota != null && nota.getIdIndicador().equals("IND-01")
        && nota.getIdEstudiante().equals("ALU-2026045") && nota.getNota() == 16,
        "la nota NOT-2026-012 registra 16/20 para el alumno");

    DerechoPago derecho = servicio.listarDerechosPago().stream()
        .filter(d -> d.getIdDerecho().equals("TAS-MAT-2026")).findFirst().orElse(null);
    afirmar(derecho != null && derecho.getMonto() == 150.0, "el derecho TAS-MAT-2026 cuesta 150.00");

    ComprobantePago comprobante = servicio.listarComprobantes().stream()
        .filter(c -> c.getIdComprobante().equals("REC-2026-0041")).findFirst().orElse(null);
    afirmar(comprobante != null && comprobante.getIdEstudiante().equals("ALU-2026045")
        && comprobante.getIdDerecho().equals("TAS-MAT-2026") && comprobante.getMonto() == 150.0
        && comprobante.getEstado() == EstadoComprobante.PAGADO,
        "el comprobante REC-2026-0041 paga el derecho del alumno");
    afirmar(servicio.saldoPendiente("ALU-2026045", "TAS-MAT-2026") == 0.0,
        "el derecho pagado no deja saldo pendiente");

    afirmar(servicio.listarComprobantes().stream().allMatch(c -> c.getEstado() == EstadoComprobante.PAGADO),
        "todos los comprobantes sembrados estan pagados");
  }

  private static void afirmar(boolean condicion, String descripcion) {
    total++;
    if (!condicion) {
      fallos++;
      System.out.println("FALLO: " + descripcion);
    }
  }
}
