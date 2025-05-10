package com.example.oliveyoungbe.service;

import com.example.oliveyoungbe.dto.TicketBookingDto;
import com.example.oliveyoungbe.dto.TicketRequestDto;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.kafka.support.SendResult;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Service
@RequiredArgsConstructor
public class KafkaProducerService {

    @Value("${kafka.topic.typeRequest}")
    private String ticketRequestTopic;

    @Value("${kafka.topic.typeBooking}")
    private String ticketBookingTopic;

    @Value("${kafka.partition.num}")
    private String partitionNum;

    private final KafkaTemplate<String, TicketRequestDto> ticketRequestKafkaTemplate;

    private final KafkaTemplate<String, TicketBookingDto> ticketBookingKafkaTemplate;

    private final AdminClient adminClient;

    /*
        1. ticketRequest 토픽에 예약 요청 메시지 생성

        *** 메시지 형식 예
        Partition: 0  (Kafka가 Key를 기준으로 파티션을 결정)
        Offset: 45678
        Key: ticketRequest_{uuid}
        Headers:
            kafka_topic: ticketRequestTopic
        Value:
        {
          "eventId": "concert-456",
          "timestamp": "2025-02-13T12:00:00Z"
        }
     */

    public void sendRequestMessage(TicketRequestDto ticketRequestDto) throws ExecutionException, InterruptedException {
        if(!checkTopicExistence(ticketRequestTopic)) {
            createTopic(ticketRequestTopic, Integer.parseInt(partitionNum));
        }

        Message<TicketRequestDto> message = MessageBuilder
                .withPayload(ticketRequestDto)
                .setHeader(KafkaHeaders.KEY, ticketRequestTopic + "_" + ticketRequestDto.getUuid())
                .setHeader(KafkaHeaders.TOPIC, ticketRequestTopic)
                .build();

        CompletableFuture<SendResult<String, TicketRequestDto>> future = ticketRequestKafkaTemplate.send(message);

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
        Key: ticketBooking_{uuid}
        Headers:
            kafka_topic: ticketBooking
        Value:
        {
          "eventId": "concert-456",
          "timeSlot": "10:00"
          "timestamp": "2025-02-13T12:00:00Z"
        }
     */
    public void sendBookingMessage(TicketBookingDto ticketBookingDto) throws ExecutionException, InterruptedException {
        if(!checkTopicExistence(ticketRequestTopic)) {
            createTopic(ticketRequestTopic, Integer.parseInt(partitionNum));
        }

        Message<TicketBookingDto> message = MessageBuilder
                .withPayload(ticketBookingDto)
                .setHeader(KafkaHeaders.KEY, ticketBookingTopic + "_" + ticketBookingDto.getUuid())
                .setHeader(KafkaHeaders.TOPIC, ticketBookingTopic)
                .build();

        CompletableFuture<SendResult<String, TicketBookingDto>> future = ticketBookingKafkaTemplate.send(message);

        future.whenComplete((result, ex) -> {
            if(ex == null) {
                System.out.println("Producer success: " + result.getProducerRecord().value());
            } else {
                System.out.println("Producer failure: " + ex.getMessage());
            }
        });
    }

    private boolean checkTopicExistence(String topic) throws ExecutionException, InterruptedException {
        return adminClient.listTopics().names().get().contains(topic);
    }

    private void createTopic(String topic, int partitionNum) {
        NewTopic newTopic = new NewTopic(topic, partitionNum, (short) 1);
        adminClient.createTopics(Collections.singletonList(newTopic));
    }
}
