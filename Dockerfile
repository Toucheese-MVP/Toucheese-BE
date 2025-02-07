FROM eclipse-temurin:17 as build

WORKDIR /app
COPY . .

COPY gradlew gradlew.bat settings.gradle build.gradle gradle.properties ./
COPY gradle ./gradle

RUN chmod +x ./gradlew
RUN ./gradlew dependencies --no-daemon

COPY . .
RUN ./gradlew clean bootJar --no-daemon --stacktrace

RUN ls -l build/libs/

RUN mv $(find build/libs -maxdepth 1 -name "*.jar" ! -name "*plain.jar" | head -n 1) app.jar

FROM eclipse-temurin:17-jre

WORKDIR /app

COPY --from=build /app/app.jar .

CMD ["java", "-jar", "app.jar"]
EXPOSE 8080
