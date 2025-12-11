# 1. Etapa de Construcción (Build Stage)
# Usamos una imagen base de Maven con Java 21 para construir el proyecto
FROM maven:3.9.6-eclipse-temurin-21 AS build

# Establece el directorio de trabajo dentro del contenedor
WORKDIR /app

# Copia los archivos de configuración de Maven (pom.xml)
COPY pom.xml .

# Copia todo el código fuente del proyecto
COPY src /app/src

# Ejecuta el comando de construcción para crear el JAR
# El archivo JAR se llamará: hogarfix-0.0.1-SNAPSHOT.jar
RUN mvn clean package -DskipTests

# 2. Etapa de Ejecución (Run Stage)
# Usamos una imagen ligera de Java 21 para ejecutar solo el JAR
FROM eclipse-temurin:21-jre-alpine

# Define el directorio de la aplicación
WORKDIR /app

# Copia el JAR creado en la etapa de construcción al directorio de ejecución
# El nombre del archivo JAR es: hogarfix-0.0.1-SNAPSHOT.jar (de tu pom.xml)
COPY --from=build /app/target/hogarfix-0.0.1-SNAPSHOT.jar app.jar

# Expone el puerto por defecto de Spring Boot (8080)
EXPOSE 8080

# Comando para ejecutar la aplicación cuando el contenedor se inicia
ENTRYPOINT ["java", "-jar", "app.jar"]