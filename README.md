# Sistema de Control y Monitoreo de Asistencia — IESTP Chincha

Aplicación de consola desarrollada en **Java 21 SE** para la gestión, control y monitoreo de asistencias académicas en el Instituto de Educación Superior Tecnológico Público Chincha.

El sistema implementa una arquitectura limpia por capas (Separación de Responsabilidades) sin dependencias externas, persistiendo la información en archivos CSV y proporcionando seguimiento analítico de inasistencias en tiempo real.

---

## Funcionalidades

### Autenticación y Roles
- **Inicio de sesión real:** el acceso se valida con correo y contraseña contra `data/usuarios.csv`; ya no se elige un usuario de una lista.
- **Roles:** cada cuenta tiene el rol `PROFESOR` o `ESTUDIANTE`, y el rol determina el menú que se abre.
- **Contraseñas protegidas:** se almacenan con PBKDF2-HMAC-SHA256, sal aleatoria de 16 bytes y 210 000 iteraciones. Nunca se guarda texto plano.
- **Un intento por acceso:** si las credenciales son incorrectas se muestra un mensaje genérico y se vuelve al menú principal; no hay contador de intentos.
- **Contraseña oculta:** en una terminal interactiva se usa `System.console().readPassword()` para no mostrarla.

### Gestión de Profesores
- **Consulta de Perfil:** Visualización detallada de datos personales, departamento académico y especialidad.
- **Cursos Asignados:** Listado de asignaturas a cargo con detalle de carga horaria y lista de alumnos matriculados.
- **Toma de Asistencia Interactiva:** Registro por sesión con captura automática de fecha actual (`LocalDate`). Solo se piden las horas asistidas: `0` significa ausente y el valor se limita al máximo de horas de la sesión.

### Portal del Estudiante
- **Consulta de Perfil:** Estado de matrícula (regular, suspendido, reserva, etc.), código institucional y carrera profesional.
- **Monitoreo de Asistencia:** Balance acumulado por curso indicando sesiones programadas, días asistidos, horas efectivas y porcentaje de inasistencia global.
- **Alertas Tempranas:** Notificación automática en consola si el estudiante supera el límite permitido de inasistencias.

---

## Lógica de Negocio y Reglas de Cálculo

> [!IMPORTANT]
> **Fórmula de Inasistencia:**  
> El cálculo se realiza sobre la duración total del curso:
> $$\text{Porcentaje de Faltas} = \frac{\text{Horas Dictadas} - \text{Horas Asistidas}}{\text{Duración en Días} \times \text{Horas por Sesión}} \times 100$$

> [!WARNING]
> **Límite de Inasistencia (`LIMITE_INASISTENCIA = 15.0%`):**  
> Si el porcentaje de inasistencias excede el 15%, el sistema emite una alerta visual en el resumen del estudiante.

---

## Arquitectura y Estructura del Código

El proyecto está organizado siguiendo el principio de separación de responsabilidades (SoC):

```
src/main/java/
├── models/         # Entidades del dominio y enumeraciones
├── security/       # Derivación y verificación de contraseñas (PBKDF2)
├── storage/        # Capa de persistencia y serialización en archivos planos CSV
├── service/        # Lógica de negocio, autenticación, cálculos de inasistencias y validaciones
└── sistema/        # Orquestación de menús, inicio de sesión, interfaz de consola (CLI) e interacción
```

---

## Diagramas de Clases UML y Relaciones

El modelado técnico orientado a objetos y la arquitectura por capas se encuentran documentados en formato **Mermaid** dentro de la carpeta `docs/`.

### Diagrama General Integrado

El archivo [`docs/Index.mmd`](docs/Index.mmd) centraliza la arquitectura del sistema y documenta las siguientes relaciones UML:

- **Relaciones de Dominio (`models`):**
  - **Asociación directa:** `Estudiante --> EstadoEstudiante` (asociación tipada con la enumeración de estados).
  - **Credenciales y rol:** `Usuario --> Rol` (asociación tipada) y `Usuario ..> Profesor` / `Usuario ..> Estudiante` (referencia desacoplada vía `idReferencia`).
  - **Referencias por clave lógica:** `Curso ..> Profesor` (asociación desacoplada vía `idProfesor`).
  - **Trazabilidad de asistencia:** `RegistroDeAsistencia ..> Estudiante` (vía `idEstudiante`) y `RegistroDeAsistencia ..> Curso` (vía `idCurso`).

- **Relaciones entre Capas (Arquitectura):**
  - **Presentación a Servicio:** `SistemaApp --> ServicioAcademico` (composición/delegación de responsabilidades de la UI hacia el servicio) y `SistemaApp --> AutenticacionService` (inicio de sesión).
  - **Servicio a Persistencia:** `ServicioAcademico ..> ArchivoManager` (dependencia para carga y guardado de datos) y `AutenticacionService ..> ArchivoManager` (lectura de usuarios).
  - **Seguridad:** `AutenticacionService ..> PasswordHasher` (verificación de contraseñas con PBKDF2).

- **Dependencias de Uso y Persistencia:**
  - `ServicioAcademico ..>` Entidades del dominio: Orquesta y realiza cálculos sobre `Profesor`, `Estudiante`, `Curso` y `RegistroDeAsistencia`.
  - `ArchivoManager ..>` Entidades del dominio: Mapea registros entre memoria y archivos CSV (`parsea enum`, `serializa/deserializa CSV`).

### Catálogo de Diagramas

| Directorio | Archivo | Descripción |
| :--- | :--- | :--- |
| `docs/` | [`Index.mmd`](docs/Index.mmd) | **Diagrama UML global** con todos los paquetes, clases y relaciones entre capas. |
| `docs/models/` | [`Models.mmd`](docs/models/Models.mmd) | Diagrama de paquete con entidades del dominio, enumeraciones y relaciones internas. |
| `docs/models/` | `*.mmd` | Diagramas individuales: [`CursoClass.mmd`](docs/models/CursoClass.mmd), [`Estudiante.mmd`](docs/models/Estudiante.mmd), [`Profesor.mmd`](docs/models/Profesor.mmd), [`Usuario.mmd`](docs/models/Usuario.mmd), [`Rol.mmd`](docs/models/Rol.mmd), [`RegistroDeAsistencia.mmd`](docs/models/RegistroDeAsistencia.mmd), [`EstadoEstudiante.mmd`](docs/models/EstadoEstudiante.mmd). |
| `docs/service/` | [`Service.mmd`](docs/service/Service.mmd) | Vista de namespace del paquete `service`. |
| `docs/service/` | [`ServicioAcademico.mmd`](docs/service/ServicioAcademico.mmd) | Especificación de atributos, constantes y métodos de la lógica de negocio. |
| `docs/service/` | [`AutenticacionService.mmd`](docs/service/AutenticacionService.mmd) | Validación de credenciales y búsqueda de usuarios por correo. |
| `docs/security/` | [`Security.mmd`](docs/security/Security.mmd) | Vista de namespace del paquete `security`. |
| `docs/security/` | [`PasswordHasher.mmd`](docs/security/PasswordHasher.mmd) | Derivación y verificación PBKDF2 de contraseñas. |
| `docs/storage/` | [`Storage.mmd`](docs/storage/Storage.mmd) | Vista de namespace del paquete `storage`. |
| `docs/storage/` | [`ArchivoManager.mmd`](docs/storage/ArchivoManager.mmd) | Métodos estáticos de persistencia CSV y colecciones genéricas (`List~T~`). |
| `docs/sistema/` | [`Sistema.mmd`](docs/sistema/Sistema.mmd) | Vista de namespace del paquete `sistema`. |
| `docs/sistema/` | [`SistemaApp.mmd`](docs/sistema/SistemaApp.mmd) | Métodos de control de flujo, menús y captura de entradas por terminal. |

---

## Persistencia de Datos (CSV)

La capa de almacenamiento gestiona archivos delimitados por comas ubicados en `data/`:

| Archivo | Estructura de Campos | Propósito |
| :--- | :--- | :--- |
| `data/profesores.csv` | `id, nombre, apellido, correo, tipo_de_documento, nro_documento, telefono, departamento_academico, especialidad` | Padrón de docentes |
| `data/estudiantes.csv` | `id_estudiante, codigo, nombres, apellidos, correo, escuela_profesional, estado` | Registro de alumnos y condición académica |
| `data/usuarios.csv` | `id, correo, hash_contrasena, rol, id_referencia, activo` | Credenciales de acceso y rol (`PROFESOR` / `ESTUDIANTE`) |
| `data/cursos.csv` | `id_curso, nombre_curso, total_horas_sesion, id_profesor, duracion_curso` | Asignaturas y carga horaria |
| `data/inscripciones.csv` | `id_curso, id_estudiante` | Matrícula de estudiantes por asignatura |
| `data/asistencias.csv` | `id_asistencia, id_curso, id_estudiante, fecha, horas_asistidas, presente` | Bitácora histórica de sesiones tomadas |

---

## Requisitos y Ejecución

### Requisitos
- **Java Development Kit (JDK) 21** o superior.

### Compilación y Ejecución

1. **Compilar el proyecto:**
   ```bash
   javac -d out $(find src/main/java -name "*.java")
   ```

2. **Ejecutar la aplicación:**
   ```bash
   java -cp out sistema.SistemaApp
   ```

---

## Credenciales de Demostración

Los usuarios sembrados en `data/usuarios.csv` usan contraseñas de demostración. Cámbialas antes de usar el sistema con datos reales.

| Rol | Correo | Contraseña |
| :--- | :--- | :--- |
| Profesor | `carlos.mendoza@usil.pe` | `Docente2026*` |
| Profesor | `ana.torres@usil.pe` | `Docente2026*` |
| Profesor | `luis.quispe@usil.pe` | `Docente2026*` |
| Profesor | `maria.huaman@usil.pe` | `Docente2026*` |
| Profesor | `jorge.ramos@usil.pe` | `Docente2026*` |
| Estudiante | `erick.escriba@iestp.pe` | `Alumno2026*` |
| Estudiante | `sofia.gomez@iestp.pe` | `Alumno2026*` |
| Estudiante | `mateo.silva@iestp.pe` | `Alumno2026*` |
| Estudiante | `lucia.cardenas@iestp.pe` | `Alumno2026*` |
| Estudiante | `diego.morales@iestp.pe` | `Alumno2026*` |

> [!NOTE]
> `data/usuarios.csv` guarda únicamente hashes PBKDF2 con sal; las contraseñas de la tabla existen solo como datos de demostración.

---

## Pruebas

El proyecto no usa dependencias externas: la suite es un `main` con aserciones simples.

1. **Compilar main y tests:**
   ```bash
   javac -d out $(find src/main/java src/test/java -name "*.java")
   ```

2. **Ejecutar la suite:**
   ```bash
   java -cp out test.TestRunner
   ```

La suite verifica el hash PBKDF2, la lectura de usuarios, el login por correo/contraseña, la normalización del correo y la resolución de perfiles por rol.
