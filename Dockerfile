# Etapa 1: Compilación
FROM gradle:8.9-jdk17 AS build
WORKDIR /home/gradle/src

# Copiar archivos de configuración de Gradle para aprovechar la caché de capas
COPY build.gradle settings.gradle ./
COPY gradle ./gradle
COPY gradlew ./

# Descargar dependencias primero (opcional, ayuda a la caché)
# RUN ./gradlew dependencies --no-daemon

# Copiar el código fuente y compilar el JAR
COPY . .
RUN ./gradlew bootJar --no-daemon

# Etapa 2: Imagen de ejecución
FROM eclipse-temurin:17-jre
WORKDIR /app

# Copiar el JAR generado desde la etapa de compilación
# La ruta en la etapa 'build' es /home/gradle/src/build/libs/
COPY --from=build /home/gradle/src/build/libs/*SNAPSHOT.jar app.jar

# Exponer el puerto
EXPOSE 8083

# Variables de entorno estándar
ENV SPRING_PROFILES_ACTIVE=production
ENV JAVA_OPTS="-Xms256m -Xmx512m"

# Comando de inicio
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar --server.port=8083"]
