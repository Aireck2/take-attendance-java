# Control de Asistencia — IESTP Chincha

Programa de consola en **Java 21 SE** para el control de asistencia estudiantil del IESTP Chincha. Arquitectura limpia por capas, sin dependencias externas, con persistencia en archivos CSV (`data/`) y autenticación por roles con contraseñas PBKDF2.

> [!NOTE]
> El informe técnico completo (caso de estudio, objetivos, análisis y diseño) está en [INFORME.md](INFORME.md).

## Descarga

```bash
git clone git@github.com:aireck2/take-attendance-java.git
cd take-attendance-java
```

## Ejecución

Requisito: **JDK 21** o superior.

1. Compilar:

   ```bash
   javac -d out $(find src/main/java -name "*.java")
   ```

2. Ejecutar:

   ```bash
   java -cp out sistema.SistemaApp
   ```

3. Iniciar sesión con una cuenta de demostración:

   | Rol        | Correo                               | Contraseña     |
   | :--------- | :----------------------------------- | :------------- |
   | Profesor   | `carlos.mendoza@iestpchincha.edu.pe` | `Docente2026*` |
   | Estudiante | `erick.escriba@iestpchincha.edu.pe`  | `Alumno2026*`  |

## Pruebas

```bash
javac -d out $(find src/main/java src/test/java -name "*.java")
java -cp out test.TestRunner
```
