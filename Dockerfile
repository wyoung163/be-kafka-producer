FROM openjdk:21

WORKDIR /app
COPY . .

ENV SPRING_SERVER_PORT=8080
ENV KAFKA_BOOTSTRAP_URL=localhost:9092
ENV KAFKA_REQUEST_TOPIC_NAME=ticketRequest
ENV KAFKA_BOOKING_TOPIC_NAME=ticketBooking
ENV SPRING_PROFILES_ACTIVE=prod

RUN chmod +x gradlew
RUN apk add --no-cache findutils
RUN ./gradlew clean
RUN ./gradlew build

CMD ["java", "-jar", "build/libs/oliveyoung-be-0.0.1-SNAPSHOT.jar", "-spring.profiles.active=${SPRING_PROFILES_ACTIVE}"]