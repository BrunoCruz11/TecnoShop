# ---- 1) Compilar: genera target/tecnoshop.jar con todas las dependencias ----
FROM maven:3.9-eclipse-temurin-21 AS compilacion
WORKDIR /app
# primero solo el pom: si no cambian las dependencias, Docker reutiliza esta capa y no las vuelve a bajar
COPY pom.xml .
RUN mvn -q -B dependency:go-offline
COPY src ./src
RUN mvn -q -B package -DskipTests

# ---- 2) Imagen final: solo el JRE y el .jar (sin Maven ni codigo fuente) ----
FROM eclipse-temurin:21-jre-alpine
RUN addgroup -S tecnoshop && adduser -S tecnoshop -G tecnoshop
WORKDIR /app
COPY --from=compilacion /app/target/tecnoshop.jar tecnoshop.jar
# no corre como root: si alguien lograra ejecutar codigo en el contenedor, tendria menos permisos
USER tecnoshop

ENV APP_ENV=prod \
    PORT=8080
EXPOSE 8080
HEALTHCHECK --interval=15s --timeout=3s --start-period=40s --retries=3 \
    CMD wget -qO /dev/null http://127.0.0.1:8080/api/salud || exit 1
# usa hasta el 75% de la memoria que tenga asignada el contenedor
ENTRYPOINT ["java", "-XX:MaxRAMPercentage=75", "-jar", "tecnoshop.jar"]
