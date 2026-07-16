# DCICodeXperiments
This repository was created for research purposes to apply polyglot programming

## Static Analysis with CK Tool

### 1. Clone CK repo and build jar
```
git clone https://github.com/mauricioaniche/ck.git
```

Now build jar 
```
mvn clean compile package
```
Then move .jar file to root dir

### 2. Crate folder for compiled .class files
```
mkdir -p target/classes
```

### 3. Compile all classes in the packages
Rare version
```
javac -d target/classes \
      -sourcepath . \
      Code/Java/Abacus/v1/Rare/*.java
```

Refactored version
```
javac -d target/classes \
      -sourcepath . \
      Code/Java/Abacus/v1/Refactored/*.java
```

### 4. Create folders to save csv results 
```
mkdir Analyzers/StaticMetricsCK/Abacus_v1_Rare
mkdir Analyzers/StaticMetricsCK/Abacus_v1_Refactored
```

### 4. Execute CK analyzers for each version
Rare version
```
java -jar ck-0.7.1-SNAPSHOT-jar-with-dependencies.jar \
     ./Code/Java/Abacus/v1/Rare \
     false \
     0 \
     true \
     ./Analyzers/StaticMetricsCK/Abacus_v1_Rare
```

Refactored version
```
java -jar ck-0.7.1-SNAPSHOT-jar-with-dependencies.jar \
     ./Code/Java/Abacus/v1/Refactored \
     false \
     0 \
     true \
     ./Analyzers/StaticMetricsCK/Abacus_v1_Refactored
```

## JMH (Java Microbenchmark Harness)

### Class level

Create jmh test project
```
$ mvn archetype:generate \
  -DinteractiveMode=false \
  -DarchetypeGroupId=org.openjdk.jmh \
  -DarchetypeArtifactId=jmh-java-benchmark-archetype \
  -DgroupId=org.sample \
  -DartifactId=jmh-code-analyzer \
  -Dversion=1.0
```

Build project
```
$ cd jmh-code-analyzer/
$ mvn clean verify
```

Run JMH benchmark
```
$ java -jar target/benchmarks.jar
```

Save results JMH in CSV
```
java -jar target/benchmarks.jar
```

Optional
```
java -jar target/benchmarks.jar -rf csv -rff results.csv
```

### Method level

Build project
```
$ cd Analyzers/JMH/jmh-methods
$ mvn clean verify
```

Run JMH benchmark
```
$ java -jar target/benchmarks.jar --I:100 --WI:10 --F:4 --MINH:4096 --MAXH:4096 --JMHMODE:SAMT --THREADS=1
```

| Argument | Description | Default |
| ----------- | ----------- | ----------- |
| --I | Total number of iterations for experiments | 1 |
| --WI | Warmup iterations number| 1 |
| --F | Forks number | 1 |
| --MINH | Min heap size (MB) | 4096 |
| --MAXH | Max heap size (MB) | 4096 |
| --JMHMODE | JMH Benchmark mode (AVGT=AverageTime, THRPT=Throughput, SAMT=SampleTime, SS=SingleShotTime, ALL) | SAMT |
| --THREADS | Threads number | 1 |

This will return a set of csv files with JMH results and nanoseconds time executions per method and parameter number.

## Profiling

### Light version
Change dirs between "Rare" and "Refactored". Then:

```
$ javac Analyzers/Profiler/v1_light/Agents/*.java
$ java Analyzers.Profiler.v1_light.Agents.ClassScanner
$ chmod +x Analyzers/Profiler/v1_light/Agents/01_run_profiling.sh #Optional, only the first time
$ ./Analyzers/Profiler/v1_light/Agents/01_run_profiling.sh
$ java Analyzers.Profiler.v1_light.Agents.JFRParser
```

### Deep version
Change dirs between "Rare" and "Refactored". Then:

```
$ javac Analyzers/Profiler/v2_deep/*.java
$ chmod +x Analyzers/Profiler/v2_deep/*.sh
$ ./Analyzers/Profiler/v2_deep/01_setup_agent.sh
$ ./Analyzers/Profiler/v2_deep/02_run_profiler.sh
$ ./Analyzers/Profiler/v2_deep/04_analyze_csv.sh
```