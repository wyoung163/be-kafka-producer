package com.example.oliveyoungbe.dto;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class TicketBooking {
    private String eventId;
    private String timeSlot;
    private Timestamp timestamp;
}
