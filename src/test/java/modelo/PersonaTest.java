package modelo;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.lang.reflect.Modifier;
import java.nio.charset.StandardCharsets;

import app.App;

/** Pruebas unitarias de la jerarquía Persona y visualización de perfiles en App. */
public final class PersonaTest {
  private PersonaTest() {
  }

  public static void ejecutar() {
    probarJerarquiaPersonaYSinMostrarPerfilEnModelo();
    probarVisualizacionPerfilesEnApp();
  }

  private static void probarJerarquiaPersonaYSinMostrarPerfilEnModelo() {
    if (!Modifier.isAbstract(Persona.class.getModifiers())) {
      throw new AssertionError("Persona debe ser una clase abstracta");
    }
    Persona estudiante = new Estudiante("ALU-1", "U001", "Ana", "Gomez", "70000001", "ana@iestp.edu.pe", "REGULAR");
    Persona profesor = new Profesor("DOC-1", "Luis", "Perez", "luis@iestp.edu.pe", "DNI", "10000001", 999888777,
        "Ingenieria", "Redes");

    if (!"Ana Gomez".equals(estudiante.getNombreCompleto()) || !"ana@iestp.edu.pe".equals(estudiante.getCorreo())) {
      throw new AssertionError("Estudiante no hereda correctamente los datos de Persona");
    }
    if (!"Luis Perez".equals(profesor.getNombreCompleto()) || !"DOC-1".equals(profesor.getId())) {
      throw new AssertionError("Profesor no hereda correctamente los datos de Persona");
    }
    verificarSinMetodoMostrarPerfil(Estudiante.class);
    verificarSinMetodoMostrarPerfil(Profesor.class);
  }

  private static void verificarSinMetodoMostrarPerfil(Class<?> tipo) {
    for (var metodo : tipo.getDeclaredMethods()) {
      if ("mostrarPerfil".equals(metodo.getName())) {
        throw new AssertionError(tipo.getSimpleName() + " no debe acoplarse a consola con mostrarPerfil()");
      }
    }
  }

  private static void probarVisualizacionPerfilesEnApp() {
    var entradaOriginal = System.in;
    var salidaOriginal = System.out;
    ByteArrayOutputStream salida = new ByteArrayOutputStream();
    String entrada = String.join("\n",
        "1", "carlos.mendoza@iestpchincha.edu.pe", "Docente2026*", "2", "3",
        "1", "erick.escriba@iestpchincha.edu.pe", "Alumno2026*", "2", "3",
        "2", "");
    try {
      System.setIn(new ByteArrayInputStream(entrada.getBytes(StandardCharsets.UTF_8)));
      System.setOut(new PrintStream(salida, true, StandardCharsets.UTF_8));
      new App().iniciar();
    } finally {
      System.setIn(entradaOriginal);
      System.setOut(salidaOriginal);
    }
    String texto = salida.toString(StandardCharsets.UTF_8);
    if (!texto.contains("PERFIL DEL PROFESOR") || !texto.contains("Especialidad: Ciberseguridad")) {
      throw new AssertionError("App no mostró correctamente el perfil del profesor");
    }
    if (!texto.contains("PERFIL DEL ESTUDIANTE") || !texto.contains("Codigo: U2024101")) {
      throw new AssertionError("App no mostró correctamente el perfil del estudiante");
    }
  }
}
