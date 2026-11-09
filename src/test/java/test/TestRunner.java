package test;

import java.util.List;

import models.BloqueSeccion;
import models.Carrera;
import models.ComprobantePago;
import models.Curso;
import models.DerechoPago;
import models.DiaSemana;
import models.Estudiante;
import models.EstadoEstudiante;
import models.EstadoMatricula;
import models.EstadoPeriodo;
import models.EstadoPlan;
import models.HorarioSeccion;
import models.IndicadorLogro;
import models.Matricula;
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
    probarPerfilesVinculados();
    probarRegistroDeAsistencia();
    probarFundacionesAcademicas();
    probarUnidadDidactica();
    probarOfertaAcademica();
    probarMatriculaYReglaNormativa();
    probarEvaluacionYFinanzas();

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

  private static void probarOfertaAcademica() {
    ServicioAcademico servicio = new ServicioAcademico();

    List<BloqueSeccion> bloques = servicio.listarBloques();
    afirmar(bloques.size() == 2, "lee los bloques de seccion");
    afirmar(!bloques.isEmpty() && bloques.get(0).getIdPeriodo().equals("PE1"), "el bloque pertenece al periodo");
    afirmar(!bloques.isEmpty() && bloques.get(0).getTurno() == Turno.NOCHE, "conserva el turno del bloque");

    List<SeccionCurso> secciones = servicio.listarSecciones();
    afirmar(secciones.size() == 5, "lee las secciones de curso");
    SeccionCurso sec1 = secciones.stream().filter(s -> s.getIdSeccion().equals("SEC1")).findFirst().orElse(null);
    afirmar(sec1 != null && sec1.getIdCurso().equals("C1"), "la seccion ejecuta una unidad didactica");
    afirmar(sec1 != null && sec1.getModalidad() == ModalidadDictado.PRESENCIAL, "conserva la modalidad");
    afirmar(sec1 != null && sec1.getIdProfesor().equals("P1") && sec1.getCupoMaximo() == 30,
        "conserva docente y cupo de la seccion");
    afirmar(servicio.listarSeccionesBloque("B1").size() == 3, "filtra secciones por bloque");

    List<HorarioSeccion> horarios = servicio.listarHorarios();
    afirmar(horarios.size() == 6, "lee las franjas horarias");
    HorarioSeccion h1 = horarios.stream().filter(h -> h.getIdHorario().equals("H1")).findFirst().orElse(null);
    afirmar(h1 != null && h1.getDiaSemana() == DiaSemana.LUNES && h1.getTipoClase() == TipoClase.TEORIA,
        "conserva dia y tipo de clase");
    afirmar(h1 != null && h1.getHoraInicio().equals("18:00") && h1.getHoraFin().equals("20:00"),
        "conserva el rango horario");
    afirmar(h1 != null && h1.getIdSalon().equals("S1"), "reserva el ambiente de la franja");
    afirmar(servicio.listarHorariosSeccion("SEC1").size() == 2, "filtra franjas por seccion");

    List<SesionClase> sesiones = servicio.listarSesiones();
    afirmar(sesiones.size() == 7, "lee las sesiones de clase");
    SesionClase ses1 = sesiones.stream().filter(s -> s.getIdSesion().equals("SES1")).findFirst().orElse(null);
    afirmar(ses1 != null && ses1.getIdHorario().equals("H1") && ses1.getFecha().equals("2026-03-09"),
        "la sesion nace de una franja en una fecha");
    afirmar(ses1 != null && ses1.getHorasProgramadas() == 2, "conserva las horas programadas");
    afirmar(servicio.listarSesionesHorario("H1").size() == 2, "filtra sesiones por franja");

    List<Salon> salones = servicio.listarSalones();
    afirmar(salones.size() == 3, "lee los ambientes");
    afirmar(salones.stream().anyMatch(s -> s.getIdSalon().equals("S2") && s.getTipo() == TipoSalon.LABORATORIO
        && s.getCapacidad() == 30), "conserva tipo y capacidad del ambiente");

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
    afirmar(matriculas.size() == 5, "lee las matriculas sembradas (encontradas: " + matriculas.size() + ")");
    Matricula m1 = servicio.buscarMatricula("E1", "B1");
    afirmar(m1 != null && m1.getIdMatricula().equals("M1"), "resuelve la matricula activa del estudiante");
    afirmar(m1 != null && m1.getEstado() == EstadoMatricula.ACTIVA, "la matricula sembrada esta activa");
    afirmar(servicio.listarMatriculasBloque("B1").size() == 5, "filtra matriculas por bloque");
    afirmar(servicio.listarMatriculasBloque("B2").isEmpty(), "no inventa matriculas en bloques sin alumnos");
    Matricula repetida = servicio.matricularEstudiante("E1", "B1", "2026-03-01");
    afirmar(repetida != null && repetida.getIdMatricula().equals("M1"), "no duplica una matricula activa");
    afirmar(servicio.matricularEstudiante("E99", "B1", "2026-03-01") == null,
        "rechaza un estudiante inexistente");
    afirmar(servicio.matricularEstudiante("E1", "B99", "2026-03-01") == null, "rechaza un bloque inexistente");

    afirmar(servicio.listarSesionesBloque("B1").size() == 5, "resuelve las sesiones del bloque");
    afirmar(servicio.listarSesionesBloque("B2").size() == 2, "resuelve las sesiones del otro bloque");

    RegistroDeAsistencia sesionReg = servicio.registrarAsistenciaSesion("SES1", "E1", 99);
    afirmar(sesionReg != null && sesionReg.getIdSesion().equals("SES1"), "vincula el registro con la sesion");
    afirmar(sesionReg != null && sesionReg.getIdCurso().equals("C1"), "deriva la unidad didactica de la sesion");
    afirmar(sesionReg != null && sesionReg.getHorasAsistidas() == 2 && sesionReg.isPresente(),
        "limita las horas a las programadas de la sesion");
    afirmar(servicio.registrarAsistenciaSesion("SES1", "E99", 2) == null,
        "rechaza la asistencia de un estudiante sin matricula");
    afirmar(servicio.registrarAsistenciaSesion("SES99", "E1", 2) == null, "rechaza una sesion inexistente");

    List<RegistroDeAsistencia> historialE1 = servicio.historialEstudiante(servicio.buscarEstudiante("E1"));
    afirmar(historialE1.stream().anyMatch(r -> r.getIdSesion().equals("SES1")),
        "lee el id_sesion de las filas nuevas");
    afirmar(historialE1.stream().anyMatch(r -> r.getIdSesion().isEmpty()),
        "tolera filas legadas sin id_sesion");

    Curso c1 = servicio.listarCursos().stream().filter(c -> c.getIdCurso().equals("C1")).findFirst().orElse(null);
    afirmar(c1 != null && servicio.horasFaltadasUnidadDidactica(servicio.buscarMatricula("E1", "B1"), c1) == 0,
        "no acumula faltas en sesiones asistidas completas");
    afirmar(c1 != null && servicio.horasFaltadasUnidadDidactica(servicio.buscarMatricula("E2", "B1"), c1) == 2,
        "acumula las horas no asistidas de la sesion tomada");

    Curso curso = new Curso("C-NORM", "Curso normativo", 2, "P1", 5, "M1", 1, 6, 4);
    Estudiante est = new Estudiante("E-NORM", "U-NORM", "Ana", "Norma", "ana@norm.pe", "Sistemas",
        EstadoEstudiante.REGULAR);
    SesionClase s1 = new SesionClase("S-N1", "H-N1", "2026-03-02", 2, "P1", "S1");
    SesionClase s2 = new SesionClase("S-N2", "H-N2", "2026-03-03", 2, "P1", "S1");
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

    Matricula activa = new Matricula("M-NORM", "E-NORM", "B1", "2026-03-01", EstadoMatricula.ACTIVA);
    Matricula retirada = new Matricula("M-RET", "E-NORM", "B1", "2026-03-01", EstadoMatricula.RETIRADA);
    afirmar(servicio.puedeRegistrarNotas(activa, curso), "permite notas con matricula activa y 0% de faltas");
    afirmar(!servicio.puedeRegistrarNotas(retirada, curso), "bloquea notas con matricula retirada");
  }

  private static void probarEvaluacionYFinanzas() {
    ServicioAcademico servicio = new ServicioAcademico();

    List<IndicadorLogro> indicadores = servicio.listarIndicadores();
    afirmar(indicadores.size() == 4, "lee los indicadores de logro sembrados (encontrados: " + indicadores.size() + ")");
    afirmar(servicio.listarIndicadoresSeccion("SEC1").size() == 3, "filtra indicadores por seccion");
    afirmar(servicio.listarIndicadoresSeccion("SEC9").isEmpty(),
        "no inventa indicadores en secciones sin configurar");
    IndicadorLogro i1 = indicadores.stream().filter(i -> i.getIdIndicador().equals("I1")).findFirst().orElse(null);
    afirmar(i1 != null && i1.getPeso() == 30 && i1.getIdSeccion().equals("SEC1"),
        "conserva peso y seccion del indicador");

    afirmar(servicio.listarNotas().size() == 2, "lee las notas sembradas");
    afirmar(servicio.listarNotasEstudiante("E1").size() == 1, "filtra notas por estudiante");
    afirmar(servicio.listarNotasIndicador("I1").size() == 1, "filtra notas por indicador");
    RegistroNota n1 = servicio.listarNotasEstudiante("E1").stream().findFirst().orElse(null);
    afirmar(n1 != null && n1.getNota() == 16 && n1.getIdIndicador().equals("I1"),
        "conserva la nota vigesimal sembrada");

    afirmar(servicio.puedeRegistrarNota("I1", "E1"), "permite notas con matricula activa y asistencia regular");
    afirmar(servicio.puedeRegistrarNota("I3", "E2"), "permite notas dentro del limite normativo");
    afirmar(!servicio.puedeRegistrarNota("I1", "E99"), "rechaza un estudiante sin matricula");
    afirmar(!servicio.puedeRegistrarNota("I99", "E1"), "rechaza un indicador inexistente");

    Curso curso = new Curso("C-NOTA", "Curso notas", 2, "P1", 5, "M1", 1, 6, 4);
    Matricula activa = new Matricula("M-NOTA", "E1", "B1", "2026-03-01", EstadoMatricula.ACTIVA);
    Estudiante est = new Estudiante("E1", "U2024101", "Erick", "Escriba", "erick.escriba@iestp.pe",
        "Ingenieria de Software", EstadoEstudiante.REGULAR);
    SesionClase s1 = new SesionClase("S-N1", "H-N1", "2026-03-02", 2, "P1", "S1");
    SesionClase s2 = new SesionClase("S-N2", "H-N2", "2026-03-03", 2, "P1", "S1");
    RegistroDeAsistencia r1 = servicio.crearRegistroSesion(s1, curso, est, 2);
    RegistroDeAsistencia r2 = servicio.crearRegistroSesion(s2, curso, est, 0);
    afirmar(servicio.puedeRegistrarNota(activa, curso, List.of(s1, s2), List.of(r1, r2)),
        "consume la regla del 30% al habilitar notas");
    RegistroDeAsistencia r3 = servicio.crearRegistroSesion(s1, curso, est, 0);
    afirmar(!servicio.puedeRegistrarNota(activa, curso, List.of(s1, s2), List.of(r3, r2)),
        "bloquea notas al superar el 30% de inasistencia");

    RegistroNota nueva = servicio.crearRegistroNota("I1", "E1", 18, "2026-04-25");
    afirmar(nueva != null && nueva.getNota() == 18 && nueva.getIdIndicador().equals("I1"),
        "crea una nota valida sin persistir");
    afirmar(nueva != null && nueva.getIdRegistroNota() == null, "la nota nueva aun no tiene identificador");
    afirmar(servicio.crearRegistroNota("I1", "E1", 21, "2026-04-25") == null, "rechaza notas mayores a 20");
    afirmar(servicio.crearRegistroNota("I1", "E1", -1, "2026-04-25") == null, "rechaza notas negativas");
    afirmar(servicio.crearRegistroNota("I99", "E1", 15, "2026-04-25") == null,
        "rechaza un indicador inexistente");
    afirmar(servicio.crearRegistroNota("I1", "E99", 15, "2026-04-25") == null,
        "rechaza un estudiante sin matricula");

    afirmar(servicio.listarDerechosPago().size() == 2, "lee los derechos de pago sembrados");
    afirmar(servicio.listarComprobantes().size() == 2, "lee los comprobantes sembrados");
    afirmar(servicio.listarComprobantesEstudiante("E1").size() == 2, "filtra comprobantes por estudiante");
    afirmar(servicio.saldoPendiente("E1", "D1") == 0.0, "el derecho pagado no deja saldo");
    afirmar(servicio.saldoPendiente("E2", "D1") == 350.0, "el derecho impago mantiene el saldo");
    afirmar(servicio.saldoPendiente("E1", "D99") == 0.0, "sin derecho no hay saldo");

    ComprobantePago nuevoPago = servicio.crearComprobantePago("E2", "D1", "2026-04-25", 100.0);
    afirmar(nuevoPago != null && nuevoPago.getMonto() == 100.0, "crea un comprobante dentro del saldo");
    afirmar(nuevoPago != null && nuevoPago.getIdComprobante() == null,
        "el comprobante nuevo aun no tiene identificador");
    afirmar(servicio.crearComprobantePago("E2", "D1", "2026-04-25", 400.0) == null,
        "rechaza un pago mayor al saldo pendiente");
    afirmar(servicio.crearComprobantePago("E2", "D1", "2026-04-25", 0.0) == null,
        "rechaza un pago no positivo");
    afirmar(servicio.crearComprobantePago("E99", "D1", "2026-04-25", 10.0) == null,
        "rechaza un estudiante inexistente");
    afirmar(servicio.crearComprobantePago("E1", "D99", "2026-04-25", 10.0) == null,
        "rechaza un derecho inexistente");
  }

  private static void afirmar(boolean condicion, String descripcion) {
    total++;
    if (!condicion) {
      fallos++;
      System.out.println("FALLO: " + descripcion);
    }
  }
}
