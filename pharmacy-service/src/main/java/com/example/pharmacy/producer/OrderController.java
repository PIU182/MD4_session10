package com.example.pharmacy.producer;
import com.example.pharmacy.producer.event.OrderEvent;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.*;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.*;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.TimeUnit;
@RestController
@RequestMapping("/api/v1/orders")
public class OrderController {
 private final KafkaTemplate<String,OrderEvent> kafka;
 public OrderController(KafkaTemplate<String,OrderEvent> kafka) { this.kafka=kafka; }
 public record SaleRequest(@NotNull Long medicineId,@Min(1) int quantity) {}
 @PostMapping("/checkout")
 public ResponseEntity<?> checkout(@Valid @RequestBody SaleRequest request) throws Exception {
   var event=new OrderEvent(UUID.randomUUID().toString(),request.medicineId(),request.quantity(),Instant.now());
   // Wait for broker acknowledgement; this is NOT confirmation of stock deduction.
   var result=kafka.send("medicine-stock-events",request.medicineId().toString(),event).get(10,TimeUnit.SECONDS);
   return ResponseEntity.accepted().body(Map.of(
      "message","Đã tiếp nhận thanh toán và gửi sự kiện Kafka; cập nhật kho đang xử lý",
      "orderId",event.orderId(),"partition",result.getRecordMetadata().partition(),
      "offset",result.getRecordMetadata().offset()));
 }
}
