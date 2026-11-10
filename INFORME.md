# Sistema de Control y Monitoreo de Asistencia — IESTP Chincha

**Informe Técnico y Documentación de Ingeniería de Software**  
_Curso:_ Paradigmas de Programación I  
_Facultad:_ Facultad de Ingeniería e Inteligencia Artificial  
_Carrera:_ Escuela Profesional de Ingeniería en Ciberseguridad  
_Plataforma:_ Java Platform, Standard Edition 21 (Java SE 21 LTS)

---

## Objetivos

### i. Objetivo General

Desarrollar un software de consola en lenguaje Java SE bajo los fundamentos de Paradigmas de Programación y una arquitectura limpia por capas, aplicando principios de Seguridad por Diseño (Security by Design) para la gestión y consulta de la asistencia estudiantil en el Instituto de Educación Superior Tecnológico Público (IESTP) Chincha; garantizando la integridad, confidencialidad y trazabilidad de los datos académicos mediante autenticación robusta por roles, persistencia desacoplada y la aplicación sistemática de las normativas de inasistencia del Ministerio de Educación (MINEDU).

### ii. Objetivos Específicos

1. Modelar formalmente el dominio académico e institucional: Aplicar abstracción, encapsulamiento estricto, modularidad y tipado fuerte mediante enumeraciones para estructurar las entidades del ecosistema formativo, garantizando la cohesión interna y la consistencia relacional de la información.
2. Implementar un subsistema de autenticación y control de acceso basado en roles (RBAC): Desarrollar un mecanismo de control de acceso que valide identidades mediante credenciales institucionales, segregando estrictamente los privilegios operativos entre los roles de profesor y estudiante bajo el principio de menor privilegio.
3. Incorporar controles criptográficos avanzados para el almacenamiento y verificación de credenciales: Mitigar vulnerabilidades de exposición de contraseñas mediante la implementación del algoritmo PBKDF2 con HMAC-SHA256, empleando sales pseudoaleatorias criptográficamente seguras de 128 bits y un factor de costo computacional de 210,000 iteraciones para neutralizar ataques de fuerza bruta offline y tablas arcoíris.
4. Proteger el canal de entrada de credenciales en tiempo de ejecución: Implementar mecanismos de captura de secretos mediante canales seguros de consola para evitar la persistencia de datos sensibles en memoria no administrada, buffers de salida estándar o historiales de terminal.
5. Automatizar el registro transaccional de asistencia con validación de fronteras: Desarrollar el flujo de captura de asistencia por sesión horaria con fecha determinista, forzando la validación defensiva del rango de horas asistidas en el intervalo cerrado $[0, \text{horasProgramadas}]$ para prevenir anomalías en la bitácora.
6. Programar el algoritmo de alerta temprana institucional: Diseñar la lógica para el cálculo continuo del porcentaje de faltas respecto a la carga horaria programada, emitiendo alertas preventivas automáticas cuando el estudiante supere el umbral del 15% de inasistencia.
7. Aplicar la regla de inhabilitación normativa institucional y del MINEDU: Computar el índice acumulado de inasistencias sobre el total de horas lectivas de la Unidad Didáctica como una precondición booleana que inhabilite de forma estricta el registro de calificaciones cuando el índice supere el 30%, resguardando la validez del proceso evaluativo.
8. Diseñar el módulo de evaluación continua sobre Indicadores de Logro: Habilitar el registro y ponderación de calificaciones en escala vigesimal vinculadas a los indicadores de logro, validando previamente la existencia de matrícula activa y la habilitación por asistencia.
9. Desarrollar una capa de persistencia ligera y desacoplada: Construir un motor de serialización y deserialización hacia archivos planos estructurados, implementando tolerancia a variaciones de esquema para garantizar la resiliencia del sistema ante cambios de estructura sin recurrir a bases de datos externas.
10. Adoptar una arquitectura de dependencias mínimas para reducir la superficie de ataque: Construir la totalidad del aplicativo apoyándose estrictamente en la biblioteca estándar de Java SE 21, eliminando dependencias de terceros para neutralizar riesgos asociados a vulnerabilidades en la cadena de suministro de software.

---

## g) Desarrollo

### i. Identificación de requerimientos

A partir del análisis del proceso manual de asistencia del IESTP Chincha —el cual se realiza mediante listas impresas con firma física y presenta limitaciones de tiempo, seguridad, riesgo de pérdida y falta de centralización—, se establecen los requerimientos funcionales y no funcionales para la implementación del programa en consola, alineado con los fundamentos de Paradigmas de la Programación I.

#### 2. Requerimientos Funcionales (RF)

A continuación se detalla la especificación formal de requerimientos funcionales, priorizando la gestión de entidades y la captura granular de horas asistidas:

| Código    | Descripción del Requisito                                                                                                                                                                                                                                                                                                                                                                                                                                                                                 | Prioridad |
| :-------- | :-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- | :-------: |
| **RF-01** | **Autenticación Segura y Control de Acceso (RBAC):** Validar identidades de usuarios a partir de correo y contraseña contrastados contra `data/usuario.csv`. El sistema debe instanciar el perfil correspondiente (`PROFESOR` o `ESTUDIANTE`) restringiendo el menú interactivo bajo el principio de menor privilegio.                                                                                                                                                                                    |   Alta    |
| **RF-02** | **Gestión del Padrón y Estado de Estudiantes:** Administrar el catálogo de alumnos (`Estudiante`), identificando código institucional, nombres, apellidos, correo institucional y condición académica (`EstadoEstudiante: REGULAR, SUSPENDIDO, RESERVA`). El sistema debe verificar que el alumno posea una matrícula activa para ser incluido en la nómina de asistencia.                                                                                                                                |   Alta    |
| **RF-03** | **Modelado de Cursos y Malla Curricular:** Estructurar las Unidades Didácticas (`Curso`) registrando su identificador, denominación, duración en sesiones, créditos, horas teóricas, horas prácticas y el total de horas lectivas acumuladas (`getHorasLectivasTotales()`), integradas en la jerarquía: Carrera → Plan de Estudios → Módulo Profesional → Unidad Didáctica.                                                                                                                               |   Media   |
| **RF-04** | **Programación y Gestión de Sesiones de Clase:** Modelar las instancias de clase (`SesionClase`) vinculadas a un horario semanal (`HorarioSeccion`), docente asignado, salón físico (`Salon`) y fecha de calendario (`LocalDate`), fijando la cuota horaria de la sesión (`horasProgramadas`).                                                                                                                                                                                                            |   Alta    |
| **RF-05** | **Gestión de Matrículas en Bloques Académicos:** Vincular a cada estudiante con un bloque de sección mediante la entidad `Matricula`, validando el estado `ACTIVA` como precondición obligatoria para la toma de asistencia y asentamiento de evaluaciones en sus cursos correspondientes.                                                                                                                                                                                                                |   Alta    |
| **RF-06** | **Registro Transaccional de Horas Asistidas por Sesión:** Permitir al docente registrar la asistencia estudiante por estudiante para una sesión horaria específica. El docente ingresa la cantidad exacta de horas presenciales efectivas ($horasAsistidas \in \mathbb{Z}$), forzando defensivamente que $0 \leq horasAsistidas \leq horasProgramadas$. Si $horasAsistidas = 0$, se computa inasistencia (`presente = false`); si $horasAsistidas > 0$, se valida presencia efectiva (`presente = true`). |   Alta    |
| **RF-07** | **Cómputo Acumulado de Horas Faltadas por Curso:** Calcular de manera continua las horas no asistidas por el alumno como la sumatoria de $(horasProgramadas_s - horasAsistidas_s)$ sobre el universo de sesiones dictadas. Las sesiones aún no registradas se consideran no impartidas y no acumulan faltas.                                                                                                                                                                                              |   Alta    |
| **RF-08** | **Emisión de Alertas Tempranas Preventivas (15%):** Desplegar advertencias visuales en el resumen de consola del estudiante y en los reportes de curso cuando el porcentaje de inasistencias sobrepase el umbral preventivo institucional del 15.0%.                                                                                                                                                                                                                                                      |   Media   |
| **RF-09** | **Aplicación de la Inhabilitación Normativa MINEDU (30%):** Evaluar si las horas acumuladas de inasistencia superan el 30.0% de las horas lectivas totales de la Unidad Didáctica. En caso afirmativo, el método `puedeRegistrarNotas` debe retornar estrictamente `false`, bloqueando de manera automática el registro de cualquier nota en la sección para dicho alumno.                                                                                                                                |   Alta    |
| **RF-10** | **Asentamiento de Calificaciones sobre Indicadores de Logro:** Habilitar al docente para ingresar calificaciones vigesimales ($0.0 \leq nota \leq 20.0$) asociadas a los `IndicadorLogro` de la sección, validando previamente que el estudiante posea matrícula activa y no se encuentre inhabilitado por inasistencia.                                                                                                                                                                                  |   Alta    |
| **RF-11** | **Consulta Analítica de Asistencia para Estudiantes:** Permitir al estudiante autenticado visualizar un reporte detallado por curso: sesiones programadas, días asistidos, horas efectivas asistidas, horas faltadas y porcentaje de inasistencia con su dictamen normativo correspondiente.                                                                                                                                                                                                              |   Media   |
| **RF-12** | **Sincronización y Persistencia Transaccional CSV:** Cargar e hidratar colecciones en memoria y sincronizar atómicamente los registros de asistencia en `data/registro_de_asistencia.csv` (y calificaciones en `data/registro_nota.csv`), preservando la integridad referencial de los 19 catálogos del sistema.                                                                                                                                                                                          |   Alta    |

---

#### 3. Requerimientos No Funcionales (RNF)

A continuación se establecen las restricciones arquitectónicas, de validación defensiva de datos y de estructuración lógica del aplicativo:

| Código     | Requisito No Funcional                                  |        Categoría         | Métrica / Criterio Técnico de Aceptación                                                                                                                                                                                                                                                                                                                                        |
| :--------- | :------------------------------------------------------ | :----------------------: | :------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------ |
| **RNF-01** | Arquitectura Limpia por Capas (SoC)                     |      Mantenibilidad      | Desacoplamiento estricto en cinco capas independientes: `models` (entidades puras), `security` (criptografía), `storage` (acceso a archivos planos), `service` (reglas de negocio y orquestación) y `sistema` (interfaz CLI). Queda prohibido acoplar lógica de presentación o persistencia directa dentro de las entidades de dominio.                                         |
| **RNF-02** | Validación Defensiva de Entradas Numéricas (Horas)      |         Robustez         | El sistema debe implementar control de fronteras estricto para las horas asistidas: deben ser números enteros acotados al intervalo cerrado $[0, \text{horasProgramadas}]$. En la capa de servicios se forzará la delimitación mediante `Math.max(0, Math.min(horas, horasMaximas))` o lanzando `IllegalArgumentException` ante parámetros que vulneren invariantes de negocio. |
| **RNF-03** | Resiliencia de Entrada por Terminal (Manejo de Errores) |         Robustez         | La captura de selecciones numéricas por consola en `SistemaApp` debe validar el stream de entrada con buffers seguros (`scanner.hasNextInt()`), descartando caracteres alfanuméricos anómalos y repitiendo la solicitud sin arrojar excepciones no controladas (`InputMismatchException`) ni terminar la aplicación.                                                            |
| **RNF-04** | Seguridad Criptográfica de Credenciales                 |        Seguridad         | El almacenamiento y cotejo de contraseñas se implementará mediante PBKDF2 con HMAC-SHA256, empleando sal criptográfica de 16 bytes (128 bits) generada por `SecureRandom` y un factor de costo de 210,000 iteraciones. Queda terminantemente prohibido almacenar contraseñas en texto claro.                                                                                    |
| **RNF-05** | Ocultamiento de Secretos en E/S en Tiempo de Ejecución  |        Seguridad         | En entornos interactivos de terminal se utilizará `System.console().readPassword()` para la captura de contraseñas, previniendo el eco en pantalla y evitando la retención de secretos en memoria no administrada, buffers de salida o historiales de shell.                                                                                                                    |
| **RNF-06** | Arquitectura "Zero Dependencies"                        | Portabilidad / Seguridad | El software debe compilar y ejecutarse apoyándose exclusivamente en la biblioteca estándar de Java Platform, Standard Edition 21 (Java SE 21 LTS), mitigando cualquier vector de vulnerabilidad vinculado a la cadena de suministro de librerías externas.                                                                                                                      |
| **RNF-07** | Estructuración Lógica e Integridad Referencial CSV      |      Confiabilidad       | El motor de almacenamiento `ArchivoManager` debe mantener la integridad referencial lógica entre los 19 archivos planos en `data/`, verificando la existencia previa de identificadores foráneos (`id_estudiante`, `id_curso`, `id_sesion`, `id_matricula`) antes de persistir vínculos en disco con codificación estándar UTF-8.                                               |
| **RNF-08** | Tolerancia a Esquemas Legados (Retrocompatibilidad)     |         Robustez         | Los analizadores sintácticos de persistencia deben manejar variaciones estructurales históricas en los archivos CSV (e.g., registros de cursos con 5 columnas en lugar de 9, o asistencias sin campo `id_sesion`) sin interrumpir la inicialización ni fallar en tiempo de ejecución.                                                                                           |
| **RNF-09** | Eficiencia y Tiempos de Respuesta en Consola            |       Rendimiento        | La inicialización del aplicativo, incluyendo la lectura completa e hidratación en memoria de los 19 archivos CSV, debe completarse en un tiempo $t < 1.0\text{ s}$. La verificación criptográfica de contraseña por inicio de sesión no debe exceder los 100 ms.                                                                                                                |
| **RNF-10** | Verificabilidad y Calidad de Código (Testing)           |      Confiabilidad       | Validación exhaustiva mediante una suite automatizada de pruebas unitarias de caja blanca (`test.TestRunner`), garantizando una tasa de aprobación del 100% (129/129 pruebas pasadas) sin requerir frameworks de pruebas de terceros.                                                                                                                                           |

### ii. Análisis de Hardware

Para el diseño, desarrollo, compilación y pruebas del sistema se utilizó como estación de trabajo principal una **MacBook Pro 13" (2020)** equipada con puertos de conectividad de alta velocidad **Thunderbolt 3 (USB-C)**. Gracias a su capacidad de procesamiento y bus de comunicación síncrona, este entorno permite ejecutar de forma determinista la suite de pruebas unitarias y soportar el cómputo intensivo requerido por las derivaciones criptográficas.

A nivel operativo, el aplicativo fue concebido bajo una arquitectura liviana y portable, permitiendo su despliegue tanto en la estación de desarrollo de referencia como en equipos institucionales con especificaciones estándar:

| Recurso Físico                     | Estación de Desarrollo (MacBook Pro 13", 2020)                                      | Requisito Mínimo del Sistema                             |
| :--------------------------------- | :---------------------------------------------------------------------------------- | :------------------------------------------------------- |
| **Procesador (CPU)**               | Intel Core multi-núcleo / Apple Silicon a 2.0 GHz o superior (x86-64 / ARM64)       | 1 núcleo físico x86-64 o ARM64 a 1.5 GHz                 |
| **Memoria Principal (RAM)**        | 8 GB / 16 GB LPDDR4X unificada                                                      | 512 MB dedicados a la JVM (Heap Memory)                  |
| **Almacenamiento Secundario**      | Unidad de Estado Sólido (SSD NVMe) de alta velocidad conectada por bus interno PCIe | 100 MB libres en disco                                   |
| **Puertos y Conectividad**         | Puertos Thunderbolt 3 (USB-C de hasta 40 Gbps)                                      | Puertos de comunicación estándar                         |
| **Dispositivos de Entrada/Salida** | Teclado físico Magic Keyboard, trackpad Force Touch y pantalla Retina               | Teclado estándar y terminal interactiva con soporte ANSI |

---

### iii. Análisis de Software

#### 1. Stack Tecnológico, Runtime y Herramientas del Ecosistema

El entorno de desarrollo, gestión de dependencias y ejecución del sistema se configuró íntegramente sobre herramientas especializadas bajo el sistema operativo **macOS Tahoe versión 26.7.3**, garantizando un entorno POSIX confiable y determinista:

```text
$ java --version
java 21.0.9 2025-10-21 LTS
Java(TM) SE Runtime Environment Oracle GraalVM 21.0.9+7.1 (build 21.0.9+7-LTS-jvmci-23.1-b79)
Java HotSpot(TM) 64-Bit Server VM Oracle GraalVM 21.0.9+7.1 (build 21.0.9+7-LTS-jvmci-23.1-b79, mixed mode, sharing)
```

A continuación se detalla el conjunto de herramientas y especificaciones técnicas que integran la cadena de ingeniería del software:

| Componente                           | Especificación / Versión                                             |
| :----------------------------------- | :------------------------------------------------------------------- |
| **Sistema Operativo**                | macOS Tahoe versión 26.7.3                                           |
| **Lenguaje de Programación**         | Java Platform, Standard Edition 21 (Java SE 21 LTS)                  |
| **Entorno de Ejecución (JRE / JDK)** | Oracle GraalVM 21.0.9+7.1 (build 21.0.9+7-LTS-jvmci-23.1-b79)        |
| **Gestor de SDKs y Versiones**       | SDKMAN! (_Software Development Kit Manager_)                         |
| **Emulador de Terminal**             | Kitty (0.50.0)                                                       |
| **Entorno de Desarrollo (IDE)**      | Visual Studio Code (VS Code 1.145.2)                                 |
| **Control de Versiones Local**       | Git (2.50.1)                                                         |
| **Repositorio Remoto**               | GitHub                                                               |
| **Paradigmas de Programación**       | Orientado a Objetos (POO), Imperativo-Modular y Seguridad por Diseño |
| **Persistencia de Datos**            | Archivos planos CSV codificados en UTF-8                             |
| **Criptografía de Credenciales**     | PBKDF2WithHmacSHA256 (`javax.crypto`, `SecureRandom`)                |
| **Política de Dependencias**         | _Zero External Dependencies_                                         |
| **Documentación y Modelado**         | Markdown (GFM) y diagramas Mermaid.js                                |

---

### iv. Solución del caso

#### Análisis

La solución operacionaliza los requerimientos identificados mediante la segregación de actores y flujos de negocio trazables:

##### 1. Descomposición de Actores y Casos de Uso del Negocio

- **Actor Docente (`PROFESOR`):**
  - **CU-01 Iniciar Sesión:** Autenticación institucional con contraseña derivada por PBKDF2 y resolución de carga académica.
  - **CU-02 Consultar Carga Lectiva:** Exploración de Unidades Didácticas asignadas, horarios programados y nóminas de alumnos con matrícula activa.
  - **CU-03 Toma de Asistencia por Sesión:** Selección de sesión horaria (`SesionClase`) y captura determinista de horas asistidas ($0 \leq h \leq horasProgramadas$), persistiendo la bitácora en `data/registro_de_asistencia.csv`.
  - **CU-04 Asentamiento de Evaluaciones:** Registro de notas vigesimales ($0.0 \leq nota \leq 20.0$) en Indicadores de Logro, sujeto a la verificación automatizada de inhabilitación por inasistencia normada ($> 30\%$).

- **Actor Estudiante (`ESTUDIANTE`):**
  - **CU-05 Iniciar Sesión:** Acceso seguro a su portal personal.
  - **CU-06 Monitoreo Analítico de Asistencia:** Visualización de balances de horas asistidas vs. dictadas, porcentaje de inasistencias y advertencias tempranas preventivas ($> 15\%$).
  - **CU-07 Consulta de Calificaciones:** Visualización de notas asentadas en sus unidades didácticas y estado de habilitación académica.

##### 2. Matriz de Trazabilidad: Requisitos vs. Módulos del Sistema

| Requisito Funcional                        | Capa de Presentación (`sistema`)      | Capa de Servicio (`service`)               | Capa de Seguridad / Persistencia    | Entidades de Dominio (`models`)                   |
| :----------------------------------------- | :------------------------------------ | :----------------------------------------- | :---------------------------------- | :------------------------------------------------ |
| **RF-01** (Autenticación / RBAC)           | `SistemaApp.iniciarSesion`            | `AutenticacionService.autenticar`          | `PasswordHasher`, `ArchivoManager`  | `Usuario`, `Rol`                                  |
| **RF-02 - RF-05** (Entidades y Sesiones)   | `SistemaApp.gestionarCursosProfesor`  | `ServicioAcademico.listarEstudiantesCurso` | `ArchivoManager.leerEstudiantes`    | `Estudiante`, `Curso`, `SesionClase`, `Matricula` |
| **RF-06 - RF-07** (Registro de Horas)      | `SistemaApp.gestionarCursosProfesor`  | `ServicioAcademico.crearRegistroSesion`    | `ArchivoManager.guardarAsistencias` | `RegistroDeAsistencia`, `SesionClase`             |
| **RF-08 - RF-09** (Alertas y Regla MINEDU) | `SistemaApp.mostrarResumenAsistencia` | `ServicioAcademico.puedeRegistrarNotas`    | Regla lógica de negocio en memoria  | `Curso`, `Matricula`, `RegistroDeAsistencia`      |
| **RF-10 - RF-12** (Notas y Persistencia)   | `SistemaApp.menuDocente`              | `ServicioAcademico.registrarNota`          | `ArchivoManager.guardarNotas`       | `RegistroNota`, `IndicadorLogro`                  |

---

#### Diseño

##### Arquitectura por Capas y Principios de Diseño

El sistema implementa una **Arquitectura en Capas (Layered Architecture)** siguiendo el principio de Separación de Responsabilidades (_Separation of Concerns_ - SoC) y directrices de diseño limpio (_Clean Architecture_):

1. **Capa de Presentación (`sistema`):**  
   Gestiona la interacción textual por consola, control de flujos de navegación, captura de entradas y formateo visual de reportes. No contiene lógica de negocio ni manipulación de archivos.
2. **Capa de Servicios de Aplicación y Lógica de Negocio (`service`):**  
   Encapsula las reglas de negocio institucionales, orquestación de operaciones de asistencia, cálculo de porcentajes normativos, habilitación de evaluaciones y mediación de autenticación.
3. **Capa de Seguridad Criptográfica (`security`):**  
   Encargada de la generación de sal pseudoaleatoria y derivación de hashes criptográficos mediante PBKDF2-HMAC-SHA256, aislada de la lógica del dominio.
4. **Capa de Persistencia e Integración (`storage`):**  
   Responsable del acceso al almacenamiento secundario (I/O), serialización y parseo de entidades delimitadas por comas con manejo de compatibilidad retrospectiva.
5. **Capa de Dominio y Modelos (`models`):**  
   Entidades y enumeraciones del dominio académico con atributos fuertemente tipados, constructores defensivos y métodos de dominio atómicos.

```
┌────────────────────────────────────────────────────────────────────────┐
│                        Capa de Presentación (CLI)                      │
│                          sistema.SistemaApp                            │
└───────────────────────────────────┬────────────────────────────────────┘
                                    │ delega operaciones
                                    ▼
┌────────────────────────────────────────────────────────────────────────┐
│                        Capa de Servicios y Negocio                     │
│               service.ServicioAcademico │ service.AutenticacionService │
└───────────────┬───────────────────────────────────────┬────────────────┘
                │ persiste / consulta                   │ verifica credenciales
                ▼                                       ▼
┌──────────────────────────────────────┐ ┌───────────────────────────────┐
│       Capa de Persistencia (I/O)     │ │       Capa de Seguridad       │
│         storage.ArchivoManager       │ │     security.PasswordHasher   │
└───────────────┬──────────────────────┘ └───────────────────────────────┘
                │ hidrata colecciones
                ▼
┌────────────────────────────────────────────────────────────────────────┐
│                        Capa de Dominio (Modelos)                       │
│    Carrera, PlanEstudios, ModuloProfesional, Curso, SesionClase,       │
│   RegistroDeAsistencia, Matricula, IndicadorLogro, RegistroNota, ...   │
└────────────────────────────────────────────────────────────────────────┘
```

##### Diagrama de Clases UML del Sistema

```mermaid
classDiagram
    direction TB

    namespace sistema {
        class SistemaApp {
            +main(args: String[])
            -ejecutarMenuDocente(profesor: Profesor)
            -ejecutarMenuEstudiante(estudiante: Estudiante)
            -tomarAsistencia(profesor: Profesor)
            -mostrarReporteEstudiante(estudiante: Estudiante)
            -leerContrasenaOculta() String
        }
    }

    namespace service {
        class AutenticacionService {
            -List~Usuario~ usuarios
            +iniciarSesion(correo: String, contrasena: String) Usuario
            +buscarUsuarioPorCorreo(correo: String) Usuario
        }
        class ServicioAcademico {
            +LIMITE_INASISTENCIA: double = 15.0
            +LIMITE_INASISTENCIA_NORMATIVO: double = 30.0
            +calcularHorasFaltadas(idEstudiante: String, idCurso: String) int
            +calcularPorcentajeInasistencia(idEstudiante: String, idCurso: String) double
            +puedeRegistrarNotas(idEstudiante: String, idCurso: String) boolean
            +puedeRegistrarNota(idEstudiante: String, idSeccion: String) boolean
            +registrarAsistencia(asistencia: RegistroDeAsistencia) void
            +registrarNota(nota: RegistroNota) void
            +obtenerSesionesPorHorario(idHorario: String) List~SesionClase~
        }
    }

    namespace security {
        class PasswordHasher {
            -ITERACIONES: int = 210000
            -LONGITUD_SAL: int = 16
            -LONGITUD_HASH: int = 256
            +hash(contrasena: String) String
            +verificar(contrasena: String, hashEsperado: String) boolean
            -toHex(bytes: byte[]) String
            -fromHex(hex: String) byte[]
        }
    }

    namespace storage {
        class ArchivoManager {
            +cargarCursos() List~Curso~
            +cargarEstudiantes() List~Estudiante~
            +cargarProfesores() List~Profesor~
            +cargarAsistencias() List~RegistroDeAsistencia~
            +guardarAsistencias(asistencias: List~RegistroDeAsistencia~) void
            +cargarNotas() List~RegistroNota~
            +guardarNotas(notas: List~RegistroNota~) void
            +cargarUsuarios() List~Usuario~
            +guardarUsuarios(usuarios: List~Usuario~) void
        }
    }

    namespace models {
        class Carrera {
            -String idCarrera
            -String nombre
            -String tipo
            +getIdCarrera() String
            +getNombre() String
            +getTipo() String
        }
        class PlanEstudios {
            -String idPlan
            -String idCarrera
            -String codigo
            -EstadoPlan estado
        }
        class ModuloProfesional {
            -String idModulo
            -String idPlan
            -String nombre
        }
        class Curso {
            -String idCurso
            -String nombreCurso
            -int totalHorasSesion
            -int duracionCurso
            -int creditos
            -int horasTeoricas
            -int horasPracticas
            +getHorasLectivasTotales() int
        }
        class SeccionCurso {
            -String idSeccion
            -String idCurso
            -ModalidadDictado modalidad
        }
        class SesionClase {
            -String idSesion
            -LocalDate fecha
            -int horasProgramadas
            +getIdSesion() String
            +getHorasProgramadas() int
        }
        class RegistroDeAsistencia {
            -String idAsistencia
            -String idCurso
            -String idEstudiante
            -LocalDate fecha
            -int horasAsistidas
            -boolean presente
            -String idSesion
            -String idMatricula
            +getHorasAsistidas() int
            +isPresente() boolean
            +getIdMatricula() String
        }
        class IndicadorLogro {
            -String idIndicador
            -String idSeccion
            -String nombre
            -double peso
        }
        class RegistroNota {
            -String idNota
            -String idIndicador
            -String idEstudiante
            -double nota
            -LocalDate fecha
            +getNota() double
        }
        class Usuario {
            -String id
            -String correo
            -String hashContrasena
            -Rol rol
            -String idReferencia
            -boolean activo
            +getRol() Rol
            +isActivo() boolean
        }
        class Rol {
            <<enumeration>>
            PROFESOR
            ESTUDIANTE
        }
    }

    SistemaApp --> ServicioAcademico : orquesta operaciones
    SistemaApp --> AutenticacionService : delega inicio de sesión
    AutenticacionService ..> PasswordHasher : verifica derivación criptográfica
    AutenticacionService ..> ArchivoManager : carga catálogo de usuarios
    ServicioAcademico ..> ArchivoManager : persiste y consulta registros
    ServicioAcademico ..> Curso : valida carga lectiva
    ServicioAcademico ..> RegistroDeAsistencia : procesa inasistencias
    ServicioAcademico ..> RegistroNota : valida y asienta evaluación
    Usuario --> Rol : tipado de permisos
    Curso --> ModuloProfesional : pertenece a
    ModuloProfesional --> PlanEstudios : compone
    PlanEstudios --> Carrera : pertenece a
    SeccionCurso --> Curso : apertura de
    SesionClase ..> RegistroDeAsistencia : referencia horaria
    RegistroNota --> IndicadorLogro : evalúa
```

> [!NOTE]
> La especificación detallada de relaciones, asociaciones y navegabilidad a nivel de entidad se encuentra formalizada en los archivos Mermaid de la carpeta [`docs/`](docs/) y el índice maestro [`docs/Index.mmd`](docs/Index.mmd).

---

#### Implementación

##### Estructura de Paquetes del Repositorio

El código fuente del sistema está estructurado bajo [`src/main/java/`](src/main/java/):

```
src/main/java/
├── models/         # Entidades del dominio, enumeraciones y relaciones jerárquicas
│   ├── BloqueSeccion.java, Carrera.java, ComprobantePago.java, Curso.java, ...
│   └── Rol.java, EstadoEstudiante.java, ModalidadDictado.java, Turno.java, ...
├── security/       # Derivación y validación de credenciales criptográficas
│   └── PasswordHasher.java
├── storage/        # Capa de serialización, deserialización y resiliencia CSV
│   └── ArchivoManager.java
├── service/        # Lógica de negocio, reglas normativas y autenticación
│   ├── AutenticacionService.java
│   └── ServicioAcademico.java
└── sistema/        # Orquestación de menús, captura de entradas y presentación CLI
    └── SistemaApp.java
```

##### Mecanismos de Seguridad y Derivación Criptográfica (PBKDF2)

La clase [`security.PasswordHasher`](src/main/java/security/PasswordHasher.java) implementa el algoritmo PBKDF2 (_Password-Based Key Derivation Function 2_) empleando la primitiva pseudoaleatoria HMAC-SHA256:

$$\text{DK} = \text{PBKDF2}(\text{PRF}=\text{HMAC-SHA256}, \text{Password}, \text{Salt}, c=210\,000, dkLen=256)$$

1. **Generación de Sal Criptográfica:**  
   Se utiliza `java.security.SecureRandom` para generar un vector aleatorio de 16 bytes (128 bits) por cada contraseña, garantizando unicidad y neutralizando ataques basados en tablas precalculadas (_Rainbow Tables_).
2. **Derivación de Clave:**  
   Se invoca `SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")` aplicando 210,000 iteraciones para obtener una clave derivada de 256 bits (32 bytes).
3. **Formato de Almacenamiento Canónico:**  
   El hash final se almacena en `data/usuario.csv` concatenando la sal y la clave derivada en formato hexadecimal:
   $$\text{Formato Persistido} = \text{hex}(\text{Salt}) \mathbin{\Vert} \text{":"} \mathbin{\Vert} \text{hex}(\text{DerivedKey})$$
4. **Verificación en Tiempo Constante:**  
   La validación contrasta el hash recalculado contra el hash esperado almacenado sin revelar diferencias temporales que permitan ataques de canal lateral (_Timing Attacks_).

##### Lógica de Negocio y Reglas de Control de Asistencia

1. **Inasistencia Preventiva de Curso (15%):**  
   Cálculo sobre las horas dictadas programadas para advertir riesgo inminente:
   $$\% \text{ Faltas} = \left( \frac{\text{Horas Dictadas} - \text{Horas Asistidas}}{\text{Duración en Días} \times \text{Horas por Sesión}} \right) \times 100$$
   Si $\% \text{ Faltas} > 15.0\%$, se activa la alerta visual en consola.

2. **Regla Normativa Ministerial de Inasistencia (MINEDU 30%):**  
   Computado rigurosamente sobre el total de horas lectivas de la Unidad Didáctica (`getHorasLectivasTotales()`):
   $$\text{Horas Faltadas Acumuladas} = \sum_{s \in \text{SesionesTomadas}} (\text{horasProgramadas}_s - \text{horasAsistidas}_s)$$
   $$\% \text{ Inasistencia Normativa} = \left( \frac{\text{Horas Faltadas Acumuladas}}{\text{Horas Lectivas Totales de la Unidad Didáctica}} \right) \times 100$$
   > [!IMPORTANT]
   > Si el $\% \text{ Inasistencia Normativa} > 30.0\%$, el método `puedeRegistrarNotas` retorna estrictamente `false`. El sistema impide asentar cualquier calificación en los Indicadores de Logro de dicha sección.

##### Catálogo de Persistencia en Archivos CSV

La persistencia del sistema está distribuida en 19 archivos dentro de la carpeta [`data/`](data/):

| Archivo CSV                       | Estructura de Campos                                                                                               | Función en la Arquitectura                                               |
| :-------------------------------- | :----------------------------------------------------------------------------------------------------------------- | :----------------------------------------------------------------------- |
| `data/usuario.csv`                | `id, correo, hash_contrasena, rol, id_referencia, activo`                                                          | Almacén de credenciales con salt/hash PBKDF2 y roles RBAC.               |
| `data/profesor.csv`               | `id, nombre, apellido, correo, tipo_doc, nro_doc, telefono, depto, especialidad`                                   | Catálogo de docentes y afiliación académica.                             |
| `data/estudiante.csv`             | `id_estudiante, codigo, nombres, apellidos, dni, correo, escuela_profesional, estado`                              | Registro estudiantil, DNI y condición académica formal.                  |
| `data/carrera.csv`                | `id_carrera, nombre, tipo`                                                                                         | Programas formativos superiores autorizados (tipo `Educación Superior`). |
| `data/plan_estudios.csv`          | `id_plan, id_carrera, codigo, nombre, creditos_totales, horas_totales, estado`                                     | Mallas curriculares vigentes e históricas.                               |
| `data/modulo_profesional.csv`     | `id_modulo, id_plan, nombre, competencia, creditos_modulo`                                                         | Módulos profesionales de certificación progresiva.                       |
| `data/periodo_academico.csv`      | `id_periodo, id_plan, codigo, fecha_inicio, fecha_fin, estado`                                                     | Ciclos y periodos lectivos.                                              |
| `data/bloque_seccion.csv`         | `id_bloque, id_periodo, nombre, turno`                                                                             | Agrupación de secciones por turno (`MANANA`, `TARDE`, `NOCHE`).          |
| `data/curso.csv`                  | `id_curso, nombre_curso, id_modulo, creditos, horas_teoricas, horas_practicas, total_horas_sesion, duracion_curso` | Unidades didácticas con desagregación de horas teóricas y prácticas.     |
| `data/seccion_curso.csv`          | `id_seccion, id_bloque, id_curso, modalidad, cupo_maximo`                                                          | Secciones ofertadas por periodo formativo.                               |
| `data/horario_seccion.csv`        | `id_horario, id_seccion, dia_semana, hora_inicio, hora_fin, tipo_clase, id_prof, id_salon`                         | Programación horaria semanal de cada sección.                            |
| `data/sesion_clase.csv`           | `id_sesion, id_horario, fecha, horas_programadas, id_profesor, id_salon`                                           | Instancias individuales de sesión con fecha de calendario.               |
| `data/salon.csv`                  | `id_salon, nombre, tipo, capacidad`                                                                                | Espacios físicos (`AULA`, `LABORATORIO`, `TALLER`).                      |
| `data/matricula.csv`              | `id_matricula, id_estudiante, id_bloque, fecha_matricula, estado`                                                  | Vinculación formal de alumnos con bloques de estudio.                    |
| `data/registro_de_asistencia.csv` | `id_asistencia, id_curso, id_estudiante, fecha, horas_asistidas, presente, id_sesion, id_matricula`                | Registro histórico de asistencia por sesión, matrícula y alumno.         |
| `data/indicador_logro.csv`        | `id_indicador, id_seccion, nombre, peso`                                                                           | Indicadores de logro evaluativos de la sección.                          |
| `data/registro_nota.csv`          | `id_nota, id_indicador, id_estudiante, nota, fecha`                                                                | Calificaciones vigesimales ($0.0 \leq \text{nota} \leq 20.0$).           |
| `data/derecho_pago.csv`           | `id_derecho, codigo, nombre, monto`                                                                                | Conceptos de cobro normados institucionalmente.                          |
| `data/comprobante_pago.csv`       | `id_comprobante, id_estudiante, id_derecho, fecha, monto, estado`                                                  | Trazabilidad financiera, comprobantes emitidos y su estado.              |

##### Compilación y Despliegue Operativo

Para compilar y ejecutar la aplicación de consola mediante herramientas nativas del JDK:

```bash
# 1. Compilación de clases del aplicativo en el directorio 'out'
javac -d out $(find src/main/java -name "*.java")

# 2. Ejecución de la aplicación principal
java -cp out sistema.SistemaApp
```

##### Credenciales de Prueba Sembradas en Almacén

> [!WARNING]
> Las contraseñas expuestas a continuación corresponden a usuarios semilla provistos con fines de demostración académica en entorno de pruebas. En un entorno de producción, deben ser cambiadas de inmediato.

| Rol Asignado | Correo Institucional                 | Contraseña de Prueba | Identificador Asociado |
| :----------- | :----------------------------------- | :------------------- | :--------------------- |
| `PROFESOR`   | `carlos.mendoza@iestpchincha.edu.pe` | `Docente2026*`       | `DOC-001`              |
| `PROFESOR`   | `ana.torres@iestpchincha.edu.pe`     | `Docente2026*`       | `DOC-003`              |
| `PROFESOR`   | `luis.quispe@iestpchincha.edu.pe`    | `Docente2026*`       | `DOC-004`              |
| `PROFESOR`   | `maria.huaman@iestpchincha.edu.pe`   | `Docente2026*`       | `DOC-005`              |
| `PROFESOR`   | `jorge.ramos@iestpchincha.edu.pe`    | `Docente2026*`       | `DOC-006`              |
| `ESTUDIANTE` | `erick.escriba@iestpchincha.edu.pe`  | `Alumno2026*`        | `ALU-2024101`          |
| `ESTUDIANTE` | `sofia.gomez@iestpchincha.edu.pe`    | `Alumno2026*`        | `ALU-2024102`          |
| `ESTUDIANTE` | `mateo.silva@iestpchincha.edu.pe`    | `Alumno2026*`        | `ALU-2024103`          |
| `ESTUDIANTE` | `lucia.cardenas@iestpchincha.edu.pe` | `Alumno2026*`        | `ALU-2024104`          |
| `ESTUDIANTE` | `diego.morales@iestpchincha.edu.pe`  | `Alumno2026*`        | `ALU-2024105`          |

---

#### Pruebas

##### Estrategia de Pruebas y Aseguramiento de Calidad

La estrategia de validación se fundamenta en pruebas unitarias de caja blanca automatizadas, orquestadas mediante la clase [`test.TestRunner`](src/test/java/test/TestRunner.java). Dicha suite no requiere frameworks externos (e.g., JUnit) y valida el 100% de los invariantes de seguridad y reglas de negocio del sistema.

##### Ejecución de la Suite de Pruebas

```bash
# 1. Compilación conjunta de código fuente y suite de pruebas
javac -d out $(find src/main/java src/test/java -name "*.java")

# 2. Ejecución de la suite determinista
java -cp out test.TestRunner
```

**Salida de Ejecución Observada:**

```text
OK: 129 pruebas pasaron.
```

##### Matriz de Casos de Prueba Críticos

| ID Caso   | Módulo Evaluado        | Vector de Entrada / Condición                                                           | Comportamiento Esperado y Criterio de Aceptación                                                                      |   Dictamen   |
| :-------- | :--------------------- | :-------------------------------------------------------------------------------------- | :-------------------------------------------------------------------------------------------------------------------- | :----------: |
| **CP-01** | `PasswordHasher`       | Generación sucesiva de hash para la cadena `"secreto"`                                  | Generación de sales aleatorias distintas con hashes resultantes no idénticos; verificación exitosa de ambos (`true`). | **APROBADO** |
| **CP-02** | `PasswordHasher`       | Verificación de contraseña incorrecta contra hash válido                                | El método `verificar` retorna estrictamente `false`.                                                                  | **APROBADO** |
| **CP-03** | `AutenticacionService` | Correo con espacios periféricos y mayúsculas (`" CARLOS.MENDOZA@USIL.PE "`)             | Normalización canónica (trim + lowercase); autenticación exitosa resolviendo `DOC-001`.                               | **APROBADO** |
| **CP-04** | `AutenticacionService` | Intento de acceso con usuario deshabilitado (`activo = false`)                          | Rechazo de autenticación retornando `null` independientemente de la contraseña provista.                              | **APROBADO** |
| **CP-05** | `ServicioAcademico`    | Registro de horas asistidas válidas: $0 \leq h \leq \text{horasProgramadas}$            | Persistencia satisfactoria de asistencia en memoria y bitácora CSV.                                                   | **APROBADO** |
| **CP-06** | `ServicioAcademico`    | Registro de horas anómalas: $h < 0$ o $h > \text{horasProgramadas}$                     | Captura defensiva mediante `IllegalArgumentException`; estado de persistencia inalterado.                             | **APROBADO** |
| **CP-07** | `ServicioAcademico`    | Inasistencia acumulada $> 15.0\%$ del curso                                             | Activación de bandera de alerta temprana preventiva en el balance del alumno.                                         | **APROBADO** |
| **CP-08** | `ServicioAcademico`    | Inasistencia acumulada $> 30.0\%$ de horas lectivas de la unidad didáctica              | Invocación de `puedeRegistrarNotas` devuelve `false`, bloqueando el asentamiento de notas.                            | **APROBADO** |
| **CP-09** | `ServicioAcademico`    | Registro de nota en rango válido $[0.0, 20.0]$ con alumno habilitado                    | Asentamiento exitoso de la nota vigesimal en `data/registro_nota.csv`.                                                | **APROBADO** |
| **CP-10** | `ServicioAcademico`    | Intento de registro de nota sobre alumno inhabilitado por inasistencia ($>30\%$)        | Operación rechazada por regla de negocio; no se modifica `data/registro_nota.csv`.                                    | **APROBADO** |
| **CP-11** | `ArchivoManager`       | Lectura de registro de `Curso` legado (5 columnas en lugar de 9)                        | Derivación dinámica de horas lectivas (`duracion × totalHorasSesion`) sin error de parsing.                           | **APROBADO** |
| **CP-12** | `ArchivoManager`       | Lectura de `RegistroDeAsistencia` legado (6 columnas sin `id_sesion` ni `id_matricula`) | Carga resiliente asignando sesión y matrícula nulas sin interrupción de la inicialización del sistema.                | **APROBADO** |
