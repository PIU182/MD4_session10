package com.example.pharmacy.notification;
import com.example.pharmacy.notification.event.OrderEvent;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.slf4j.*;
@Component
public class NotificationConsumer {
 private static final Logger log=LoggerFactory.getLogger(NotificationConsumer.class);
 @KafkaListener(topics="medicine-stock-events",groupId="notification-group")
 public void onOrder(OrderEvent event) {
  log.info("Hóa đơn cho đơn hàng [{}] đã được gửi tới khách hàng (mô phỏng, chưa gửi email thực tế)",
     event.orderId());
 }
}
