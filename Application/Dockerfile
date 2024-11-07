FROM maven:3.8.1-openjdk-17-slim AS build
LABEL authors="strange"

WORKDIR /app

COPY pom.xml .
COPY src ./src

CMD ["mvn", "clean", "exec:java" ,"-Dexec.mainClass=\"fr.univlille.s3.S302.Main\""]