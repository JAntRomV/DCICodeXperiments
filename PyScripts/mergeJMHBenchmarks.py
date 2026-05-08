"""
mergeJMHBenchmarks.py
==================
Script para unir múltiples CSVs de resultados JMH (Java Microbenchmark Harness)
en un único DataFrame enriquecido y exportarlo como CSV unificado.

Uso:
    python mergeJMHBenchmarks.py

Ajusta la variable RUTA_CSVS si tus archivos están en otra carpeta.
"""

import pandas as pd
import glob
import os
import sys

# ─────────────────────────────────────────────
# CONFIGURACIÓN
# ─────────────────────────────────────────────

# Carpeta donde están todos los CSVs de resultados
RUTA_CSVS = "./Analyzers/JMH/jmh-code-analyzer/1000/AbacusV1Refactored"

# Nombre del archivo de salida
ARCHIVO_SALIDA = "./Data/1000/Refactored/jmh_refactored.csv"

# ─────────────────────────────────────────────
# PASO 1: Cargar y concatenar todos los CSVs
# ─────────────────────────────────────────────

patron = os.path.join(RUTA_CSVS, "*.csv")
archivos = sorted(glob.glob(patron))

if not archivos:
    print(f"[ERROR] No se encontraron archivos CSV en: {RUTA_CSVS}")
    sys.exit(1)

print(f"[INFO] Archivos encontrados: {len(archivos)}")

dfs = []
archivos_con_error = []

for archivo in archivos:
    try:
        df = pd.read_csv(archivo)
        dfs.append(df)
    except Exception as e:
        print(f"  [WARN] No se pudo leer '{archivo}': {e}")
        archivos_con_error.append(archivo)

if not dfs:
    print("[ERROR] Ningún archivo pudo ser leído.")
    sys.exit(1)

df_total = pd.concat(dfs, ignore_index=True)
print(f"[INFO] Total de filas concatenadas: {len(df_total)}")


# ─────────────────────────────────────────────
# PASO 2: Separar la columna Benchmark
# Formato: "com.bench.AbacusBenchmark.testMethod:gc.alloc.rate"
#          benchmark_base          :  metric
# ─────────────────────────────────────────────

split_benchmark = df_total["Benchmark"].str.split(":", n=1, expand=True)
df_total["benchmark_base"] = split_benchmark[0]
df_total["metric"] = split_benchmark[1]  # NaN cuando no hay sufijo

# Nombre corto del método (último segmento del package)
df_total["method_name"] = df_total["benchmark_base"].str.split(".").str[-1]


# ─────────────────────────────────────────────
# PASO 3: Descomponer Param: className
# Formato: "Code.Java.Abacus.v1.Rare.AreaCuadrado"
#           [0]  [1]   [2]   [3] [4]      [5]
# ─────────────────────────────────────────────

partes = df_total["Param: className"].str.split(".")

df_total["language"]   = partes.str[1]
df_total["tool"]       = partes.str[2]
df_total["version"]    = partes.str[3]
df_total["category"]   = partes.str[4]
df_total["class_name"] = partes.str[5]


# ─────────────────────────────────────────────
# PASO 4: Renombrar columnas con caracteres especiales
# ─────────────────────────────────────────────

df_total.rename(columns={
    "Score Error (99.9%)": "score_error",
    "Param: className":    "param_class",
}, inplace=True)


# ─────────────────────────────────────────────
# PASO 5: Reordenar columnas para mayor claridad
# ─────────────────────────────────────────────

columnas_ordenadas = [
    # Identificadores de la clase
    "class_name",
    "language",
    "tool",
    "version",
    "category",
    "param_class",
    # Identificadores del benchmark
    "method_name",
    "benchmark_base",
    "metric",
    "Benchmark",
    # Configuración de la prueba
    "Mode",
    "Threads",
    "Samples",
    # Resultados
    "Score",
    "score_error",
    "Unit",
]

# Solo incluir columnas que existan en el DataFrame
columnas_finales = [c for c in columnas_ordenadas if c in df_total.columns]
df_total = df_total[columnas_finales]


# ─────────────────────────────────────────────
# PASO 6: Exportar a CSV
# ─────────────────────────────────────────────

df_total.to_csv(ARCHIVO_SALIDA, index=False)
print(f"[OK] Archivo unificado guardado en: {ARCHIVO_SALIDA}")


# ─────────────────────────────────────────────
# RESUMEN FINAL
# ─────────────────────────────────────────────

print("\n" + "=" * 50)
print("RESUMEN DEL DATAFRAME UNIFICADO")
print("=" * 50)
print(f"  Filas totales      : {len(df_total):,}")
print(f"  Columnas           : {len(df_total.columns)}")
print(f"  Clases únicas      : {df_total['class_name'].nunique()}")
print(f"  Herramientas       : {sorted(df_total['tool'].dropna().unique().tolist())}")
print(f"  Versiones          : {sorted(df_total['version'].dropna().unique().tolist())}")
print(f"  Modos de benchmark : {sorted(df_total['Mode'].dropna().unique().tolist())}")
if archivos_con_error:
    print(f"\n  [WARN] Archivos con error ({len(archivos_con_error)}):")
    for f in archivos_con_error:
        print(f"    - {f}")
print("=" * 50)

print("\nPrimeras filas del DataFrame:")
print(df_total.head(5).to_string())