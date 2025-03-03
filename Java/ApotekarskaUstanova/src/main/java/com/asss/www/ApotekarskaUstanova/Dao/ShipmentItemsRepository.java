package com.asss.www.ApotekarskaUstanova.Dao;

import com.asss.www.ApotekarskaUstanova.Entity.Shipment_Items;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ShipmentItemsRepository extends JpaRepository<Shipment_Items, Long> {
    List<Shipment_Items> findByShipmentId(Long shipmentId);
}
