package com.example.oliveyoungbe.controller;

import com.example.oliveyoungbe.dto.TicketBookingDto;
import com.example.oliveyoungbe.dto.TicketRequestDto;
import com.example.oliveyoungbe.service.KafkaProducerService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/tickets")
@RequiredArgsConstructor
public class TicketController {

    private static final Logger logger = LoggerFactory.getLogger(TicketController.class);
    private final KafkaProducerService kafkaProducerService;
    /*
        1. 예약 요청 API
        [POST] /tickets/request

        ** Request Body Example
        {
            "eventId": "concert-456",
            "timestamp": "2025-02-13T12:00:00Z"
        }

        ** Response Status Code
          - success: 200
          - exception:
     */
    @PostMapping("/request")
    public ResponseEntity<String> requestTicket(@RequestBody TicketRequestDto ticketRequestDto, HttpServletResponse response) throws ExecutionException, InterruptedException {
        String uuid = UUID.randomUUID().toString(); // 사용자 식별자
        Cookie cookie = new Cookie("uuid", uuid);
        cookie.setHttpOnly(true);
        cookie.setMaxAge(60 * 60 * 24);
        cookie.setPath("/");
        response.addCookie(cookie);

        ticketRequestDto.setUuid(uuid);

        logger.info("Request received for ticket request: eventId={}, timestamp={}, uuid={}",
                ticketRequestDto.getEventId(), ticketRequestDto.getTimestamp(), uuid);

        kafkaProducerService.sendRequestMessage(ticketRequestDto);

        logger.info("Ticket request successfully sent to kafka cluster with uuid={}", uuid);

        return ResponseEntity.ok().build();
    }

    /*
        1. 시간 선택 및 예약 완료 API
        [POST] /tickets/booking

        ** Request Body Example
        {
            "eventId": "concert-456",
            "timeSlot": "10:00"
            "timestamp": "2025-02-13T12:00:00Z"
        }

        ** Response Status Code
          - success: 200
          - exception:
     */
    @PostMapping("/booking")
    public ResponseEntity<String> bookingTicket(@RequestBody TicketBookingDto ticketBookingDto, @CookieValue(value="uuid", required = true) String uuid) throws ExecutionException, InterruptedException {
        ticketBookingDto.setUuid(uuid);

        logger.info("Request received for ticket booking: eventId={}, timeslot={}, timestamp={}, uuid={}",
                ticketBookingDto.getEventId(), ticketBookingDto.getTimeSlot(), ticketBookingDto.getTimestamp(), uuid);

        kafkaProducerService.sendBookingMessage(ticketBookingDto);

        logger.info("Booking request successfully sent to kafka cluster for uuid={}", uuid);

        return ResponseEntity.ok().build();
    }
}
