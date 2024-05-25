#syntax=docker/dockerfile:1.7-labs
FROM eclipse-temurin:22-jre-jammy AS unpacking

ARG JAR_FILE=target/fruit-stock-management-*.jar

# https://www.docker.com/blog/new-dockerfile-capabilities-v1-7-0/
COPY --exclude=**/** --exclude=!$JAR_FILE \
     $JAR_FILE /app.jar

# https://docs.spring.io/spring-boot/3.3/reference/deployment/efficient.html#deployment.efficient.unpacking
RUN <<EOF
# The destination is automatically created
java -Djarmode=tools -jar /app.jar  extract --destination /app --application-filename app.jar
EOF

FROM eclipse-temurin:22-jre-jammy

ENV JAVA_OPTS_APPEND=""
ENV JAVA_OPTS="--enable-preview -javaagent:/app/otel-agent.jar"

WORKDIR /app

ARG OTEL_AGENT_VERSION=1.33.2

ADD --chmod=777 https://github.com/open-telemetry/opentelemetry-java-instrumentation/releases/download/v${OTEL_AGENT_VERSION}/opentelemetry-javaagent.jar ./otel-agent.jar

RUN <<EOF
set -e
addgroup --gid 1001 fruit
useradd --uid 1001 --gid 1001 -m fruit
EOF

COPY --from=unpacking --chown=fruit:fruit /app/ ./

EXPOSE 8080

USER fruit

# Shell Form (Under The hood -> /bin/sh -c)
## prevents any CMD
## the executable will not be the container's PID 1, and will not receive Unix signals.
## ENTRYPOINT java "$JAVA_OPTS" "$JAVA_OPTS_APPEND" -jar /app/app.jar
## ENTRYPOINT [ "sh", "-c", "java -jar $JAVA_OPTS $JAVA_OPTS_APPEND /app/app.jar" ]

# Exec Form
## the exec form doesn't automatically invoke a command shell
## This means that normal shell processing, such as variable substitution, doesn't happen

## ENTRYPOINT ["java" , "-jar", "/app/app.jar"]

## Remember to use "exec java …" to launch the java process (so that it can handle the KILL signals)
## exec java "$JAVA_OPTS" "$JAVA_OPTS_APPEND" -jar /app/app.jar
## ENTRYPOINT ["/app/run.sh"]

ENTRYPOINT exec java $JAVA_OPTS $JAVA_OPTS_APPEND -jar /app/app.jar
