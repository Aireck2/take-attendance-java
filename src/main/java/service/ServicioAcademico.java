package service;

import java.util.ArrayList;
import java.util.List;

import models.BloqueSeccion;
import models.Carrera;
import models.ComprobantePago;
import models.Curso;
import models.DerechoPago;
import models.Estudiante;
import models.EstadoComprobante;
import models.EstadoMatricula;
import models.HorarioSeccion;
import models.IndicadorLogro;
import models.Matricula;
import models.ModuloProfesional;
import models.PeriodoAcademico;
import models.PlanEstudios;
import models.Profesor;
import models.RegistroDeAsistencia;
import models.RegistroNota;
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
    String prefijo = prefijoMatricula(fechaMatricula);
    int maxId = 0;
    for (Matricula matricula : matriculas) {
      String id = matricula.getIdMatricula();
      if (id != null && id.startsWith(prefijo + "-")) {
        try {
          maxId = Math.max(maxId, Integer.parseInt(id.substring(prefijo.length() + 1)));
        } catch (NumberFormatException e) {
          // Ignorar identificadores con formato distinto
        }
      }
    }
    Matricula nueva = new Matricula(String.format("%s-%03d", prefijo, maxId + 1), idEstudiante, idBloque,
        fechaMatricula, EstadoMatricula.ACTIVA);
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
    return crearRegistroSesion(sesion, curso, estudiante, horasAsistidas, null);
  }

  /**
   * Crea un registro de asistencia para una sesion concreta, ligado a la
   * matricula del alumno en el bloque de la sesion.
   */
  public RegistroDeAsistencia crearRegistroSesion(SesionClase sesion, Curso curso, Estudiante estudiante,
      int horasAsistidas, String idMatricula) {
    int horasValidas = Math.max(0, Math.min(horasAsistidas, sesion.getHorasProgramadas()));
    boolean presente = horasValidas > 0;
    return new RegistroDeAsistencia(null, curso.getIdCurso(), estudiante.getId(), sesion.getFecha(), horasValidas,
        presente, sesion.getIdSesion(), idMatricula);
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
    Matricula matricula = buscarMatricula(idEstudiante, seccion.getIdBloque());
    return crearRegistroSesion(sesion, curso, estudiante, horasAsistidas, matricula.getIdMatricula());
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
   * Lista los indicadores de logro registrados.
   */
  public List<IndicadorLogro> listarIndicadores() {
    return ArchivoManager.leerIndicadores();
  }

  /**
   * Devuelve los indicadores de logro configurados en una seccion.
   */
  public List<IndicadorLogro> listarIndicadoresSeccion(String idSeccion) {
    List<IndicadorLogro> indicadores = new ArrayList<>();
    for (IndicadorLogro indicador : ArchivoManager.leerIndicadores()) {
      if (indicador.getIdSeccion().equals(idSeccion)) {
        indicadores.add(indicador);
      }
    }
    return indicadores;
  }

  /**
   * Lista los registros de notas.
   */
  public List<RegistroNota> listarNotas() {
    return ArchivoManager.leerNotas();
  }

  /**
   * Devuelve las notas de un estudiante.
   */
  public List<RegistroNota> listarNotasEstudiante(String idEstudiante) {
    List<RegistroNota> notas = new ArrayList<>();
    for (RegistroNota nota : ArchivoManager.leerNotas()) {
      if (nota.getIdEstudiante().equals(idEstudiante)) {
        notas.add(nota);
      }
    }
    return notas;
  }

  /**
   * Devuelve las notas registradas en un indicador de logro.
   */
  public List<RegistroNota> listarNotasIndicador(String idIndicador) {
    List<RegistroNota> notas = new ArrayList<>();
    for (RegistroNota nota : ArchivoManager.leerNotas()) {
      if (nota.getIdIndicador().equals(idIndicador)) {
        notas.add(nota);
      }
    }
    return notas;
  }

  /**
   * Indica si un estudiante puede registrar una nota, combinando la matricula
   * activa con la regla normativa del 30% sobre datos ya cargados.
   */
  public boolean puedeRegistrarNota(Matricula matricula, Curso curso, List<SesionClase> sesiones,
      List<RegistroDeAsistencia> registros) {
    if (matricula == null || curso == null || matricula.getEstado() != EstadoMatricula.ACTIVA) {
      return false;
    }
    int horasFaltadas = calcularHorasFaltadas(sesiones, registros);
    return !estaInhabilitadoPorInasistencia(horasFaltadas, curso.getHorasLectivasTotales());
  }

  /**
   * Resuelve el indicador, la seccion, la unidad didactica y la matricula del
   * estudiante para decidir si puede registrar la nota.
   */
  public boolean puedeRegistrarNota(String idIndicador, String idEstudiante) {
    IndicadorLogro indicador = buscarIndicador(idIndicador);
    if (indicador == null) {
      return false;
    }
    SeccionCurso seccion = buscarSeccion(indicador.getIdSeccion());
    if (seccion == null) {
      return false;
    }
    Curso curso = buscarCurso(seccion.getIdCurso());
    Matricula matricula = buscarMatricula(idEstudiante, seccion.getIdBloque());
    if (curso == null || matricula == null) {
      return false;
    }
    List<SesionClase> sesiones = new ArrayList<>();
    for (SesionClase sesion : listarSesionesBloque(seccion.getIdBloque())) {
      SeccionCurso seccionDeSesion = seccionDeSesion(sesion);
      if (seccionDeSesion != null && seccionDeSesion.getIdCurso().equals(curso.getIdCurso())) {
        sesiones.add(sesion);
      }
    }
    return puedeRegistrarNota(matricula, curso, sesiones,
        ArchivoManager.leerAsistenciasEstudiante(idEstudiante));
  }

  /**
   * Crea un registro de nota vigesimal (0 a 20). Devuelve null si el indicador o
   * el estudiante no existen, si la nota esta fuera de rango o si el alumno
   * supera el 30% de inasistencia.
   */
  public RegistroNota crearRegistroNota(String idIndicador, String idEstudiante, int nota, String fecha) {
    if (buscarIndicador(idIndicador) == null || nota < 0 || nota > 20) {
      return null;
    }
    if (!puedeRegistrarNota(idIndicador, idEstudiante)) {
      return null;
    }
    return new RegistroNota(null, idIndicador, idEstudiante, nota, fecha);
  }

  /**
   * Guarda las notas en el CSV. Una nota existente del mismo indicador y
   * estudiante se reemplaza en lugar de duplicarse; las nuevas reciben un
   * identificador consecutivo.
   */
  public void guardarNotas(List<RegistroNota> notas) {
    List<RegistroNota> existentes = ArchivoManager.leerNotas();
    for (RegistroNota nota : notas) {
      String prefijo = prefijoAnual("NOT-", nota.getFecha());
      int maxId = 0;
      for (RegistroNota existente : existentes) {
        String id = existente.getIdRegistroNota();
        if (id != null && id.startsWith(prefijo + "-")) {
          try {
            maxId = Math.max(maxId, Integer.parseInt(id.substring(prefijo.length() + 1)));
          } catch (NumberFormatException e) {
            // Ignorar identificadores con formato distinto
          }
        }
      }
      RegistroNota previa = null;
      for (RegistroNota candidata : existentes) {
        if (candidata.getIdIndicador().equals(nota.getIdIndicador())
            && candidata.getIdEstudiante().equals(nota.getIdEstudiante())) {
          previa = candidata;
          break;
        }
      }
      if (previa != null) {
        existentes.remove(previa);
        nota.setIdRegistroNota(previa.getIdRegistroNota());
      } else {
        nota.setIdRegistroNota(String.format("%s-%03d", prefijo, maxId + 1));
      }
      existentes.add(nota);
    }
    ArchivoManager.guardarNotas(existentes);
  }

  /**
   * Lista los derechos de pago normados.
   */
  public List<DerechoPago> listarDerechosPago() {
    return ArchivoManager.leerDerechosPago();
  }

  /**
   * Lista los comprobantes de pago emitidos.
   */
  public List<ComprobantePago> listarComprobantes() {
    return ArchivoManager.leerComprobantes();
  }

  /**
   * Devuelve los comprobantes de pago de un estudiante.
   */
  public List<ComprobantePago> listarComprobantesEstudiante(String idEstudiante) {
    List<ComprobantePago> comprobantes = new ArrayList<>();
    for (ComprobantePago comprobante : ArchivoManager.leerComprobantes()) {
      if (comprobante.getIdEstudiante().equals(idEstudiante)) {
        comprobantes.add(comprobante);
      }
    }
    return comprobantes;
  }

  /**
   * Saldo pendiente de un derecho de pago para un estudiante: monto del derecho
   * menos la suma de sus comprobantes.
   */
  public double saldoPendiente(String idEstudiante, String idDerecho) {
    DerechoPago derecho = buscarDerechoPago(idDerecho);
    if (derecho == null) {
      return 0.0;
    }
    double pagado = 0.0;
    for (ComprobantePago comprobante : ArchivoManager.leerComprobantes()) {
      if (comprobante.getIdEstudiante().equals(idEstudiante) && comprobante.getIdDerecho().equals(idDerecho)) {
        pagado += comprobante.getMonto();
      }
    }
    return Math.max(0.0, derecho.getMonto() - pagado);
  }

  /**
   * Crea un comprobante de pago que amortiza un derecho. Devuelve null si el
   * estudiante o el derecho no existen, si el monto no es positivo o si supera
   * el saldo pendiente.
   */
  public ComprobantePago crearComprobantePago(String idEstudiante, String idDerecho, String fecha, double monto) {
    if (buscarEstudiante(idEstudiante) == null || buscarDerechoPago(idDerecho) == null) {
      return null;
    }
    if (monto <= 0 || monto > saldoPendiente(idEstudiante, idDerecho)) {
      return null;
    }
    return new ComprobantePago(null, idEstudiante, idDerecho, fecha, monto, EstadoComprobante.PAGADO);
  }

  /**
   * Guarda los comprobantes de pago en el CSV asignando un identificador
   * consecutivo a cada uno.
   */
  public void guardarComprobantes(List<ComprobantePago> comprobantes) {
    List<ComprobantePago> existentes = ArchivoManager.leerComprobantes();
    for (ComprobantePago comprobante : comprobantes) {
      String prefijo = prefijoAnual("REC-", comprobante.getFecha());
      int maxId = 0;
      for (ComprobantePago existente : existentes) {
        String id = existente.getIdComprobante();
        if (id != null && id.startsWith(prefijo + "-")) {
          try {
            maxId = Math.max(maxId, Integer.parseInt(id.substring(prefijo.length() + 1)));
          } catch (NumberFormatException e) {
            // Ignorar identificadores con formato distinto
          }
        }
      }
      comprobante.setIdComprobante(String.format("%s-%04d", prefijo, maxId + 1));
      existentes.add(comprobante);
    }
    ArchivoManager.guardarComprobantes(existentes);
  }

  /**
   * Devuelve los cursos asignados a un profesor, derivados de las franjas
   * horarias de las secciones que dicta.
   */
  public List<Curso> listarCursosProfesor(Profesor profesor) {
    List<Curso> cursos = new ArrayList<>();
    if (profesor == null) {
      return cursos;
    }
    for (HorarioSeccion horario : ArchivoManager.leerHorarios()) {
      if (!profesor.getId().equals(horario.getIdProfesor())) {
        continue;
      }
      SeccionCurso seccion = buscarSeccion(horario.getIdSeccion());
      if (seccion == null) {
        continue;
      }
      Curso curso = buscarCurso(seccion.getIdCurso());
      if (curso == null) {
        continue;
      }
      boolean yaAgregado = false;
      for (Curso candidato : cursos) {
        if (candidato.getIdCurso().equals(curso.getIdCurso())) {
          yaAgregado = true;
          break;
        }
      }
      if (!yaAgregado) {
        cursos.add(curso);
      }
    }
    return cursos;
  }

  /**
   * Devuelve la nomina de un curso, derivada de las secciones que lo dictan y
   * de las matriculas activas del bloque al que pertenecen esas secciones.
   */
  public List<Estudiante> listarEstudiantesCurso(Curso curso) {
    List<Estudiante> alumnos = new ArrayList<>();
    if (curso == null) {
      return alumnos;
    }
    List<Estudiante> estudiantes = ArchivoManager.leerEstudiantes();
    List<Matricula> matriculas = ArchivoManager.leerMatriculas();
    for (SeccionCurso seccion : ArchivoManager.leerSecciones()) {
      if (!curso.getIdCurso().equals(seccion.getIdCurso())) {
        continue;
      }
      for (Matricula matricula : matriculas) {
        if (matricula.getEstado() != EstadoMatricula.ACTIVA
            || !matricula.getIdBloque().equals(seccion.getIdBloque())) {
          continue;
        }
        Estudiante estudiante = buscarEstudianteEn(estudiantes, matricula.getIdEstudiante());
        if (estudiante != null && buscarEstudianteEn(alumnos, estudiante.getId()) == null) {
          alumnos.add(estudiante);
        }
      }
    }
    return alumnos;
  }

  /**
   * Devuelve los cursos de un estudiante, derivados de sus matriculas activas
   * y de las secciones del bloque en el que esta matriculado.
   */
  public List<Curso> listarCursosEstudiante(Estudiante estudiante) {
    List<Curso> cursos = new ArrayList<>();
    if (estudiante == null) {
      return cursos;
    }
    List<SeccionCurso> secciones = ArchivoManager.leerSecciones();
    for (Matricula matricula : ArchivoManager.leerMatriculas()) {
      if (matricula.getEstado() != EstadoMatricula.ACTIVA
          || !estudiante.getId().equals(matricula.getIdEstudiante())) {
        continue;
      }
      for (SeccionCurso seccion : secciones) {
        if (!matricula.getIdBloque().equals(seccion.getIdBloque())) {
          continue;
        }
        Curso curso = buscarCurso(seccion.getIdCurso());
        if (curso != null && !contieneCurso(cursos, curso.getIdCurso())) {
          cursos.add(curso);
        }
      }
    }
    return cursos;
  }

  private boolean contieneCurso(List<Curso> cursos, String idCurso) {
    for (Curso curso : cursos) {
      if (curso.getIdCurso().equals(idCurso)) {
        return true;
      }
    }
    return false;
  }

  private Estudiante buscarEstudianteEn(List<Estudiante> estudiantes, String idEstudiante) {
    for (Estudiante estudiante : estudiantes) {
      if (estudiante.getId().equals(idEstudiante)) {
        return estudiante;
      }
    }
    return null;
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
    List<RegistroDeAsistencia> existentes = ArchivoManager.leerTodasAsistencias();
    int maxId = 0;
    for (RegistroDeAsistencia reg : existentes) {
      String id = reg.getIdAsistencia();
      if (id != null && id.startsWith("REG-ASI-")) {
        try {
          maxId = Math.max(maxId, Integer.parseInt(id.substring("REG-ASI-".length())));
        } catch (NumberFormatException e) {
          // Ignorar identificadores con formato distinto
        }
      }
    }
    int siguienteId = maxId + 1;
    for (RegistroDeAsistencia reg : registros) {
      reg.setIdAsistencia(String.format("REG-ASI-%04d", siguienteId));
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

  private IndicadorLogro buscarIndicador(String idIndicador) {
    if (idIndicador == null) {
      return null;
    }
    for (IndicadorLogro indicador : ArchivoManager.leerIndicadores()) {
      if (indicador.getIdIndicador().equals(idIndicador)) {
        return indicador;
      }
    }
    return null;
  }

  private DerechoPago buscarDerechoPago(String idDerecho) {
    if (idDerecho == null) {
      return null;
    }
    for (DerechoPago derecho : ArchivoManager.leerDerechosPago()) {
      if (derecho.getIdDerecho().equals(idDerecho)) {
        return derecho;
      }
    }
    return null;
  }

  /**
   * Prefijo anual de un identificador (por ejemplo NOT-2026) derivado de una
   * fecha en formato ISO. Si la fecha no es valida usa el anio por defecto.
   */
  private static String prefijoAnual(String base, String fecha) {
    String anio = "2026";
    if (fecha != null && fecha.length() >= 4) {
      String candidato = fecha.substring(0, 4);
      try {
        Integer.parseInt(candidato);
        anio = candidato;
      } catch (NumberFormatException e) {
        // Mantener el anio por defecto
      }
    }
    return base + anio;
  }

  /**
   * Prefijo de matricula (por ejemplo MAT-20261) derivado del anio y semestre
   * de la fecha de matricula.
   */
  private static String prefijoMatricula(String fechaMatricula) {
    String anio = "2026";
    int semestre = 1;
    if (fechaMatricula != null && fechaMatricula.length() >= 7) {
      try {
        anio = fechaMatricula.substring(0, 4);
        Integer.parseInt(anio);
        int mes = Integer.parseInt(fechaMatricula.substring(5, 7));
        semestre = mes <= 6 ? 1 : 2;
      } catch (NumberFormatException e) {
        anio = "2026";
        semestre = 1;
      }
    }
    return "MAT-" + anio + semestre;
  }
}
