# Usamos una imagen base ligera con Java 17
FROM eclipse-temurin:17-jre

# Configurar zona horaria a America/Bogota
ENV TZ=America/Bogota

# Instalar tzdata para manejar la zona horaria correctamente
RUN apt-get update && \
    apt-get install -y tzdata && \
    rm -rf /var/lib/apt/lists/*

# Copiamos el JAR generado por Maven al contenedor
COPY target/inventario-ms.jar app.jar

# Exponemos el puerto que usa Spring Boot (por defecto 8080)
EXPOSE 8090

# Comando de arranque
ENTRYPOINT ["java", "-jar", "/app.jar"]
