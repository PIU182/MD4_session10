package com.example.pharmacy.inventory;
import jakarta.persistence.*;
import java.time.Instant;
@Entity @Table(name="processed_orders")
public class ProcessedOrder {
 @Id private String orderId;
 @Column(nullable=false) private Long medicineId;
 @Column(nullable=false) private int quantity;
 @Column(nullable=false) private Instant processedAt;
 protected ProcessedOrder(){}
 public ProcessedOrder(String orderId,Long medicineId,int quantity) {
  this.orderId=orderId;this.medicineId=medicineId;this.quantity=quantity;this.processedAt=Instant.now();
 }
}
