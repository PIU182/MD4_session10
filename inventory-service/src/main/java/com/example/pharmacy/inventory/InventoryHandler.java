package com.example.pharmacy.inventory;
import com.example.pharmacy.inventory.event.OrderEvent;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
@Service
public class InventoryHandler {
 private final MedicineRepository medicines;
 private final ProcessedOrderRepository orders;
 public InventoryHandler(MedicineRepository medicines,ProcessedOrderRepository orders){
   this.medicines=medicines;this.orders=orders;
 }
 @Transactional
 public void apply(OrderEvent event) {
   if(orders.existsById(event.orderId())) return; // idempotent on replay
   var medicine=medicines.findLocked(event.medicineId())
       .orElseThrow(()->new IllegalArgumentException("Medicine not found: "+event.medicineId()));
   medicine.deduct(event.quantity());
   orders.saveAndFlush(new ProcessedOrder(event.orderId(),event.medicineId(),event.quantity()));
   medicines.save(medicine);
 }
}
