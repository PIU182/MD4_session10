package com.example.pharmacy.producer;
import com.example.pharmacy.producer.event.OrderEvent;
import org.springframework.context.annotation.*;
import org.springframework.kafka.core.*;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import java.util.*;
@Configuration
public class KafkaProducerConfig {
 @Bean
 ProducerFactory<String,OrderEvent> producerFactory(
     @org.springframework.beans.factory.annotation.Value("${spring.kafka.bootstrap-servers}") String bootstrap) {
   Map<String,Object> config=new HashMap<>();
   config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,bootstrap);
   config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,StringSerializer.class);
   config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,JsonSerializer.class);
   config.put(ProducerConfig.ACKS_CONFIG,"all");
   config.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG,true);
   return new DefaultKafkaProducerFactory<>(config);
 }
 @Bean KafkaTemplate<String,OrderEvent> kafkaTemplate(ProducerFactory<String,OrderEvent> factory) {
   return new KafkaTemplate<>(factory);
 }
}
