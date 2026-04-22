# DCICodeXperiments
This repository was created for research purposes to apply polyglot programming

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