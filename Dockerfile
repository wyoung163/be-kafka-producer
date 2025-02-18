FROM openjdk:17-jdk-alpine

WORKDIR /app
COPY . .

RUN chmod +x gradlew
RUN apk add --no-cache findutils
RUN ./gradlew clean build

CMD ["java", "-jar", "build/libs/oliveyoung-be-0.0.1-SNAPSHOT.jar"]