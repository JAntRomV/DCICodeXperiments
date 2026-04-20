# Sistema de Profiling — DTSCode4Traning
## Paquete: `Code.Java.Abacus.v1.Rare`

---

## Estructura de archivos

```
DTSCode4Traning/
├── Code/Java/Abacus/v1/Rare/          ← tus clases Java
│   ├── ClaseA.java
│   └── ClaseB.java
│
└── Analyzers/Profiler/                 ← todos los artefactos del profiler
    ├── ProfilingAgent.java             ← agente JVM (fuente)
    ├── ProfilingAgent.jar              ← agente JVM (generado por setup_agent.sh)
    ├── setup_agent.sh                  ← compilar el agente (1 vez)
    ├── run_profiler.sh                 ← script principal de profiling
    ├── open_visualvm.sh                ← abrir VisualVM (opcional, visual)
    ├── analyze_csv.sh                  ← estadísticas del CSV
    ├── profiling_results.csv           ← resultado principal (generado)
    ├── profiling_stats_summary.csv     ← estadísticas agregadas (generado)
    └── profiling_summary.txt           ← resumen legible (generado)
```

---

## Prerrequisitos

| Herramienta | Versión mínima | Verificar con |
|---|---|---|
| Java JDK | 11+ | `java -version` |
| javac | 11+ | `javac -version` |
| bash | 4.0+ | `bash --version` |
| VisualVM | 2.x (opcional) | `jvisualvm --version` |

> **VisualVM es opcional.** El profiling automático funciona 100% con el agente
> Java. VisualVM sólo se usa para inspección visual interactiva adicional.

---

## Pasos detallados

### Paso 0 — Posicionarse en la carpeta raíz

```bash
cd /ruta/a/DTSCode4Traning
```

> **Todos los comandos siguientes se ejecutan desde aquí.**

---

### Paso 1 — (Una sola vez) Compilar el agente de profiling

```bash
bash Analyzers/Profiler/setup_agent.sh
```

Qué hace:
- Compila `ProfilingAgent.java` con `javac`
- Crea `ProfilingAgent.jar` con el Manifest correcto (`Premain-Class`)
- Verifica que `java`, `javac` y `jar` están en el PATH

Salida esperada:
```
[SETUP] Compilando ProfilingAgent.java...
[OK]    Compilación exitosa.
[SETUP] Empaquetando ProfilingAgent.jar...
[OK]    ProfilingAgent.jar generado en: .../Analyzers/Profiler/ProfilingAgent.jar
```

---

### Paso 2 — Ejecutar el profiling (script principal)

```bash
bash Analyzers/Profiler/run_profiler.sh
```

Qué hace automáticamente:

1. **Descubre** todas las clases `.java` en `Code/Java/Abacus/v1/Rare/` con `find`
2. **Compila** las clases con `javac`
3. **Inicializa** el CSV con cabecera
4. **Por cada clase** encontrada:
   - Ejecuta `java -javaagent:ProfilingAgent.jar ... ClassName` exactamente **100 veces**
   - En cada ejecución el agente captura (antes y después del `main()`):
     - Tiempo de ejecución (ms)
     - Heap usado antes/después (MB)
     - Heap máximo disponible (MB)
     - Conteo de GC antes/después
     - Tiempo acumulado de GC (ms)
     - Carga de CPU del proceso (%)
     - Número de threads vivos
   - Escribe una fila por ejecución en `profiling_results.csv`
5. Genera `profiling_summary.txt` con promedios por clase

---

### Paso 3 — (Opcional) Abrir VisualVM para inspección visual

```bash
# Abrir VisualVM standalone
bash Analyzers/Profiler/open_visualvm.sh

# Conectar a un proceso específico por PID
bash Analyzers/Profiler/open_visualvm.sh 12345
```

Para monitoreo en tiempo real con JMX, edita `run_profiler.sh` y añade estas
flags al comando `java`:

```bash
-Dcom.sun.management.jmxremote \
-Dcom.sun.management.jmxremote.port=9010 \
-Dcom.sun.management.jmxremote.authenticate=false \
-Dcom.sun.management.jmxremote.ssl=false
```

Luego en VisualVM: **File → Add JMX Connection → localhost:9010**

---

### Paso 4 — Analizar estadísticas del CSV

```bash
bash Analyzers/Profiler/analyze_csv.sh
```

Genera `profiling_stats_summary.csv` con una fila por clase y columnas:

| Columna | Descripción |
|---|---|
| `class_name` | Nombre de la clase |
| `runs` | Número de ejecuciones registradas |
| `avg_time_ms` | Tiempo promedio de ejecución |
| `median_time_ms` | Mediana del tiempo |
| `min_time_ms` / `max_time_ms` | Rango |
| `stddev_ms` | Desviación estándar |
| `avg_heap_delta_mb` | Delta promedio de heap por ejecución |
| `avg_gc_cycles` | Ciclos de GC promedio por ejecución |
| `success_rate_pct` | % de ejecuciones con exit code 0 |

---

## Estructura del CSV principal (`profiling_results.csv`)

```
class_name, run_number, execution_time_ms,
heap_used_before_mb, heap_used_after_mb, heap_max_mb,
gc_count_before, gc_count_after,
gc_time_ms_before, gc_time_ms_after,
cpu_load_percent, threads_live,
exit_code, timestamp
```

---

## Solución de problemas frecuentes

### "No se encontraron archivos .java"
Verifica que la estructura de carpetas sea exactamente:
```
DTSCode4Traning/Code/Java/Abacus/v1/Rare/*.java
```

### "Error: Main method not found"
La clase no tiene `public static void main(String[] args)`. El agente igualmente
registrará el intento con `exit_code` distinto de 0.

### "ProfilingAgent.jar no encontrado"
Ejecuta primero `bash Analyzers/Profiler/setup_agent.sh`

### Las métricas de CPU muestran `-1`
Ocurre en JVMs sin `com.sun.management.OperatingSystemMXBean` (OpenJ9, GraalVM).
El resto de métricas siguen siendo válidas.

### VisualVM no se encuentra
Descarga desde https://visualvm.github.io/ y asegura que `jvisualvm` está en el PATH.
El profiling con el agente **no requiere VisualVM instalado**.

---

## Importar el CSV en VisualVM (método alternativo manual)

1. Abrir VisualVM
2. **File → Load** (o **File → Import**)
3. Seleccionar `Analyzers/Profiler/profiling_results.csv`
4. Para análisis avanzado, instalar el plugin **VisualVM-Extensions** desde
   **Tools → Plugins → Available Plugins**

---

## Ejemplo de salida del CSV

```csv
class_name,run_number,execution_time_ms,heap_used_before_mb,...
MyClass,1,12.45,4.20,4.85,256.00,0,1,0,5,0.03,8,0,2024-01-15T10:30:00Z
MyClass,2,11.98,4.85,4.90,256.00,1,1,5,5,0.02,8,0,2024-01-15T10:30:01Z
```
