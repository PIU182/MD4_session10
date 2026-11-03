package com.example.pharmacy.producer.event;
import java.time.Instant;
public record OrderEvent(String orderId, Long medicineId, int quantity, Instant timestamp) {}
