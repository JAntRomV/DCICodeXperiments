## Micronaut 4.10.3 Documentation

- [User Guide](https://docs.micronaut.io/4.10.3/guide/index.html)
- [API Reference](https://docs.micronaut.io/4.10.3/api/index.html)
- [Configuration Reference](https://docs.micronaut.io/4.10.3/guide/configurationreference.html)
- [Micronaut Guides](https://guides.micronaut.io/index.html)
---

- [Micronaut Maven Plugin documentation](https://micronaut-projects.github.io/micronaut-maven-plugin/latest/)
## Feature serialization-jackson documentation

- [Micronaut Serialization Jackson Core documentation](https://micronaut-projects.github.io/micronaut-serialization/latest/guide/)


## Feature maven-enforcer-plugin documentation

- [https://maven.apache.org/enforcer/maven-enforcer-plugin/](https://maven.apache.org/enforcer/maven-enforcer-plugin/)


## Feature micronaut-aot documentation

- [Micronaut AOT documentation](https://micronaut-projects.github.io/micronaut-aot/latest/guide/)

## Getting ready

$Compile and run app locally:
$ ./mvnw test
$ ./mvnw mn:run

Create docker image:
$ ./mvnw package -Dpackaging=docker

Create docker image + native image:
$ ./mvnw package -Dpackaging=docker-native -Pgraalvm

RUN
$ docker run -p 8080:8080 dcixperiment001

DISABLE SUDO 
$ docker ps (if permission denied, execure the next line)
$ sudo usermod -aG docker $USER
THEN RESTART
