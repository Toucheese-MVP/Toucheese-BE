FROM gradle:8.5-jdk17 AS build
WORKDIR /app

COPY build.gradle.kts settings.gradle.kts gradle.properties ./
COPY gradle ./gradle
RUN gradle dependencies --no-daemon

COPY . .
RUN chmod +x ./gradlew
RUN ./gradlew clean bootJar --stacktrace
RUN ls -l build/libs/
RUN mv build/libs/*.jar app.jar

FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /app/app.jar .
CMD ["java", "-jar", "app.jar"]
EXPOSE 8080
