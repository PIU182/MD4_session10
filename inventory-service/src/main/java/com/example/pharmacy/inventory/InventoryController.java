package com.example.pharmacy.inventory;
import org.springframework.web.bind.annotation.*;
import java.util.*;
@RestController @RequestMapping("/api/v1/medicines")
public class InventoryController {
 private final MedicineRepository repo;
 public InventoryController(MedicineRepository repo){this.repo=repo;}
 @GetMapping public List<Medicine> all(){return repo.findAll();}
 @GetMapping("/{id}") public Medicine one(@PathVariable Long id){
  return repo.findById(id).orElseThrow(()->new org.springframework.web.server.ResponseStatusException(
      org.springframework.http.HttpStatus.NOT_FOUND,"Medicine not found"));
 }
}
