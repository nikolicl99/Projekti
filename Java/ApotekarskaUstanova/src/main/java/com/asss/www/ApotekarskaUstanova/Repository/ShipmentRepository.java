package com.asss.www.ApotekarskaUstanova.Repository;

import com.asss.www.ApotekarskaUstanova.Entity.Shipment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface ShipmentRepository extends JpaRepository<Shipment, Integer> {
    Optional<Shipment> findById (Long id);
    List<Shipment> findBySupplierId(Integer supplierId); // Dohvatanje svih isporuka za određenog dobavljača
    List<Shipment> findTop5BySupplierIdOrderByArrivalTimeDesc(Integer supplierId); // Dohvatanje poslednjih 5 isporuka
    List<Shipment> findByArrivalDate(LocalDate arrivalDate);
}
