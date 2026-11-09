package models;

/**
 * Periodo academico: contexto temporal del dictado (por ejemplo 2026-I)
 * con fechas oficiales de inicio y fin de clases.
 */
public class PeriodoAcademico {
  private String idPeriodo;
  private String idPlan;
  private String codigo;
  private String fechaInicio;
  private String fechaFin;
  private EstadoPeriodo estado;

  public PeriodoAcademico(String idPeriodo, String idPlan, String codigo, String fechaInicio, String fechaFin,
      EstadoPeriodo estado) {
    this.idPeriodo = idPeriodo;
    this.idPlan = idPlan;
    this.codigo = codigo;
    this.fechaInicio = fechaInicio;
    this.fechaFin = fechaFin;
    this.estado = estado;
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
}
