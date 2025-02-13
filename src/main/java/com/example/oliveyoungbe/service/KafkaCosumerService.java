package com.example.oliveyoungbe.service;

import com.example.oliveyoungbe.dto.TicketRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;
import static org.apache.kafka.common.requests.DeleteAclsResponse.log;

@Slf4j
@Service
public class KafkaCosumerService {
    @KafkaListener(topics = "${kafka.topic.typeRequest}",
            groupId = "${spring.kafka.consumer.group-id}")
    public void consume(@Payload TicketRequest ticketRequest,
                        @Headers MessageHeaders messageHeaders,
                        Acknowledgment acknowledgment) {

        if(isValid()){
            processTicketRequest(ticketRequest);
            acknowledgment.acknowledge();
        } else {
            // 커밋 건너뛰기 > 소비 재시도
        }

        log.info("consumer: success >>> message: {}, headers: {}", ticketRequest.toString(), messageHeaders);
    }

    private boolean isValid() {
        /*
            대기 인원이 있는지 확인 후 현재 입장 가능한 지 redis에서 확인하는 조건문 추가
         */
        return true;
    }

    // 예매 요청 메시지 처리
    private void processTicketRequest(TicketRequest ticketRequest) {
        /*
            비즈니스 로직 처리
            - 메시지 소비
            - redis 대기 인원 업데이트
            ...
         */
    }
}
