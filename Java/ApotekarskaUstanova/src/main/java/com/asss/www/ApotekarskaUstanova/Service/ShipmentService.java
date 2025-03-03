package com.asss.www.ApotekarskaUstanova.Service;

import com.asss.www.ApotekarskaUstanova.Dao.ShipmentRepository;
import com.asss.www.ApotekarskaUstanova.Dto.ShipmentDto;
import com.asss.www.ApotekarskaUstanova.Dto.Shipment_ItemsDto;
import com.asss.www.ApotekarskaUstanova.Entity.Shipment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ShipmentService {
    @Autowired
    private ShipmentRepository shipmentRepository;

    public ShipmentDto getShipmentById(Long id) {
        Shipment shipment = shipmentRepository.findById(id).orElse(null);
        return (shipment != null) ? new ShipmentDto(shipment) : null;
    }

    public List<ShipmentDto> getShipmentsBySupplier(Long supplierId) {
        List<Shipment> items = shipmentRepository.findBySupplierId(supplierId);
        return items.stream()
                .map(item -> new ShipmentDto(item.getId(), item.getArrivalTime()))
                .collect(Collectors.toList());
    }

}
