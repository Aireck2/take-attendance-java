package modelo;

/**
 * Periodo academico: contexto temporal del dictado con rango calendario y
 * cantidad oficial de semanas lectivas.
 */
public class PeriodoAcademico {
  public static final int DEFAULT_SEMANAS_LECTIVAS = 16;
  private final String idPeriodo;
  private final String codigo;
  private final String fechaInicio;
  private final String fechaFin;
  private final EstadoPeriodo estado;
  private final int semanasLectivas;

  public PeriodoAcademico(String idPeriodo, String codigo, String fechaInicio, String fechaFin,
      EstadoPeriodo estado, int semanasLectivas) {
    if (semanasLectivas <= 0) {
      throw new IllegalArgumentException("El periodo debe definir semanas lectivas positivas");
    }
    this.idPeriodo = idPeriodo;
    this.codigo = codigo;
    this.fechaInicio = fechaInicio;
    this.fechaFin = fechaFin;
    this.estado = estado;
    this.semanasLectivas = semanasLectivas;
  }

  public String getIdPeriodo() {
    return idPeriodo;
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

  public double calcularHorasSemanalesRequeridas(Curso curso) {
    if (curso == null || semanasLectivas <= 0) {
      return 0.0;
    }
    return (double) curso.getTotalHorasLectivas() / semanasLectivas;
  }
}
