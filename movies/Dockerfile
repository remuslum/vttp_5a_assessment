FROM eclipse-temurin:23-noble AS builder

WORKDIR /src

# copy files
COPY mvnw .
COPY pom.xml .

COPY .mvn .mvn
COPY src src
COPY movies_post_2010.json movies_post_2010.json

# make mvnw executable
RUN chmod a+x mvnw && /src/mvnw package -Dmaven.test.skip=true

FROM eclipse-temurin:23-jre-noble

WORKDIR /app

COPY --from=builder /src/target/movies-0.0.1-SNAPSHOT.jar app.jar
COPY --from=builder /src/movies_post_2010.json movies_post_2010.json

ENV PORT=8080
ENV SPRING_DATASOURCE_URL=
ENV SPRING_DATA_MONGODB_URI=

EXPOSE ${PORT}

ENTRYPOINT SERVER_PORT=${PORT} java -jar app.jar