package com.example.oliveyoungbe.controller;

import com.example.oliveyoungbe.dto.TicketBooking;
import com.example.oliveyoungbe.dto.TicketRequest;
import com.example.oliveyoungbe.service.KafkaProducerService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/tickets")
@RequiredArgsConstructor
public class TicketController {

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
    public ResponseEntity<Map<String, String>> requestTicket(@RequestBody TicketRequest ticketRequest, HttpServletResponse response) throws ExecutionException, InterruptedException {
        String uuid = UUID.randomUUID().toString(); // 사용자 식별자
//        int maxAge = 60 * 60 * 24;
//        String cookieValue = String.format("uuid=%s; Max-Age=%d; Path=/; SameSite=None", uuid, maxAge);
//        response.setHeader("Set-Cookie", cookieValue);
//
//        ticketRequest.setUuid(uuid);
        Map<String, String> responseBody = new HashMap<>();
        responseBody.put("uuid", uuid);

        kafkaProducerService.sendRequestMessage(ticketRequest);
        return ResponseEntity.ok(responseBody);
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
    public ResponseEntity<String> bookingTicket(@RequestBody TicketBooking ticketbooking, @CookieValue(value="uuid", required = false) String uuid) throws ExecutionException, InterruptedException {
        //ticketbooking.setUuid(uuid);

        kafkaProducerService.sendBookingMessage(ticketbooking);
        return ResponseEntity.ok().build();
    }
}
