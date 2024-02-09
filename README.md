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

## Testing

This code allows for unit and integration testing. There is a specific package, `glmartin.java.restapi.integrationtesting`, to run
integration tests. The tests will need to extend `RestIntegrationTester`. This will provide a full database, and run the migrations.
To run the latest migrations, the value for the static variable `DB_MIG_IMAGE` will need to be set to the latest DB migration
image. TODO: Refactor this to make it more dynamic (Spring doesn't support `@Value` on static variables.)