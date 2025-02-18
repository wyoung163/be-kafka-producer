package com.example.oliveyoungbe.service;

import com.example.oliveyoungbe.dto.TicketRequest;
import com.example.oliveyoungbe.dto.TicketBooking;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class KafkaConsumerService {

    private final StringRedisTemplate redisTemplate;
    private static final String WAITING_LIST_KEY = "waiting_list";
    private static final String ENTER_LIST_KEY = "enter_list";
    private static final String BOOKING_LIST_KEY = "booking_list";
    private static final int MAX_CAPACITY = 100;

    /**
     * 예매 요청 메시지 소비
     */
    @KafkaListener(topics = "${kafka.topic.typeRequest}", groupId = "${spring.kafka.consumer.group-id}")
    public void consumeTicketRequest(@Payload TicketRequest ticketRequest,
                                     @Headers MessageHeaders messageHeaders,
                                     Acknowledgment acknowledgment) {

        if (isValid()) {
            processTicketRequest(ticketRequest);
            acknowledgment.acknowledge();
            System.out.println("예매 요청 성공: "+ ticketRequest.getUuid());
        } else {
            System.out.println("예매 요청 실패");
        }
    }

    /**
     * 예매 완료 메시지 소비
     */
    @KafkaListener(topics = "${kafka.topic.typeBooking}", groupId = "${spring.kafka.consumer.group-id}")
    public void consumeTicketBooking(@Payload TicketBooking ticketBooking,
                                     @Headers MessageHeaders messageHeaders,
                                     Acknowledgment acknowledgment) {

        processTicketBooking(ticketBooking);
        acknowledgment.acknowledge();
        System.out.println("예매 완료 : " + ticketBooking.getUuid());
    }

    /**
     * 현재 입장 가능 여부 확인
     */
    private boolean isValid() {
        ZSetOperations<String, String> zSetOperations = redisTemplate.opsForZSet();
        Long waitingSize = Optional.ofNullable(zSetOperations.zCard(WAITING_LIST_KEY)).orElse(0L);

        if (waitingSize < MAX_CAPACITY) {
            System.out.println("입장 가능: 현재 대기 인원 " + waitingSize + "명");
            return true;
        } else {
            System.out.println("입장 불가: 대기 인원 초과 (" + waitingSize + "/" + MAX_CAPACITY + ")");
            return false;
        }
    }

    /**
     * 예매 요청 처리 (대기열 관리)
     */
    private void processTicketRequest(TicketRequest ticketRequest) {
        ZSetOperations<String, String> zSetOperations = redisTemplate.opsForZSet();
        String uuid = ticketRequest.getUuid();
        long timestamp = System.currentTimeMillis();

        // UUID 기반 ZSet 추가
        zSetOperations.add(WAITING_LIST_KEY, uuid, timestamp);
        System.out.println("대기열 추가: UUID=" + uuid + ", timestamp=" + timestamp);

        // 현재 대기 순번 조회
        Set<String> waitingUsers = zSetOperations.range(WAITING_LIST_KEY, 0, -1);
        if (waitingUsers != null) {
            System.out.println("현재 대기열: " + waitingUsers);
        }

        // 입장 가능하면 대기열에서 제거 후 입장 목록에 추가
        if (isValid()) {
            zSetOperations.remove(WAITING_LIST_KEY, uuid);
            redisTemplate.opsForSet().add(ENTER_LIST_KEY, uuid);
            System.out.println("입장: " + uuid);
        }
    }

    /**
     * 예매 완료 처리 (예약 확정)
     */
    private void processTicketBooking(TicketBooking ticketBooking) {
        ZSetOperations<String, String> zSetOperations = redisTemplate.opsForZSet();
        String uuid = ticketBooking.getUuid();

        // 예매 완료된 사용자를 대기열에서 제거
        zSetOperations.remove(WAITING_LIST_KEY, uuid);
        redisTemplate.opsForSet().remove(ENTER_LIST_KEY, uuid);

        // 예매 완료된 정보를 Booking List에 추가 (RDS or Redis)
        redisTemplate.opsForHash().put(BOOKING_LIST_KEY, uuid, ticketBooking.toString());

        System.out.println("예매 완료:" + uuid);
    }
}
