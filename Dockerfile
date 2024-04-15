FROM maven AS build
WORKDIR /intégrator-project
COPY . .
RUN mvn clean install


FROM adoptopenjdk/openjdk16 AS deploy
LABEL description="Java 16 Docker image build to run Integrator project"

ARG JAR_FILE="/intégrator-project/server/target/integrator-project.jar"
ENV TZ="Europe/Paris"
ENV spring_profiles_active="prod"

COPY --from=build ${JAR_FILE} /opt/app/app.jar
RUN chmod u+x /opt/app/app.jar

WORKDIR /opt/app
RUN apt-get update \
  && apt-get install -y --no-install-recommends wget=1.20.3-1ubuntu2 \
    && rm -rf /var/lib/apt/lists/*

EXPOSE 8080

CMD ["java", "-jar", "/opt/app/app.jar"]