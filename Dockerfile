# ---------- Сборка ----------
FROM eclipse-temurin:21.0.1_12-jdk-ubi9-minimal as build

WORKDIR /app

COPY pom.xml .
COPY mvnw .
COPY .mvn .mvn
COPY src src

RUN chmod +x ./mvnw
RUN ./mvnw package -DskipTests

# ---------- Запуск ----------
FROM eclipse-temurin:21.0.1_12-jre-ubi9-minimal

WORKDIR /app

COPY --from=build /app/target/*.jar app.jar

#ENV SPRING_PROFILES_ACTIVE=prod

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar /app/app.jar"]