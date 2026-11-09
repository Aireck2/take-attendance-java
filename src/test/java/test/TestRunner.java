package test;

import java.util.List;

import models.Estudiante;
import models.Profesor;
import models.Rol;
import models.Usuario;
import security.PasswordHasher;
import service.AutenticacionService;
import service.ServicioAcademico;
import storage.ArchivoManager;

/**
 * Suite de pruebas del sistema, sin dependencias externas.
 * Se ejecuta con: java -cp out test.TestRunner
 */
public class TestRunner {
  private static int total = 0;
  private static int fallos = 0;

  public static void main(String[] args) {
    probarHashDeContrasenas();
    probarLecturaDeUsuarios();
    probarInicioDeSesion();
    probarPerfilesVinculados();

    System.out.println();
    if (fallos == 0) {
      System.out.println("OK: " + total + " pruebas pasaron.");
    } else {
      System.out.println("FALLARON " + fallos + " de " + total + " pruebas.");
      System.exit(1);
    }
  }

  private static void probarHashDeContrasenas() {
    String hash1 = PasswordHasher.hash("secreto");
    String hash2 = PasswordHasher.hash("secreto");

    afirmar(hash1.startsWith("pbkdf2_sha256$"), "el hash usa el prefijo pbkdf2_sha256");
    afirmar(!hash1.equals(hash2), "dos hashes de la misma contrasena usan sales distintas");
    afirmar(PasswordHasher.verificar("secreto", hash1), "verifica la contrasena correcta");
    afirmar(!PasswordHasher.verificar("otra", hash1), "rechaza la contrasena incorrecta");
    afirmar(!PasswordHasher.verificar("secreto", "formato-invalido"), "rechaza un hash con formato invalido");
    afirmar(!PasswordHasher.verificar("secreto", null), "rechaza un hash nulo");
  }

  private static void probarLecturaDeUsuarios() {
    List<Usuario> usuarios = ArchivoManager.leerUsuarios();

    afirmar(usuarios.size() == 10, "lee los 10 usuarios sembrados (encontrados: " + usuarios.size() + ")");
    afirmar(usuarios.stream().allMatch(u -> u.getHashContrasena().startsWith("pbkdf2_sha256$")),
        "ningun usuario guarda la contrasena en texto plano");
  }

  private static void probarInicioDeSesion() {
    AutenticacionService auth = new AutenticacionService();

    Usuario docente = auth.autenticar("carlos.mendoza@usil.pe", "Docente2026*");
    afirmar(docente != null, "autentica al docente con credenciales correctas");
    afirmar(docente != null && docente.getRol() == Rol.PROFESOR, "el docente recibe el rol PROFESOR");

    Usuario alumno = auth.autenticar("  ERICK.ESCRIBA@IESTP.PE  ", "Alumno2026*");
    afirmar(alumno != null, "normaliza el correo (mayusculas y espacios)");
    afirmar(alumno != null && alumno.getRol() == Rol.ESTUDIANTE, "el alumno recibe el rol ESTUDIANTE");

    afirmar(auth.autenticar("carlos.mendoza@usil.pe", "incorrecta") == null, "rechaza una contrasena incorrecta");
    afirmar(auth.autenticar("no.existe@iestp.pe", "Docente2026*") == null, "rechaza un correo desconocido");
    afirmar(auth.autenticar("", "") == null, "rechaza credenciales vacias");
  }

  private static void probarPerfilesVinculados() {
    ServicioAcademico servicio = new ServicioAcademico();

    Profesor profesor = servicio.buscarProfesor("P1");
    Estudiante estudiante = servicio.buscarEstudiante("E1");

    afirmar(profesor != null && profesor.getNombreCompleto().equals("Carlos Mendoza"),
        "resuelve el perfil del docente por id_referencia");
    afirmar(estudiante != null && estudiante.getNombreCompleto().equals("Erick Escriba"),
        "resuelve el perfil del estudiante por id_referencia");
  }

  private static void afirmar(boolean condicion, String descripcion) {
    total++;
    if (!condicion) {
      fallos++;
      System.out.println("FALLO: " + descripcion);
    }
  }
}
