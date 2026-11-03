package com.example.pharmacy.inventory;
import org.springframework.boot.*;
import org.springframework.context.annotation.*;
@Configuration
public class SeedData {
 @Bean CommandLineRunner seed(MedicineRepository repo){
  return args->{if(!repo.existsById(1L)) repo.save(new Medicine(1L,"Paracetamol",100));
                if(!repo.existsById(2L)) repo.save(new Medicine(2L,"Vitamin C",80));};
 }
}
