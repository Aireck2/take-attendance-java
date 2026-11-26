package modelo;

/** Salon de clase, laboratorio o taller reservable por una franja horaria. */
public class Salon {
  private final String idSalon;
  private final String nombre;

  public Salon(String idSalon, String nombre) {
    this.idSalon = idSalon;
    this.nombre = nombre;
  }

  public String getIdSalon() {
    return idSalon;
  }

  public String getNombre() {
    return nombre;
  }
}
