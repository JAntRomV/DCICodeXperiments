# JMH Multi-Class Benchmark + Perfilado línea por línea

Herramienta en Java para:
1. Ejecutar JMH sobre varias clases de benchmark en una sola corrida.
2. Guardar los resultados oficiales de JMH en un CSV.
3. Correr 100 iteraciones de medición por clase.
4. Dentro de cada clase, perfilar el código línea por línea con tags,
   guardar cada marca en un CSV alterno y generar un resumen con
   promedios y percentiles (p50/p90/p95/p99).

## Estructura

```
src/main/java/com/antonio/benchmark/
├── profiler/
│   ├── LineProfiler.java        # API que usas DENTRO del método @Benchmark: profiler.mark("tag")
│   ├── LineRecord.java          # fila inmutable del CSV de líneas
│   ├── LineCsvWriter.java       # escritor asíncrono en background (no bloquea la medición)
│   ├── NanoClock.java           # estimación de hora de pared con resolución de ns
│   └── SummaryGenerator.java    # calcula avg/min/max/percentiles agrupado por (clase, tag)
├── runner/
│   └── MultiClassBenchmarkRunner.java  # main(): dispara JMH sobre la lista de clases
└── examples/
    ├── ExampleBenchmarkA.java   # ejemplo simple con LineProfiler
    └── ExampleBenchmarkB.java   # ejemplo con instanciación por reflexión (Class.forName)
```

## Compilar y ejecutar

```bash
mvn clean package
java -jar target/jmh-multi-benchmark.jar
```

Esto genera:

| Archivo | Contenido |
|---|---|
| `results/jmh_results.csv` | Resultados oficiales de JMH (throughput, avgt, sampletime, etc., según `Mode.All`) |
| `results/line_profile.csv` | Una fila por cada `mark(tag)` ejecutado: clase, iteración, tag, hora de pared (ns), duración desde la marca anterior (ns), hilo |
| `results/line_profile_summary.csv` | Por cada combinación (clase, tag): count, avg_ns, min_ns, max_ns, p50_ns, p90_ns, p95_ns, p99_ns |

## Cómo agregar tus propias clases de benchmark

1. Crea una clase en `examples/` (o en el paquete que prefieras) con métodos `@Benchmark`.
2. Dentro del método, instancia un `LineProfiler` (normalmente en `@Setup(Level.Invocation)`)
   y llama a `profiler.mark("tag")` entre los bloques de código que quieras medir.
3. En `@TearDown(Level.Invocation)` llama a `profiler.flush()`.
4. Agrega el nombre completamente calificado de tu clase a `TARGET_CLASSES` en
   `MultiClassBenchmarkRunner`.

```java
profiler.mark("lectura_csv");
List<String> filas = leerCsv(path);
profiler.mark("parseo");
List<Registro> registros = parsear(filas);
profiler.mark("escritura_resultado");
```

## Decisiones de diseño (y por qué importan para no contaminar la medición)

- **`mark()` es solo memoria.** Cada llamada agrega dos `long` a una lista en memoria.
  No hay I/O de disco dentro del método `@Benchmark`, así que el overhead que introduce
  en el código medido es mínimo y consistente entre invocaciones.
- **El volcado a CSV es asíncrono.** `flush()` (llamado en `@TearDown`) solo encola los
  registros; un hilo demonio (`LineCsvWriter`) los escribe a disco en lotes cada 200 ms,
  desacoplando el I/O lento del hilo que ejecuta el benchmark.
- **Cuidado con `Level.Invocation` en modo Throughput.** JMH documenta que los fixtures
  de nivel `Invocation` pueden interferir con la medición en `Mode.Throughput` (a diferencia
  de `AverageTime` o `SampleTime`, donde sí quedan fuera de la ventana medida). Si te importa
  Throughput puro, considera anotar esas clases con `@BenchmarkMode(Mode.AverageTime)` o
  `Mode.SampleTime` en lugar de `Mode.All`, o crear una clase separada solo para el perfilado
  de línea.
- **`NanoClock` es una estimación, no una hora real de nanosegundo.** El reloj de pared del
  SO normalmente no tiene resolución real de nanosegundo; `NanoClock` ancla `System.currentTimeMillis()`
  a `System.nanoTime()` para reportar timestamps con resolución de ns de forma consistente,
  pero la exactitud absoluta respecto al reloj de pared está limitada por la resolución real
  del SO en el instante del anclaje. Para duraciones (lo que importa en el 99% de los casos)
  siempre se usa `System.nanoTime()` puro, que sí es confiable.

## Ajustes rápidos

- Cantidad de iteraciones de medición: `measurementIterations(100)` en `MultiClassBenchmarkRunner`.
- Forks (como en tu trabajo previo con `AbacusBenchmark`, subir a 4 para mayor robustez estadística):
  cambia `.forks(1)` por `.forks(4)`.
- Warmups: `.warmupIterations(5)`.
