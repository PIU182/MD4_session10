package com.example.pharmacy.inventory;
import jakarta.persistence.*;
@Entity @Table(name="medicines")
public class Medicine {
 @Id private Long id;
 @Column(nullable=false) private String name;
 @Column(nullable=false) private int stock;
 @Version private Long version;
 protected Medicine(){}
 public Medicine(Long id,String name,int stock){this.id=id;this.name=name;this.stock=stock;}
 public Long getId(){return id;}
 public String getName(){return name;}
 public int getStock(){return stock;}
 public void deduct(int quantity) {
   if(quantity<=0) throw new IllegalArgumentException("Quantity must be positive");
   if(stock<quantity) throw new IllegalStateException("Insufficient stock for medicine "+id);
   stock-=quantity;
 }
}
