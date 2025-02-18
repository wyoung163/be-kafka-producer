package com.example.oliveyoungbe.dto;

import jakarta.annotation.Nullable;
import lombok.Data;

import java.sql.Timestamp;

@Data
public class TicketRequest {
    @Nullable
    private String uuid;
    private String eventId;
    @Nullable
    private String region;
    private long timestamp;
}