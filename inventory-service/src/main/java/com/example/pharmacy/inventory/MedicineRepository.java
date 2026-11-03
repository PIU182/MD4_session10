package com.example.pharmacy.inventory;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import jakarta.persistence.LockModeType;
import java.util.Optional;
public interface MedicineRepository extends JpaRepository<Medicine,Long> {
 @Lock(LockModeType.PESSIMISTIC_WRITE)
 @Query("select m from Medicine m where m.id=:id")
 Optional<Medicine> findLocked(@Param("id") Long id);
}
