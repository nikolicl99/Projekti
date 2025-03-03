package com.asss.www.ApotekarskaUstanova.Dao;

import com.asss.www.ApotekarskaUstanova.Entity.Shipment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface ShipmentRepository extends JpaRepository<Shipment, Integer> {
    List<Shipment> findTop5BySupplierIdOrderByArrivalTimeDesc(int supplierId);
    Optional<Shipment> findById (Long id);
    List<Shipment> findBySupplierId(Long supplierId);
}
