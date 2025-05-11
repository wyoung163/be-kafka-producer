package com.example.oliveyoungbe.dto;

import jakarta.annotation.Nullable;
import lombok.Data;

@Data
public class TicketBookingDto {
    @Nullable
    private String uuid;
    private String eventId;
    private String timeSlot;
    private String timestamp;
}
