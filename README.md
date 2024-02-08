# Microservice Arcitecture (MSA) REST API

REST API for Organization and Application User tracking.

# Simple Build

From the `restapi` directory:

```
version=1.0.0 && \
  ./gradlew clean && \
  ./gradlew -Dorg.gradle.project.masterVersion=${version} assemble && \
  docker build -t "local/rest-api:$version" .
```