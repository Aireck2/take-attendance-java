package modelo;

/**
 * Bloque de seccion: paquete de unidades didacticas de un mismo ciclo
 * dentro de un periodo academico.
 */
public class BloqueSeccion {
  private final String idBloque;
  private final String idPeriodo;
  private final String nombre;

  public BloqueSeccion(String idBloque, String idPeriodo, String nombre) {
    this.idBloque = idBloque;
    this.idPeriodo = idPeriodo;
    this.nombre = nombre;
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

  public String getCiclo() {
    if (nombre == null) {
      return "";
    }
    int separador = nombre.indexOf(" - ");
    return separador > 0 ? nombre.substring(0, separador) : nombre;
  }
}
