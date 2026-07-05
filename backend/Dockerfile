# ==============================================================================
# STAGE 1: BUILD (Mengompilasi source code Java menjadi file JAR)
# ==============================================================================
FROM maven:3.8.5-openjdk-17-slim AS build
WORKDIR /app

# 1. Salin file konfigurasi Maven pom.xml
COPY pom.xml .

# 2. Download dependency Maven terlebih dahulu agar di-cache oleh Docker
RUN mvn dependency:go-offline -B

# 3. Salin seluruh source code Java
COPY src ./src

# 4. Compile dan kemas aplikasi menjadi file .jar tanpa menjalankan unit test
RUN mvn clean package -DskipTests

# ==============================================================================
# STAGE 2: RUNTIME (Menjalankan file JAR menggunakan JRE ringan)
# ==============================================================================
# Menggunakan eclipse-temurin alpine JRE yang sangat hemat RAM dan berukuran kecil
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Salin hasil file JAR dari Stage 1 (Build) ke folder runtime ini
COPY --from=build /app/target/*.jar app.jar

# Port internal aplikasi backend (Spring Boot default port 8020)
EXPOSE 8020

# Konfigurasi memory limits JVM saat startup untuk menghemat RAM VPS
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
