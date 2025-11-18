# Usamos una imagen base ligera con Java 17
FROM eclipse-temurin:17-jre

# Configurar zona horaria a America/Bogota
ENV TZ=America/Bogota

# Instalar tzdata para manejar la zona horaria correctamente
RUN apt-get update && \
    apt-get install -y tzdata && \
    rm -rf /var/lib/apt/lists/*

# Copiamos el JAR generado por Maven al contenedor
# Asegúrate que este nombre coincida con el generado en /target
COPY target/producto-ms.jar app.jar

# Exponemos el puerto que usa Spring Boot
EXPOSE 8080

# Comando de arranque
ENTRYPOINT ["java", "-jar", "/app.jar"]