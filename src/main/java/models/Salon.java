package models;

/**
 * Salon de clase, laboratorio o taller reservable por una franja horaria.
 */
public class Salon {
  private String idSalon;
  private String nombre;
  private TipoSalon tipo;
  private int capacidad;

  public Salon(String idSalon, String nombre, TipoSalon tipo, int capacidad) {
    this.idSalon = idSalon;
    this.nombre = nombre;
    this.tipo = tipo;
    this.capacidad = capacidad;
  }

  public String getIdSalon() {
    return idSalon;
  }

  public String getNombre() {
    return nombre;
  }

  public TipoSalon getTipo() {
    return tipo;
  }

  public int getCapacidad() {
    return capacidad;
  }
}
