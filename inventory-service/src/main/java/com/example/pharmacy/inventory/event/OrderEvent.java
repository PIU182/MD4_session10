package com.example.pharmacy.inventory.event;
import java.time.Instant;
public record OrderEvent(String orderId, Long medicineId, int quantity, Instant timestamp) {}
