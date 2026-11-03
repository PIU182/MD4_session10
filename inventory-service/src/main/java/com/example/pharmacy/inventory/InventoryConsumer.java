package com.example.pharmacy.inventory;
import com.example.pharmacy.inventory.event.OrderEvent;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.slf4j.*;
@Component
public class InventoryConsumer {
 private static final Logger log=LoggerFactory.getLogger(InventoryConsumer.class);
 private final InventoryHandler handler;
 public InventoryConsumer(InventoryHandler handler){this.handler=handler;}
 @KafkaListener(topics="medicine-stock-events",groupId="inventory-group")
 public void onOrder(OrderEvent event){
   handler.apply(event);
   log.info("Inventory updated: orderId={}, medicineId={}, quantity={}",
     event.orderId(),event.medicineId(),event.quantity());
 }
}
