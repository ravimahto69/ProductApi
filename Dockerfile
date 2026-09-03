FROM maven:3.9-eclipse-temurin-17

WORKDIR /app
COPY . .
RUN mvn package -DskipTests

EXPOSE 9090
CMD ["java", "-jar", "target/ProductApi-0.0.1-SNAPSHOT.jar"]
