# Snake Race — ARSW Lab #2 (Java 21, Virtual Threads)

## Integrantes Laboratorio 2

- Samuel Castelblanco (encargado de parte II)
- Ángela Gómez  (encargada de parte I)

---

**Escuela Colombiana de Ingeniería – Arquitecturas de Software**  
Laboratorio de programación concurrente: condiciones de carrera, sincronización y colecciones seguras.

---

## Requisitos

- **JDK 21** (Temurin recomendado)
- **Maven 3.9+**
- SO: Windows, macOS o Linux

---

## Cómo ejecutar

```bash
mvn clean verify
mvn -q -DskipTests exec:java -Dsnakes=4
```

- `-Dsnakes=N` → inicia el juego con **N** serpientes (por defecto 2).
- **Controles**:
  - **Flechas**: serpiente **0** (Jugador 1).
  - **WASD**: serpiente **1** (si existe).
  - **Espacio** o botón **Action**: Pausar / Reanudar.

---

## Reglas del juego (resumen)

- **N serpientes** corren de forma autónoma (cada una en su propio hilo).
- **Ratones**: al comer uno, la serpiente **crece** y aparece un **nuevo obstáculo**.
- **Obstáculos**: si la cabeza entra en un obstáculo hay **rebote**.
- **Teletransportadores** (flechas rojas): entrar por uno te **saca por su par**.
- **Rayos (Turbo)**: al pisarlos, la serpiente obtiene **velocidad aumentada** temporal.
- Movimiento con **wrap-around** (el tablero “se repite” en los bordes).

---

## Arquitectura (carpetas)

```
co.eci.snake
├─ app/                 # Bootstrap de la aplicación (Main)
├─ core/                # Dominio: Board, Snake, Direction, Position
├─ core/engine/         # GameClock (ticks, Pausa/Reanudar)
├─ concurrency/         # SnakeRunner (lógica por serpiente con virtual threads)
└─ ui/legacy/           # UI estilo legado (Swing) con grilla y botón Action
```

---

# Actividades del laboratorio

## Parte I — (Calentamiento) `wait/notify` en un programa multi-hilo

1. Toma el programa [**PrimeFinder**](https://github.com/ARSW-ECI/wait-notify-excercise).
2. Modifícalo para que **cada _t_ milisegundos**:
   - Se **pausen** todos los hilos trabajadores.
   - Se **muestre** cuántos números primos se han encontrado.
   - El programa **espere ENTER** para **reanudar**.
3. La sincronización debe usar **`synchronized`**, **`wait()`**, **`notify()` / `notifyAll()`** sobre el **mismo monitor** (sin _busy-waiting_).
4. Entrega en el reporte de laboratorio **las observaciones y/o comentarios** explicando tu diseño de sincronización (qué lock, qué condición, cómo evitas _lost wakeups_).

> Objetivo didáctico: practicar suspensión/continuación **sin** espera activa y consolidar el modelo de monitores en Java.

---

## Parte II — SnakeRace concurrente (núcleo del laboratorio)

### 1) Análisis de concurrencia

- Explica **cómo** el código usa hilos para dar autonomía a cada serpiente.
- **Identifica** y documenta en **`el reporte de laboratorio`**:
  - Posibles **condiciones de carrera**.
  - **Colecciones** o estructuras **no seguras** en contexto concurrente.
  - Ocurrencias de **espera activa** (busy-wait) o de sincronización innecesaria.

### 2) Correcciones mínimas y regiones críticas

- **Elimina** esperas activas reemplazándolas por **señales** / **estados** o mecanismos de la librería de concurrencia.
- Protege **solo** las **regiones críticas estrictamente necesarias** (evita bloqueos amplios).
- Justifica en **`el reporte de laboratorio`** cada cambio: cuál era el riesgo y cómo lo resuelves.

### 3) Control de ejecución seguro (UI)

- Implementa la **UI** con **Iniciar / Pausar / Reanudar** (ya existe el botón _Action_ y el reloj `GameClock`).
- Al **Pausar**, muestra de forma **consistente** (sin _tearing_):
  - La **serpiente viva más larga**.
  - La **peor serpiente** (la que **primero murió**).
- Considera que la suspensión **no es instantánea**; coordina para que el estado mostrado no quede “a medias”.

### 4) Robustez bajo carga

- Ejecuta con **N alto** (`-Dsnakes=20` o más) y/o aumenta la velocidad.
- El juego **no debe romperse**: sin `ConcurrentModificationException`, sin lecturas inconsistentes, sin _deadlocks_.
- Si habilitas **teleports** y **turbo**, verifica que las reglas no introduzcan carreras.

> Entregables detallados más abajo.

---

## Entregables

1. **Código fuente** funcionando en **Java 21**.
2. Todo de manera clara en **`**el reporte de laboratorio**`** con:
   - Data races encontradas y su solución.
   - Colecciones mal usadas y cómo se protegieron (o sustituyeron).
   - Esperas activas eliminadas y mecanismo utilizado.
   - Regiones críticas definidas y justificación de su **alcance mínimo**.
3. UI con **Iniciar / Pausar / Reanudar** y estadísticas solicitadas al pausar.

---

## Criterios de evaluación (10)

- (3) **Concurrencia correcta**: sin data races; sincronización bien localizada.
- (2) **Pausa/Reanudar**: consistencia visual y de estado.
- (2) **Robustez**: corre **con N alto** y sin excepciones de concurrencia.
- (1.5) **Calidad**: estructura clara, nombres, comentarios; sin _code smells_ obvios.
- (1.5) **Documentación**: **`reporte de laboratorio`** claro, reproducible;

---

## Tips y configuración útil

- **Número de serpientes**: `-Dsnakes=N` al ejecutar.
- **Tamaño del tablero**: cambiar el constructor `new Board(width, height)`.
- **Teleports / Turbo**: editar `Board.java` (métodos de inicialización y reglas en `step(...)`).
- **Velocidad**: ajustar `GameClock` (tick) o el `sleep` del `SnakeRunner` (incluye modo turbo).

---

## Cómo correr pruebas

```bash
mvn clean verify
```

Incluye compilación y ejecución de pruebas JUnit. Si tienes análisis estático, ejecútalo en `verify` o `site` según tu `pom.xml`.

---

## Créditos

Este laboratorio es una adaptación modernizada del ejercicio **SnakeRace** de ARSW. El enunciado de actividades se conserva para mantener los objetivos pedagógicos del curso.

**Base construida por el Ing. Javier Toquica.**


---

# RESPUESTAS A LABORATORIO II

## Parte I — (Calentamiento) `wait/notify` en un programa multi-hilo

> 1. Toma el programa [**PrimeFinder**](https://github.com/ARSW-ECI/wait-notify-excercise).
> 2. Modifícalo para que **cada _t_ milisegundos**:
>  - Se **pausen** todos los hilos trabajadores.
>  - Se **muestre** cuántos números primos se han encontrado.
> - El programa **espere ENTER** para **reanudar**.
> 3. La sincronización debe usar **`synchronized`**, **`wait()`**, **`notify()` / `notifyAll()`** sobre el **mismo monitor** (sin _busy-waiting_).
> 4. Entrega en el reporte de laboratorio **las observaciones y/o comentarios** explicando tu diseño de sincronización (qué lock, qué condición, cómo evitas _lost wakeups_).


> Objetivo didáctico: practicar suspensión/continuación **sin** espera activa y consolidar el modelo de monitores en Java.

**Scaffolding con la realización del primer punto**

```
co.eci.snake
├─ app/                 # Bootstrap de la aplicación (Main)
└─ part1_primefinder/   # Solución al ejercicio wait and notify 
├─ core/                # Dominio: Board, Snake, Direction, Position
├─ core/engine/         # GameClock (ticks, Pausa/Reanudar)
├─ concurrency/         # SnakeRunner (lógica por serpiente con virtual threads)
└─ ui/legacy/           # UI estilo legado (Swing) con grilla y botón Action
```


Para este ejercicio, lo primero que se realizó fue la creación de una clase llamada `PauseController`, la cual fue utilizada para manejar una variable que monitoreara el estado de los hilos. De esta forma podemos identificar de forma puntual si este se encuentra activo o no por medio de una función `checkpoint`. 

Ahora bien, para evitar las condiciones carrera al contar los números primos se creó una clase llamada `PrimeCounter`, el cual utiliza un AtomicInteger para poder manejar esas acciones puntuales sin necesidad de utilizar `synchronized`. 

Aunque estas acciones podían estar implementadas en la clase de `Control` directamente, la idea de realizar una clase aparte fue apoyada por Claude, en donde se identifica el desacoplamiento y una mejor estructura para una posible refactorización en el futuro. 

Después, en el `PrimeFinder` se utilizaron los métodos puntuales de las clases previamente mencionadas, para evitar el llamado de un objeto completo y más bien utilizar las funciones como `checkpoint()` e `increment()` y proteger mejor los demás parámetros que no deben ser modificados. 

En adición, Claude recomendó el uso de la excepción `InterruptedException`, el cual es usado para no perder el registro que se tiene de la variable pause. 

Además, `Control` dejó de extender Thread, ya que en la versión original esto generaba un hilo adicional sin propósito real (solo para lanzar a los otros 3). Ahora es una clase normal, con un método `startAll()` que el Main invoca directamente, reduciendo la cantidad de hilos y simplificando el diseño concurrente. Acá se usa un método llamado `startAll()`, el cual es finalmente usado por el Main, el cual ahora decide cuando pausar, imprimir el conteo, esperar el ENTER y reanudar el conteo. 

**Nota**: Para probar la funcionalidad de oprimir ENTER después de la pausa, se recomienda cambiar el MAXVALUE = 30_000_000 de `Control` a 300_000_000, pues con el primer valor, el programa no se demora más de 5 segundos en procesarlos todos. 

A continuación se muestran algunos códigos que evidencian la lógica explicada junto con el resultado con un MAXVALUE = 300_000_000 para una mejor apreciación de la dinámica usada con la tecla ENTER. 

**Algunas clases**

![Main class](/src/img/part_1/main.png)

![PrimeFinderThread class](/src/img/part_1/prime_finder.png)

![Control class](/src/img/part_1/control.png)

**Ejemplo resultado**

![Resultados](/src/img/part_1/results.png)

## Diseño de sincronización

- **Lock/monitor usado**: la propia instancia de `PauseController`, compartida por referencia entre los 3 `PrimeFinderThread` y el `Main`. Los tres métodos (`pause()`, `resume()`, `checkpoint()`) son `synchronized` sobre ese mismo objeto.

- **Condición de espera**: el booleano `paused`, siempre leído/escrito bajo el mismo lock.

- **Cómo se evitan los *lost wakeups***: se usa `while (paused) wait();` en vez de `if`, para que cada hilo, al despertar, vuelva a verificar la condición real antes de continuar (protege contra *spurious wakeups* y contra el caso donde el estado cambió entre la notificación y la reanudación real del hilo). Además se usa `notifyAll()` en vez de `notify()`, porque hay 3 hilos esperando simultáneamente, pues `notify()` solo despertaría uno al azar, dejando a los otros 2 bloqueados indefinidamente.

---


## Parte II — SnakeRace concurrente (núcleo del laboratorio)

>