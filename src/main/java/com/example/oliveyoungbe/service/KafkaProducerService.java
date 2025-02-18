package com.example.oliveyoungbe.service;

import com.example.oliveyoungbe.dto.TicketBooking;
import com.example.oliveyoungbe.dto.TicketRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.kafka.support.SendResult;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
public class KafkaProducerService {

    @Value("$spring.kafka.typeRequest")
    private String ticketRequestTopic;

    @Value("$spring.kafka.typeBooking")
    private String ticketBookingTopic;

    private final KafkaTemplate<String, TicketRequest> ticketRequestKafkaTemplate;

    private final KafkaTemplate<String, TicketBooking> ticketBookingKafkaTemplate;

    /*
        1. ticketRequest 토픽에 예약 요청 메시지 생성

        *** 메시지 형식 예
        Partition: 0  (Kafka가 Key를 기준으로 파티션을 결정)
        Offset: 45678
        Key: ticketRequest1
        Headers:
            kafka_topic: ticketRequest
        Value:
        {
          "eventId": "concert-456",
          "timestamp": "2025-02-13T12:00:00Z"
        }
     */

    public void sendRequestMessage(TicketRequest ticketRequest) {
        Message<TicketRequest> message = MessageBuilder
                .withPayload(ticketRequest)
                .setHeader(KafkaHeaders.KEY, "ticketRequestKey1")
                .setHeader(KafkaHeaders.TOPIC, "ticketRequestTopic")
                .build();

        CompletableFuture<SendResult<String, TicketRequest>> future = ticketRequestKafkaTemplate.send(message);

        future.whenComplete((result, ex) -> {
            if(ex == null) {
                System.out.println("Producer success: " + result.getProducerRecord().value());
            } else {
                System.out.println("Producer failure: " + ex.getMessage());
            }
        });
    }

     /*
        2. ticketBooking 토픽에 예약 완료 메시지 생성
        *** 메시지 형식 예
        Partition: 0
        Offset: 45678
        Key: ticketBooking1
        Headers:
            kafka_topic: ticketBooking
        Value:
        {
          "eventId": "concert-456",
          "timeSlot": "10:00"
          "timestamp": "2025-02-13T12:00:00Z"
        }
     */
    public void sendBookingMessage(TicketBooking ticketBooking) {
        Message<TicketBooking> message = MessageBuilder
                .withPayload(ticketBooking)
                .setHeader(KafkaHeaders.KEY, "ticketBookingKey1")
                .setHeader(KafkaHeaders.TOPIC, "ticketBookingTopic")
                .build();

        CompletableFuture<SendResult<String, TicketBooking>> future = ticketBookingKafkaTemplate.send(message);

        future.whenComplete((result, ex) -> {
            if(ex == null) {
                System.out.println("Producer success: " + result.getProducerRecord().value());
            } else {
                System.out.println("Producer failure: " + ex.getMessage());
            }
        });
    }
}
