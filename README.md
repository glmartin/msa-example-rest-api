# Microservice Architecture (MSA) REST API

REST API for Organization and Application User tracking.

## Simple Build

From the project root directory directory:

```
version=1.0.0 && \
  ./gradlew clean && \
  ./gradlew -Dorg.gradle.project.masterVersion=${version} assemble && \
  docker build -t "local/msa-example-rest-api:$version" .
```

Then, using the name and tag, `local/msa-example-rest-api:1.0.0`, we
can update the `image` for the `app` service in the `docker-compose.yaml` file in the (Dev Cluster Repo)[https://github.com/glmartin/msa-example-dev-cluster]:

```
  app:
    image: local/msa-example-rest-api:1.0.0
```


**NOTE**: I am not publishing the images for these example to any image repository. So they need to be build manually.