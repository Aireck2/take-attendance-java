package models;

/**
 * Modulo profesional: unidad formativa acumulativa de un plan que agrupa
 * competencias tecnicas para certificaciones modulares intermedias.
 */
public class ModuloProfesional {
  private String idModulo;
  private String idPlan;
  private String nombre;
  private String competencia;
  private int creditosModulo;

  public ModuloProfesional(String idModulo, String idPlan, String nombre, String competencia, int creditosModulo) {
    this.idModulo = idModulo;
    this.idPlan = idPlan;
    this.nombre = nombre;
    this.competencia = competencia;
    this.creditosModulo = creditosModulo;
  }

  public String getIdModulo() {
    return idModulo;
  }

  public String getIdPlan() {
    return idPlan;
  }

  public String getNombre() {
    return nombre;
  }

  public String getCompetencia() {
    return competencia;
  }

  public int getCreditosModulo() {
    return creditosModulo;
  }
}
