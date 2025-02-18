package com.example.oliveyoungbe.dto;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class TicketRequest {
    private String eventId;
    private Timestamp timestamp;
}
