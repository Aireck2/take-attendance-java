# Autenticación por correo y contraseña con roles

Objetivo: incorporar usuarios y roles (profesor/estudiante) al dominio, y un inicio de sesión real con correo y contraseña.
Rama: `feature/autenticacion-roles` (desde `develop`).
Estrategia de entrega: `single-pr` (una sola rama; no se solicitó PR).
Runner: `javac` + `java` (proyecto sin dependencias externas).

## Specs

- **S1:** El dominio incorpora `Rol` (`PROFESOR`, `ESTUDIANTE`) y `Usuario` (`id`, `correo`, `hashContrasena`, `rol`, `idReferencia`, `activo`). `idReferencia` apunta al `id` de `Profesor` o `Estudiante`.
- **S2:** Las contraseñas nunca se guardan en texto plano. Se usa PBKDF2-HMAC-SHA256 con sal aleatoria de 16 bytes y 210000 iteraciones, con formato `pbkdf2_sha256$<iteraciones>$<salBase64>$<hashBase64>` y comparación en tiempo constante.
- **S3:** El programa inicia con el menú `1. Iniciar sesión / 2. Salir`. El inicio de sesión pide correo y contraseña; la contraseña se oculta con `System.console().readPassword()` cuando la terminal lo permite.
- **S4:** Credenciales correctas abren directamente el menú del profesor o del estudiante según el rol. Credenciales incorrectas muestran un mensaje genérico y permiten hasta 3 intentos.
- **S5:** El correo se normaliza (recorta espacios y compara sin distinguir mayúsculas).
- **S6:** `data/usuarios.csv` persiste `id,correo,hash_contrasena,rol,id_referencia,activo` con 10 usuarios de demostración (5 docentes y 5 estudiantes) y contraseñas documentadas en el README.
- **S7:** README y diagramas documentan el modelo, la persistencia y el flujo de acceso.

## Tasks

- T1 | S1, S2 | inline | **hecho** | Tests de `PasswordHasher` en rojo; luego `Rol`, `Usuario` y `PasswordHasher`. Commit `f7a7d39`.
- T2 | S1, S5, S6 | inline | **hecho** | `ArchivoManager.leerUsuarios`, semilla `data/usuarios.csv`, `AutenticacionService` y tests de login. Commit `f7a7d39`.
- T3 | S3, S4 | inline | **hecho** | Flujo de inicio de sesión en `SistemaApp` y resolución de perfiles en `ServicioAcademico`. Commit `c0b0b2c`.
- T4 | S7 | inline | **hecho** | README, diagramas Mermaid y verificación funcional. Commit de documentación.

## Log

- **L1 (2026-11-09):** "quiero que agregues roles,usuarios, etc, a modelos (profesor, estudiante), quiero que agregues al programa un flujo de inicio de session real (correo y contrasena)"
- **L2 (2026-11-09):** Al iniciar, `src/main/java/sistema/SistemaApp.java` ya tenía un cambio sin commitear (limpieza de imports y eliminación de `ArchivoManager` sin uso). Se conserva y se integra.
- **L3 (2026-11-09):** Decisión técnica: `Usuario` es una entidad separada de `Profesor`/`Estudiante`, vinculada por `idReferencia`. Evita mezclar credenciales con datos académicos y permite que el rol decida el menú.
- **L4 (2026-11-09):** Decisión técnica: PBKDF2 con JDK (`SecretKeyFactory`) en lugar de texto plano o hash simple. No agrega dependencias externas.
- **L5 (2026-11-09):** RED observado: la suite no compila porque faltan `Rol`, `Usuario`, `PasswordHasher` y `AutenticacionService`. GREEN: `java -cp out test.TestRunner` → `OK: 17 pruebas pasaron.`
- **L6 (2026-11-09):** Verificación funcional con entrada redirigida: login de profesor (`carlos.mendoza@usil.pe`) y de estudiante (`erick.escriba@iestp.pe`), apertura del menú correcto, 3 intentos fallidos con mensaje genérico. Toma de asistencia probada en una copia temporal de `data/`: se agregaron 5 registros (A6–A10) sin tocar el CSV del repositorio.
- **L7 (2026-11-09):** Commits de la feature: `f7a7d39` (modelo, seguridad, persistencia y tests) y `c0b0b2c` (flujo de login por rol).
