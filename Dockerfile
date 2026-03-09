FROM eclipse-temurin:17-jre

# Crear directorio de la app
WORKDIR /app

# Copiar el JAR genérico (excluyendo el -plain.jar)
COPY build/libs/[^p]*.jar app.jar

# Exponer el puerto
EXPOSE 8083

# Variables de entorno estándar
ENV SPRING_PROFILES_ACTIVE=production
ENV JAVA_OPTS="-Xms256m -Xmx512m"

# Comando de inicio
# Nota: Spring Boot detectará automáticamente el context-path de application.yml
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar --server.port=8083"]
