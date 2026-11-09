package models;

/**
 * Indicador de logro: criterio evaluativo configurado en una seccion de curso
 * con su peso porcentual dentro de la unidad didactica.
 */
public class IndicadorLogro {
  private String idIndicador;
  private String idSeccion;
  private String nombre;
  private int peso;

  public IndicadorLogro(String idIndicador, String idSeccion, String nombre, int peso) {
    this.idIndicador = idIndicador;
    this.idSeccion = idSeccion;
    this.nombre = nombre;
    this.peso = peso;
  }

  public String getIdIndicador() {
    return idIndicador;
  }

  public String getIdSeccion() {
    return idSeccion;
  }

  public String getNombre() {
    return nombre;
  }

  public int getPeso() {
    return peso;
  }
}
