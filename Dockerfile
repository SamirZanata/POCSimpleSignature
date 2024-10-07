FROM openjdk:11-jre-slim

# Define o diretório de trabalho
WORKDIR /app

# Copia o arquivo JAR para o contêiner
COPY target/SignatureApp.jar app.jar

# Executa o aplicativo Java
CMD ["java", "-jar", "app.jar"]

