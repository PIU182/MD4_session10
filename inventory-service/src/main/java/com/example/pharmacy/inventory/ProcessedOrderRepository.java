package com.example.pharmacy.inventory;
import org.springframework.data.jpa.repository.JpaRepository;
public interface ProcessedOrderRepository extends JpaRepository<ProcessedOrder,String> {}
