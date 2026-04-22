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

Create jmh test project
$ mvn archetype:generate \
  -DinteractiveMode=false \
  -DarchetypeGroupId=org.openjdk.jmh \
  -DarchetypeArtifactId=jmh-java-benchmark-archetype \
  -DgroupId=org.sample \
  -DartifactId=jmh-code-analyzer \
  -Dversion=1.0

Build project
$ cd test/
$ mvn clean verify

Run JMH benchmark
$ java -jar target/benchmarks.jar

Save results JMH in CSV
  java -jar target/benchmarks.jar -rf csv -rff results.csv
