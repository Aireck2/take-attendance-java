# Sistema de Control y Monitoreo de Asistencia — IESTP Chincha

Aplicación de consola desarrollada en **Java 21 SE** para la gestión, control y monitoreo de asistencias académicas en el Instituto de Educación Superior Tecnológico Público Chincha.

El sistema implementa una arquitectura limpia por capas (Separación de Responsabilidades) sin dependencias externas, persistiendo la información en archivos CSV y proporcionando seguimiento analítico de inasistencias en tiempo real.

---

## Funcionalidades

### Gestión de Profesores
- **Consulta de Perfil:** Visualización detallada de datos personales, departamento académico y especialidad.
- **Cursos Asignados:** Listado de asignaturas a cargo con detalle de carga horaria y lista de alumnos matriculados.
- **Toma de Asistencia Interactiva:** Registro por sesión con captura automática de fecha actual (`LocalDate`), validación de estado (asistió / faltó) y registro de horas efectivas asistidas con tope por sesión.

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
├── storage/        # Capa de persistencia y serialización en archivos planos CSV
├── service/        # Lógica de negocio, cálculos de inasistencias y validaciones
└── sistema/        # Orquestación de menús, interfaz de consola (CLI) e interacción
```

---

## Diagramas de Clases UML y Relaciones

El modelado técnico orientado a objetos y la arquitectura por capas se encuentran documentados en formato **Mermaid** dentro de la carpeta `docs/`.

### Diagrama General Integrado

El archivo [`docs/Index.mmd`](docs/Index.mmd) centraliza la arquitectura del sistema y documenta las siguientes relaciones UML:

- **Relaciones de Dominio (`models`):**
  - **Asociación directa:** `Estudiante --> EstadoEstudiante` (asociación tipada con la enumeración de estados).
  - **Referencias por clave lógica:** `Curso ..> Profesor` (asociación desacoplada vía `idProfesor`).
  - **Trazabilidad de asistencia:** `RegistroDeAsistencia ..> Estudiante` (vía `idEstudiante`) y `RegistroDeAsistencia ..> Curso` (vía `idCurso`).

- **Relaciones entre Capas (Arquitectura):**
  - **Presentación a Servicio:** `SistemaApp --> ServicioAcademico` (composición/delegación de responsabilidades de la UI hacia el servicio).
  - **Servicio a Persistencia:** `ServicioAcademico ..> ArchivoManager` (dependencia para carga y guardado de datos).

- **Dependencias de Uso y Persistencia:**
  - `ServicioAcademico ..>` Entidades del dominio: Orquesta y realiza cálculos sobre `Profesor`, `Estudiante`, `Curso` y `RegistroDeAsistencia`.
  - `ArchivoManager ..>` Entidades del dominio: Mapea registros entre memoria y archivos CSV (`parsea enum`, `serializa/deserializa CSV`).

### Catálogo de Diagramas

| Directorio | Archivo | Descripción |
| :--- | :--- | :--- |
| `docs/` | [`Index.mmd`](docs/Index.mmd) | **Diagrama UML global** con todos los paquetes, clases y relaciones entre capas. |
| `docs/models/` | [`Models.mmd`](docs/models/Models.mmd) | Diagrama de paquete con entidades del dominio, enumeraciones y relaciones internas. |
| `docs/models/` | `*.mmd` | Diagramas individuales: [`CursoClass.mmd`](docs/models/CursoClass.mmd), [`Estudiante.mmd`](docs/models/Estudiante.mmd), [`Profesor.mmd`](docs/models/Profesor.mmd), [`RegistroDeAsistencia.mmd`](docs/models/RegistroDeAsistencia.mmd), [`EstadoEstudiante.mmd`](docs/models/EstadoEstudiante.mmd). |
| `docs/service/` | [`Service.mmd`](docs/service/Service.mmd) | Vista de namespace del paquete `service`. |
| `docs/service/` | [`ServicioAcademico.mmd`](docs/service/ServicioAcademico.mmd) | Especificación de atributos, constantes y métodos de la lógica de negocio. |
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
