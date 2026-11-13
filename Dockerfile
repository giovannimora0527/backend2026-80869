# ============================================================
#  Dockerfile del backend - Clinica DDSF (Spring Boot, Java 21)
# ============================================================
#
#  Se usa una construccion en dos etapas (multi-stage build):
#
#   Etapa 1 "build": trae Maven + JDK 21, compila el proyecto y
#     produce el archivo .jar ejecutable. Esta etapa es pesada
#     (incluye el compilador y las dependencias de construccion),
#     pero no forma parte de la imagen final.
#
#   Etapa 2 "runtime": trae unicamente el JRE 21 (lo minimo para
#     ejecutar Java) y copia el .jar ya compilado desde la etapa 1.
#     El resultado es una imagen liviana que solo sabe ejecutar la
#     aplicacion.
# ============================================================

# ---------- Etapa 1: construccion ----------
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app

# Primero se copia solo el pom.xml y se descargan las dependencias.
# Docker cachea esta capa: si el pom no cambia, en construcciones
# posteriores no se vuelven a descargar las dependencias (mas rapido).
COPY pom.xml .
RUN mvn -q dependency:go-offline

# Ahora se copia el codigo fuente y se empaqueta el .jar.
# Se omiten las pruebas para que la imagen se construya de forma
# reproducible sin depender de servicios externos (correo, BD).
COPY src ./src
RUN mvn -q clean package -DskipTests

# ---------- Etapa 2: ejecucion ----------
FROM eclipse-temurin:21-jre
WORKDIR /app

# Se copia el .jar generado en la etapa de construccion.
COPY --from=build /app/target/*.jar app.jar

# El backend escucha en el puerto 8080 (con context-path /clinica/v1).
EXPOSE 8080

# Se activa el perfil "docker", que lee la configuracion (BD, correo,
# JWT) desde variables de entorno definidas en docker-compose.yml.
ENV SPRING_PROFILES_ACTIVE=docker

ENTRYPOINT ["java", "-jar", "app.jar"]
