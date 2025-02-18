package com.example.oliveyoungbe.controller;

import com.example.oliveyoungbe.dto.TicketBooking;
import com.example.oliveyoungbe.dto.TicketRequest;
import com.example.oliveyoungbe.service.KafkaProducerService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<String> requestTicket(@RequestBody TicketRequest ticketRequest) {
        kafkaProducerService.sendRequestMessage(ticketRequest);
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
    public ResponseEntity<String> bookingTicket(@RequestBody TicketBooking ticketbooking) {
        kafkaProducerService.sendBookingMessage(ticketbooking);
        return ResponseEntity.ok().build();
    }
}
