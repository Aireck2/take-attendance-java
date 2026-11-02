# Sistema de Asistencia Universitaria — IESTP Chincha

Aplicación de terminal para registrar y consultar la asistencia de clases en el Instituto de Educación Superior Tecnológico Público Chincha (IESTP Chincha).

## Funcionalidades

- **Profesor**
  - Ver perfil.
  - Ver cursos asignados y detalle con alumnos inscritos.
  - Tomar asistencia: fecha automática (`YYYY-MM-DD`), horas por alumno y ausencias.

- **Estudiante**
  - Ver perfil.
  - Ver resumen de asistencia por curso: días, horas, porcentaje de inasistencia y alerta.

## Cálculo de la toma de asistencia

- El profesor registra la asistencia curso por curso: para cada alumno pregunta si asistió (1/0) y, si asistió, las horas (máx. horas de la sesión).
- Se guardan los registros en `data/asistencias.csv`.
- El porcentaje de inasistencia se calcula con el total del curso: `(horas dictadas − horas asistidas) / horas totales del curso × 100`.

## Tecnología

- Java 21 (SE)
- Sin librerías externas
- Arquitectura por capas (SoC): `models`, `storage`, `service`, `sistema`

## Ejecución

```bash
javac -d out $(find src/main/java -name "*.java")
java -cp out sistema.SistemaApp
```

## Datos

Los archivos CSV se agrupan en la carpeta `data/`.

| Archivo | Contenido |
| --- | --- |
| `data/profesores.csv` | Profesores: id, nombre, correo, especialidad. |
| `data/estudiantes.csv` | Estudiantes: id, código, nombre, correo, carrera, estado. |
| `data/cursos.csv` | Cursos: id, nombre, horas por sesión, profesor, duración. |
| `data/inscripciones.csv` | Inscripciones: curso, estudiante. |
| `data/asistencias.csv` | Asistencias: id, curso, estudiante, fecha, horas, estado. |

## Estructura

El proyecto sigue separación de responsabilidades:

- `src/main/java/models` — Entidades de dominio.
- `src/main/java/storage` — Lectura/escritura de archivos CSV.
- `src/main/java/service` — Lógica de negocio (porcentajes, resúmenes).
- `src/main/java/sistema` — Interfaz de terminal y menús.
