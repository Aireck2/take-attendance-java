package models;

/**
 * Periodo academico: contexto temporal del dictado con rango calendario y
 * cantidad oficial de semanas lectivas, divisor para planificar las horas
 * semanales de las unidades del periodo.
 */
public class PeriodoAcademico {
  public static final int DEFAULT_SEMANAS_LECTIVAS = 16;
  private String idPeriodo;
  private String idPlan;
  private String codigo;
  private String fechaInicio;
  private String fechaFin;
  private EstadoPeriodo estado;
  private int semanasLectivas;

  public PeriodoAcademico(String idPeriodo, String idPlan, String codigo, String fechaInicio, String fechaFin,
      EstadoPeriodo estado) {
    this(idPeriodo, idPlan, codigo, fechaInicio, fechaFin, estado, DEFAULT_SEMANAS_LECTIVAS);
  }

  public PeriodoAcademico(String idPeriodo, String idPlan, String codigo, String fechaInicio, String fechaFin,
      EstadoPeriodo estado, int semanasLectivas) {
    if (semanasLectivas <= 0) {
      throw new IllegalArgumentException("El periodo debe definir semanas lectivas positivas");
    }
    this.idPeriodo = idPeriodo;
    this.idPlan = idPlan;
    this.codigo = codigo;
    this.fechaInicio = fechaInicio;
    this.fechaFin = fechaFin;
    this.estado = estado;
    this.semanasLectivas = semanasLectivas;
  }

  public String getIdPeriodo() {
    return idPeriodo;
  }

  public String getIdPlan() {
    return idPlan;
  }

  public String getCodigo() {
    return codigo;
  }

  public String getFechaInicio() {
    return fechaInicio;
  }

  public String getFechaFin() {
    return fechaFin;
  }

  public EstadoPeriodo getEstado() {
    return estado;
  }

  public int getSemanasLectivas() {
    return semanasLectivas;
  }

  /** Calcula las horas semanales requeridas para completar la carga lectiva del curso. */
  public double calcularHorasSemanalesRequeridas(Curso curso) {
    if (curso == null || semanasLectivas <= 0) {
      return 0.0;
    }
    return (double) curso.getTotalHorasLectivas() / semanasLectivas;
  }
}
