# Metasu Backend Application
[![Build and deploy](https://github.com/cornelsen/metasu-app-backend/actions/workflows/.docker.yaml/badge.svg)](https://github.com/cornelsen/metasu-app-backend/actions/workflows/.docker.yaml)

[[Develop instance]](https://app.dev.metasu.de)\
[[Staging instance]](https://app.stage.metasu.de)

## Metasu in a nutshell

Metasu is a National Edication Platform connecting educational offerings and creating access to a wider range of educational opportunities for every citizen, achieved by common standards, formats and interoperable structures.

## Guides

Metasu represents a secure, smart connected systems that enables searching through decentralized content and data.

Based on [LTI Resource Search Service](https://www.imsglobal.org/sites/default/files/spec/lti-rs/v1p0/information_model/rsservicev1p0_infomodelv1p0.html)

### Integration guidelines

To integrate with Metasu, contact us via [support](http://contact-us)

#### Authentication

Providing searchable content requires successfull authentication with Metasu platform.

#### Consumable Rest API

To provide searchable content, you need to follow [LTI Resource Search Service](https://www.imsglobal.org/sites/default/files/spec/lti-rs/v1p0/information_model/rsservicev1p0_infomodelv1p0.html) standards.

Metasu application consumes following [REST API](http://swagger-api) endpoints that has to be provided:

- [Filters](http://swagger-api/api/filters) - provides available searchable filters, such as subjects, school types, study years, etc.
- [Search](http://swagger-api/api/search/related) - provides searchable learning content
- [Most relevant](http://swagger-api/api/search/most-relevant) - provides most relevant learning content
- [Details](http://swagger-api/api/api/details) - provides additional details of a content found


### Used technologies

#### Frameworks

- Spring Boot

#### Libs

- [Spring Boot 3.1.3](https://mvnrepository.com/artifact/org.springframework.boot/spring-boot/3.1.3)
- [Project Lombok 1.18.28](https://mvnrepository.com/artifact/org.projectlombok/lombok/1.18.28)
- [MapStruct Processor 1.5.3.Final](https://mvnrepository.com/artifact/org.mapstruct/mapstruct/1.5.3.Final)
- [Flyway Core 9.16.3](https://mvnrepository.com/artifact/org.flywaydb/flyway-core/9.16.3)
- [Caffeine Cache 3.1.3](https://mvnrepository.com/artifact/com.github.ben-manes.caffeine/caffeine/3.1.3)
- [Micrometer Registry Prometheus 1.11.3](https://mvnrepository.com/artifact/io.micrometer/micrometer-registry-prometheus/1.11.3)
- [PostgreSQL JDBC Driver 42.6.0](https://mvnrepository.com/artifact/org.postgresql/postgresql/42.6.0)
- [Jackson Data 2.15.3](https://mvnrepository.com/artifact/com.fasterxml.jackson.core/jackson-core/2.15.3)
- [Apache Commons Codec 1.15](https://mvnrepository.com/artifact/commons-codec/commons-codec/1.15)

#### Database

- PostgreSQL

#### Internal integration

- [Enmeshed](https://enmeshed.eu)
- [Keycloak](https://www.keycloak.org)

### Requirements

Before getting started with the project, make sure you have the following tools installed:

- [JDK 19](https://www.oracle.com/java/technologies/javase/jdk19-archive-downloads.html)
- [Maven 3](https://maven.apache.org)


### Running with docker


```bash
docker build -t nep/metasu-app-backend:latest .
docker run -d -p 8080:8080 nep/metasu-app-backend:latest
```

## Report Issues

You can [view existing issues](https://github.com/Metasu/Issues/issues) or [report a new issue](https://github.com/Metasu/Issues/issues/new?template=bug_report.yml).

## License

Metasu is Open Source software released under the MIT License. See [LICENSE](https://github.com/openai/whisper/blob/main/LICENSE) for further details.

