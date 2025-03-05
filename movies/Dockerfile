FROM eclipse-temurin:23-noble AS builder
# FROM eclipse-temurin:23-jdk-alpine AS builder

WORKDIR /src

# RUN ulimit -c unlimited && chmod a+x mvnw && ./mvnw package -Dmaven.test.skip=true

# # Install Maven
# RUN apk add --no-cache maven

# copy files
COPY mvnw .
COPY pom.xml .

COPY .mvn .mvn
COPY src src
COPY data data

# make mvnw executable
RUN chmod a+x mvnw && /src/mvnw package -Dmaven.test.skip=true
# RUN mvn package -D.maven.test.skip=true

FROM eclipse-temurin:23-jre-noble

WORKDIR /app

COPY --from=builder /src/target/movies-0.0.1-SNAPSHOT.jar app.jar
COPY --from=builder /src/data data

ENV PORT=8080
ENV SPRING_DATASOURCE_URL=
ENV SPRING_DATA_MONGODB_URI=

EXPOSE ${PORT}

SHELL [ "/bin/sh", "-c" ]
ENTRYPOINT SERVER_PORT=${PORT} java -jar app.jar