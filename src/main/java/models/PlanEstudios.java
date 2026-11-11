package models;

/**
 * Plan de estudios: fuente curricular que define los totales agregados de
 * creditos y horas lectivas de una promocion. Las unidades y su desglose
 * individual pertenecen a Curso dentro de los modulos del plan.
 */
public class PlanEstudios {
  private String idPlan;
  private String idCarrera;
  private String codigo;
  private String nombre;
  private int creditosTotales;
  private int horasTotales;
  private EstadoPlan estado;

  public PlanEstudios(String idPlan, String idCarrera, String codigo, String nombre, int creditosTotales,
      int horasTotales, EstadoPlan estado) {
    this.idPlan = idPlan;
    this.idCarrera = idCarrera;
    this.codigo = codigo;
    this.nombre = nombre;
    this.creditosTotales = creditosTotales;
    this.horasTotales = horasTotales;
    this.estado = estado;
  }

  public String getIdPlan() {
    return idPlan;
  }

  public String getIdCarrera() {
    return idCarrera;
  }

  public String getCodigo() {
    return codigo;
  }

  public String getNombre() {
    return nombre;
  }

  public int getCreditosTotales() {
    return creditosTotales;
  }

  public int getHorasTotales() {
    return horasTotales;
  }

  public EstadoPlan getEstado() {
    return estado;
  }
}
