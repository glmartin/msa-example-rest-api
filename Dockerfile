FROM eclipse-temurin:17-jdk-alpine
LABEL maintainer "Greg Martin <greg.l.martin@gmail.com>"
LABEL revision "202402011028"

COPY docker/entrypoint.sh /entrypoint.sh
COPY ./build/libs/restapi.jar /restapi.jar

CMD ["/entrypoint.sh"]