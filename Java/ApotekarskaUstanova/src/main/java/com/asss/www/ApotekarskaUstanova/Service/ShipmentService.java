package com.asss.www.ApotekarskaUstanova.Service;

import com.asss.www.ApotekarskaUstanova.Repository.ShipmentRepository;
import com.asss.www.ApotekarskaUstanova.Repository.SupplierRepository;
import com.asss.www.ApotekarskaUstanova.Dto.ShipmentDto;
import com.asss.www.ApotekarskaUstanova.Dto.SupplierDto;
import com.asss.www.ApotekarskaUstanova.Entity.Shipment;
import com.asss.www.ApotekarskaUstanova.Entity.Supplier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ShipmentService {

    @Autowired
    private ShipmentRepository shipmentRepository;

    @Autowired
    private SupplierRepository supplierRepository;

    public ShipmentDto getShipmentById(int id) {
        return shipmentRepository.findById(id)
                .map(this::convertToDto)
                .orElse(null);
    }

    public List<ShipmentDto> getShipmentsBySupplierId(Integer supplierId) {
        List<Shipment> shipments = shipmentRepository.findBySupplierId(supplierId);
        return shipments.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public ShipmentDto addShipment(ShipmentDto shipmentDto) {
        if (shipmentDto.getSupplierId() == 0) {
            throw new IllegalArgumentException("Supplier ID must not be null or invalid");
        }

        // Fetch the supplier from the repository using the supplierId
        Supplier supplier = supplierRepository.findById(shipmentDto.getSupplierId())
                .orElseThrow(() -> new RuntimeException("Supplier not found"));

        // Convert DTO to Entity
        Shipment shipment = new Shipment();
        shipment.setArrivalDate(shipmentDto.getArrivalDate());
        shipment.setArrivalTime(shipmentDto.getArrivalTime());
        shipment.setSupplier(supplier);

        // Save the shipment to the database
        Shipment savedShipment = shipmentRepository.save(shipment);
        shipmentRepository.flush(); // Force write to the database

        // Convert the saved entity back to DTO
        return convertToDto(savedShipment);
    }

    public List<ShipmentDto> getShipmentByDate(LocalDate date) {
        List<Shipment> shipments = shipmentRepository.findByArrivalDate(date);
        return shipments.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    // Helper method to convert Shipment entity to ShipmentDto
    private ShipmentDto convertToDto(Shipment shipment) {
        if (shipment == null) {
            throw new IllegalArgumentException("Shipment cannot be null.");
        }

        // Convert Supplier to SupplierDto
        SupplierDto supplierDto = null;
        if (shipment.getSupplier() != null) {
            supplierDto = new SupplierDto(shipment.getSupplier());
        }

        // Create and return the ShipmentDto
        return new ShipmentDto(
                shipment.getId(),
                shipment.getArrivalDate(),
                shipment.getArrivalTime(),
                shipment.getSupplier().getName(), // Supplier name
                shipment.getSupplier().getId(),   // Supplier ID
                supplierDto                      // SupplierDto
        );
    }
}