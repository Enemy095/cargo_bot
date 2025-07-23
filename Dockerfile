FROM eclipse-temurin:24-jre-alpine as runtime
WORKDIR /app

# Копируем fat-jar (замени путь/имя)
ARG JAR_FILE=build/libs/cargo-bot*.jar
COPY ${JAR_FILE} app.jar

EXPOSE 8084
ENTRYPOINT ["java","-jar","/app/app.jar"]
