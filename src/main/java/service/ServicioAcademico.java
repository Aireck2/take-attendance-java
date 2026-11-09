package service;

import java.util.ArrayList;
import java.util.List;

import models.BloqueSeccion;
import models.Carrera;
import models.Curso;
import models.Estudiante;
import models.EstadoMatricula;
import models.HorarioSeccion;
import models.Matricula;
import models.ModuloProfesional;
import models.PeriodoAcademico;
import models.PlanEstudios;
import models.Profesor;
import models.RegistroDeAsistencia;
import models.Salon;
import models.SeccionCurso;
import models.SesionClase;
import storage.ArchivoManager;

/**
 * Contiene la logica de negocio del sistema de asistencia:
 * consultas entre entidades, calculos de porcentajes y registro de asistencias.
 */
public class ServicioAcademico {

  /**
   * Limite de inasistencia (porcentaje de faltas) antes de generar alerta.
   */
  public static final double LIMITE_INASISTENCIA = 15.0;

  /**
   * Limite normativo de inasistencia: superar el 30% inhabilita al alumno en la
   * unidad didactica e impide el registro de notas.
   */
  public static final double LIMITE_INASISTENCIA_NORMATIVO = 30.0;

  /**
   * Lista todos los profesores registrados en el sistema.
   */
  public List<Profesor> listarProfesores() {
    return ArchivoManager.leerProfesores();
  }

  /**
   * Lista todos los estudiantes registrados en el sistema.
   */
  public List<Estudiante> listarEstudiantes() {
    return ArchivoManager.leerEstudiantes();
  }

  /**
   * Busca un profesor por su identificador. Devuelve null si no existe.
   */
  public Profesor buscarProfesor(String idProfesor) {
    if (idProfesor == null) {
      return null;
    }
    for (Profesor profesor : ArchivoManager.leerProfesores()) {
      if (profesor.getId().equals(idProfesor)) {
        return profesor;
      }
    }
    return null;
  }

  /**
   * Busca un estudiante por su identificador. Devuelve null si no existe.
   */
  public Estudiante buscarEstudiante(String idEstudiante) {
    if (idEstudiante == null) {
      return null;
    }
    for (Estudiante estudiante : ArchivoManager.leerEstudiantes()) {
      if (estudiante.getId().equals(idEstudiante)) {
        return estudiante;
      }
    }
    return null;
  }

  /**
   * Lista todos los cursos registrados en el sistema.
   */
  public List<Curso> listarCursos() {
    return ArchivoManager.leerCursos();
  }

  /**
   * Lista las carreras / programas de estudio registrados.
   */
  public List<Carrera> listarCarreras() {
    return ArchivoManager.leerCarreras();
  }

  /**
   * Lista los planes de estudio registrados.
   */
  public List<PlanEstudios> listarPlanesEstudios() {
    return ArchivoManager.leerPlanesEstudios();
  }

  /**
   * Lista los modulos profesionales registrados.
   */
  public List<ModuloProfesional> listarModulosProfesionales() {
    return ArchivoManager.leerModulosProfesionales();
  }

  /**
   * Lista los periodos academicos registrados.
   */
  public List<PeriodoAcademico> listarPeriodosAcademicos() {
    return ArchivoManager.leerPeriodosAcademicos();
  }

  /**
   * Lista los bloques de seccion / turno registrados.
   */
  public List<BloqueSeccion> listarBloques() {
    return ArchivoManager.leerBloques();
  }

  /**
   * Lista las secciones de curso registradas.
   */
  public List<SeccionCurso> listarSecciones() {
    return ArchivoManager.leerSecciones();
  }

  /**
   * Devuelve las secciones que pertenecen a un bloque.
   */
  public List<SeccionCurso> listarSeccionesBloque(String idBloque) {
    List<SeccionCurso> secciones = new ArrayList<>();
    for (SeccionCurso seccion : ArchivoManager.leerSecciones()) {
      if (seccion.getIdBloque().equals(idBloque)) {
        secciones.add(seccion);
      }
    }
    return secciones;
  }

  /**
   * Lista las franjas horarias registradas.
   */
  public List<HorarioSeccion> listarHorarios() {
    return ArchivoManager.leerHorarios();
  }

  /**
   * Devuelve las franjas horarias de una seccion.
   */
  public List<HorarioSeccion> listarHorariosSeccion(String idSeccion) {
    List<HorarioSeccion> horarios = new ArrayList<>();
    for (HorarioSeccion horario : ArchivoManager.leerHorarios()) {
      if (horario.getIdSeccion().equals(idSeccion)) {
        horarios.add(horario);
      }
    }
    return horarios;
  }

  /**
   * Lista las sesiones de clase registradas.
   */
  public List<SesionClase> listarSesiones() {
    return ArchivoManager.leerSesiones();
  }

  /**
   * Devuelve las sesiones generadas por una franja horaria.
   */
  public List<SesionClase> listarSesionesHorario(String idHorario) {
    List<SesionClase> sesiones = new ArrayList<>();
    for (SesionClase sesion : ArchivoManager.leerSesiones()) {
      if (sesion.getIdHorario().equals(idHorario)) {
        sesiones.add(sesion);
      }
    }
    return sesiones;
  }

  /**
   * Lista los salones, laboratorios y talleres registrados.
   */
  public List<Salon> listarSalones() {
    return ArchivoManager.leerSalones();
  }

  /**
   * Lista las matriculas registradas.
   */
  public List<Matricula> listarMatriculas() {
    return ArchivoManager.leerMatriculas();
  }

  /**
   * Devuelve las matriculas de un bloque de seccion.
   */
  public List<Matricula> listarMatriculasBloque(String idBloque) {
    List<Matricula> matriculas = new ArrayList<>();
    for (Matricula matricula : ArchivoManager.leerMatriculas()) {
      if (matricula.getIdBloque().equals(idBloque)) {
        matriculas.add(matricula);
      }
    }
    return matriculas;
  }

  /**
   * Devuelve las matriculas de un estudiante.
   */
  public List<Matricula> listarMatriculasEstudiante(String idEstudiante) {
    List<Matricula> matriculas = new ArrayList<>();
    for (Matricula matricula : ArchivoManager.leerMatriculas()) {
      if (matricula.getIdEstudiante().equals(idEstudiante)) {
        matriculas.add(matricula);
      }
    }
    return matriculas;
  }

  /**
   * Busca la matricula activa de un estudiante en un bloque. Devuelve null si no
   * existe.
   */
  public Matricula buscarMatricula(String idEstudiante, String idBloque) {
    if (idEstudiante == null || idBloque == null) {
      return null;
    }
    for (Matricula matricula : ArchivoManager.leerMatriculas()) {
      if (matricula.getIdEstudiante().equals(idEstudiante) && matricula.getIdBloque().equals(idBloque)
          && matricula.getEstado() == EstadoMatricula.ACTIVA) {
        return matricula;
      }
    }
    return null;
  }

  /**
   * Matricula a un estudiante en un bloque de seccion. Si ya tiene una matricula
   * activa devuelve la existente sin duplicar filas. Devuelve null si el
   * estudiante o el bloque no existen.
   */
  public Matricula matricularEstudiante(String idEstudiante, String idBloque, String fechaMatricula) {
    if (buscarEstudiante(idEstudiante) == null || !existeBloque(idBloque)) {
      return null;
    }
    Matricula existente = buscarMatricula(idEstudiante, idBloque);
    if (existente != null) {
      return existente;
    }
    List<Matricula> matriculas = ArchivoManager.leerMatriculas();
    int maxId = 0;
    for (Matricula matricula : matriculas) {
      try {
        maxId = Math.max(maxId, Integer.parseInt(matricula.getIdMatricula().replace("M", "")));
      } catch (NumberFormatException e) {
        // Ignorar identificadores con formato distinto
      }
    }
    Matricula nueva = new Matricula("M" + (maxId + 1), idEstudiante, idBloque, fechaMatricula,
        EstadoMatricula.ACTIVA);
    matriculas.add(nueva);
    ArchivoManager.guardarMatriculas(matriculas);
    return nueva;
  }

  /**
   * Devuelve las sesiones de clase que pertenecen a un bloque de seccion.
   */
  public List<SesionClase> listarSesionesBloque(String idBloque) {
    List<SesionClase> sesiones = new ArrayList<>();
    for (SesionClase sesion : ArchivoManager.leerSesiones()) {
      SeccionCurso seccion = seccionDeSesion(sesion);
      if (seccion != null && seccion.getIdBloque().equals(idBloque)) {
        sesiones.add(sesion);
      }
    }
    return sesiones;
  }

  /**
   * Crea un registro de asistencia para una sesion concreta. Las horas se
   * limitan entre 0 y las horas programadas de la sesion; 0 significa ausente.
   */
  public RegistroDeAsistencia crearRegistroSesion(SesionClase sesion, Curso curso, Estudiante estudiante,
      int horasAsistidas) {
    int horasValidas = Math.max(0, Math.min(horasAsistidas, sesion.getHorasProgramadas()));
    boolean presente = horasValidas > 0;
    return new RegistroDeAsistencia(null, curso.getIdCurso(), estudiante.getId(), sesion.getFecha(), horasValidas,
        presente, sesion.getIdSesion());
  }

  /**
   * Crea el registro de asistencia de un estudiante en una sesion, validando que
   * tenga matricula activa en el bloque de la sesion. Devuelve null si la sesion
   * no existe o el estudiante no esta matriculado.
   */
  public RegistroDeAsistencia registrarAsistenciaSesion(String idSesion, String idEstudiante, int horasAsistidas) {
    SesionClase sesion = buscarSesion(idSesion);
    if (sesion == null) {
      return null;
    }
    SeccionCurso seccion = seccionDeSesion(sesion);
    if (seccion == null) {
      return null;
    }
    Curso curso = buscarCurso(seccion.getIdCurso());
    Estudiante estudiante = buscarEstudiante(idEstudiante);
    if (curso == null || estudiante == null) {
      return null;
    }
    if (buscarMatricula(idEstudiante, seccion.getIdBloque()) == null) {
      return null;
    }
    return crearRegistroSesion(sesion, curso, estudiante, horasAsistidas);
  }

  /**
   * Suma las horas faltadas de las sesiones que ya tienen asistencia tomada:
   * suma(horas_programadas - horas_asistidas). Las sesiones sin registro se
   * consideran no dictadas y no acumulan faltas.
   */
  public int calcularHorasFaltadas(List<SesionClase> sesionesProgramadas, List<RegistroDeAsistencia> registros) {
    int faltadas = 0;
    for (SesionClase sesion : sesionesProgramadas) {
      RegistroDeAsistencia registro = null;
      for (RegistroDeAsistencia candidato : registros) {
        if (sesion.getIdSesion().equals(candidato.getIdSesion())) {
          registro = candidato;
          break;
        }
      }
      if (registro == null) {
        continue;
      }
      int noAsistidas = sesion.getHorasProgramadas() - registro.getHorasAsistidas();
      if (noAsistidas > 0) {
        faltadas += noAsistidas;
      }
    }
    return faltadas;
  }

  /**
   * Horas faltadas de una unidad didactica para una matricula concreta, usando
   * las sesiones del bloque y los registros del estudiante.
   */
  public int horasFaltadasUnidadDidactica(Matricula matricula, Curso curso) {
    if (matricula == null || curso == null) {
      return 0;
    }
    List<SesionClase> sesiones = new ArrayList<>();
    for (SesionClase sesion : listarSesionesBloque(matricula.getIdBloque())) {
      SeccionCurso seccion = seccionDeSesion(sesion);
      if (seccion != null && seccion.getIdCurso().equals(curso.getIdCurso())) {
        sesiones.add(sesion);
      }
    }
    return calcularHorasFaltadas(sesiones, ArchivoManager.leerAsistenciasEstudiante(matricula.getIdEstudiante()));
  }

  /**
   * Porcentaje normativo de inasistencia:
   * (horas faltadas / horas lectivas totales de la unidad didactica) * 100.
   */
  public double calcularPorcentajeInasistencia(int horasFaltadas, int horasLectivasTotales) {
    if (horasLectivasTotales <= 0) {
      return 0.0;
    }
    int faltadas = Math.max(0, horasFaltadas);
    return ((double) faltadas / horasLectivasTotales) * 100;
  }

  /**
   * Indica si la inasistencia supera el limite normativo del 30%.
   */
  public boolean estaInhabilitadoPorInasistencia(int horasFaltadas, int horasLectivasTotales) {
    return calcularPorcentajeInasistencia(horasFaltadas, horasLectivasTotales) > LIMITE_INASISTENCIA_NORMATIVO;
  }

  /**
   * Indica si el estudiante puede registrar notas en una unidad didactica.
   * Requiere matricula activa y no superar el 30% de inasistencia.
   */
  public boolean puedeRegistrarNotas(Matricula matricula, Curso curso) {
    if (matricula == null || curso == null || matricula.getEstado() != EstadoMatricula.ACTIVA) {
      return false;
    }
    int horasFaltadas = horasFaltadasUnidadDidactica(matricula, curso);
    return !estaInhabilitadoPorInasistencia(horasFaltadas, curso.getHorasLectivasTotales());
  }

  /**
   * Devuelve los cursos asignados a un profesor.
   */
  public List<Curso> listarCursosProfesor(Profesor profesor) {
    List<Curso> cursos = new ArrayList<>();
    for (Curso curso : ArchivoManager.leerCursos()) {
      if (curso.getIdProfesor().equals(profesor.getId())) {
        cursos.add(curso);
      }
    }
    return cursos;
  }

  /**
   * Devuelve los estudiantes inscritos en un curso.
   */
  public List<Estudiante> listarEstudiantesCurso(Curso curso) {
    List<Estudiante> alumnos = new ArrayList<>();
    for (String idEstudiante : ArchivoManager.leerInscripcionesCurso(curso.getIdCurso())) {
      for (Estudiante estudiante : ArchivoManager.leerEstudiantes()) {
        if (estudiante.getId().equals(idEstudiante)) {
          alumnos.add(estudiante);
        }
      }
    }
    return alumnos;
  }

  /**
   * Devuelve los cursos en los que esta inscrito un estudiante.
   */
  public List<Curso> listarCursosEstudiante(Estudiante estudiante) {
    List<Curso> cursos = new ArrayList<>();
    for (Curso curso : ArchivoManager.leerCursos()) {
      if (ArchivoManager.leerInscripcionesCurso(curso.getIdCurso()).contains(estudiante.getId())) {
        cursos.add(curso);
      }
    }
    return cursos;
  }

  /**
   * Devuelve el historial de asistencias de un estudiante.
   */
  public List<RegistroDeAsistencia> historialEstudiante(Estudiante estudiante) {
    return ArchivoManager.leerAsistenciasEstudiante(estudiante.getId());
  }

  /**
   * Guarda el lote de registros de asistencia en el archivo CSV.
   * Asigna un identificador consecutivo a cada registro del lote antes de guardar.
   */
  public void guardarAsistencias(List<RegistroDeAsistencia> registros) {
    int maxId = 0;
    for (RegistroDeAsistencia reg : ArchivoManager.leerTodasAsistencias()) {
      try {
        maxId = Math.max(maxId, Integer.parseInt(reg.getIdAsistencia().replace("A", "")));
      } catch (NumberFormatException e) {
        // Ignorar identificadores con formato distinto
      }
    }
    int siguienteId = maxId + 1;
    for (RegistroDeAsistencia reg : registros) {
      reg.setIdAsistencia("A" + siguienteId);
      siguienteId++;
    }
    ArchivoManager.guardarAsistencia(registros);
  }

  /**
   * Crea un registro de asistencia a partir de las horas asistidas.
   * Las horas se limitan entre 0 y el maximo de la sesion; 0 significa ausente.
   * El identificador se asigna al guardar el lote.
   */
  public RegistroDeAsistencia crearRegistro(Curso curso, Estudiante estudiante, String fecha, int horasAsistidas) {
    int horasValidas = Math.max(0, Math.min(horasAsistidas, curso.getTotalHorasSesion()));
    boolean presente = horasValidas > 0;
    return new RegistroDeAsistencia(null, curso.getIdCurso(), estudiante.getId(), fecha, horasValidas, presente);
  }

  /**
   * Calcula el porcentaje de inasistencia global con base en el total del curso:
   * (horas dictadas hasta la fecha - horas asistidas) / (horas totales del curso) * 100.
   */
  public double calcularPorcentajeFaltas(int horasDictadas, int horasAsistidas, int totalHorasCurso) {
    if (totalHorasCurso <= 0) {
      return 0.0;
    }
    int horasNoAsistidas = horasDictadas - horasAsistidas;
    if (horasNoAsistidas < 0) {
      horasNoAsistidas = 0;
    }
    return ((double) horasNoAsistidas / totalHorasCurso) * 100;
  }

  /**
   * Resuelve la seccion de curso a la que pertenece una sesion.
   */
  private SeccionCurso seccionDeSesion(SesionClase sesion) {
    if (sesion == null) {
      return null;
    }
    HorarioSeccion horario = buscarHorario(sesion.getIdHorario());
    if (horario == null) {
      return null;
    }
    return buscarSeccion(horario.getIdSeccion());
  }

  private SesionClase buscarSesion(String idSesion) {
    if (idSesion == null) {
      return null;
    }
    for (SesionClase sesion : ArchivoManager.leerSesiones()) {
      if (sesion.getIdSesion().equals(idSesion)) {
        return sesion;
      }
    }
    return null;
  }

  private HorarioSeccion buscarHorario(String idHorario) {
    if (idHorario == null) {
      return null;
    }
    for (HorarioSeccion horario : ArchivoManager.leerHorarios()) {
      if (horario.getIdHorario().equals(idHorario)) {
        return horario;
      }
    }
    return null;
  }

  private SeccionCurso buscarSeccion(String idSeccion) {
    if (idSeccion == null) {
      return null;
    }
    for (SeccionCurso seccion : ArchivoManager.leerSecciones()) {
      if (seccion.getIdSeccion().equals(idSeccion)) {
        return seccion;
      }
    }
    return null;
  }

  private Curso buscarCurso(String idCurso) {
    if (idCurso == null) {
      return null;
    }
    for (Curso curso : ArchivoManager.leerCursos()) {
      if (curso.getIdCurso().equals(idCurso)) {
        return curso;
      }
    }
    return null;
  }

  private boolean existeBloque(String idBloque) {
    if (idBloque == null) {
      return false;
    }
    for (BloqueSeccion bloque : ArchivoManager.leerBloques()) {
      if (bloque.getIdBloque().equals(idBloque)) {
        return true;
      }
    }
    return false;
  }
}
