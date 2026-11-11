package models;

/**
 * Bloque de seccion / turno: paquete rigido de unidades didacticas de un
 * mismo ciclo y turno dentro de un periodo academico.
 */
public class BloqueSeccion {
  private String idBloque;
  private String idPeriodo;
  private String nombre;
  private Turno turno;

  public BloqueSeccion(String idBloque, String idPeriodo, String nombre, Turno turno) {
    this.idBloque = idBloque;
    this.idPeriodo = idPeriodo;
    this.nombre = nombre;
    this.turno = turno;
  }

  public String getIdBloque() {
    return idBloque;
  }

  public String getIdPeriodo() {
    return idPeriodo;
  }

  public String getNombre() {
    return nombre;
  }

  public Turno getTurno() {
    return turno;
  }

  /**
   * Ciclo academico declarado en el nombre del bloque.
   * La convencion del catalogo es "3er Ciclo - Turno Noche"; si el nombre no
   * sigue esa convencion se devuelve completo para no perder informacion.
   */
  public String getCiclo() {
    if (nombre == null) {
      return "";
    }
    int separador = nombre.indexOf(" - ");
    return separador > 0 ? nombre.substring(0, separador) : nombre;
  }
}
