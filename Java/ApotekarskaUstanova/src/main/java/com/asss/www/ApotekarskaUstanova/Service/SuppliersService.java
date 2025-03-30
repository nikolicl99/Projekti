package com.asss.www.ApotekarskaUstanova.Service;

import com.asss.www.ApotekarskaUstanova.Repository.AddressRepository;
import com.asss.www.ApotekarskaUstanova.Repository.ShipmentRepository;
import com.asss.www.ApotekarskaUstanova.Repository.SupplierRepository;
import com.asss.www.ApotekarskaUstanova.Dto.ShipmentDto;
import com.asss.www.ApotekarskaUstanova.Dto.SupplierDto;
import com.asss.www.ApotekarskaUstanova.Entity.Address;
import com.asss.www.ApotekarskaUstanova.Entity.Shipment;
import com.asss.www.ApotekarskaUstanova.Entity.Supplier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class SuppliersService {

    @Autowired
    private SupplierRepository supplierRepository;

    @Autowired
    private ShipmentRepository shipmentRepository;

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    public SuppliersService(SupplierRepository supplierRepository, ShipmentRepository shipmentRepository) {
        this.supplierRepository = supplierRepository;
        this.shipmentRepository = shipmentRepository;
    }

//    public List<Supplier> getSuppliers() {
////        return supplierRepository.findAllByOrderByIdAsc();
//        return supplierRepository.findAll();
//    }

    public Integer getSupplierIdByName(String name) {
        Optional<Supplier> supplier = supplierRepository.findByName(name);
        return supplier.map(Supplier::getId).orElse(null); // Return null if supplier is not found
    }

    public Supplier getSupplierById(int id) {
        return supplierRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Nije pronađen dobavljač sa ID-jem: " + id));
    }

    public List<Supplier> getSuppliers() {
        return supplierRepository.findAll();
    }

    // Metoda za HTTP odgovore (DTO)
    public List<SupplierDto> getAllSuppliers() {
        List<Supplier> suppliers = supplierRepository.findAll();
        return suppliers.stream()
                .map(SupplierDto::new)
                .collect(Collectors.toList());
    }

    public SupplierDto getSupplierById(Integer id) {
        Supplier supplier = supplierRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Dobavljač nije pronađen!"));
        return new SupplierDto(supplier);
    }

    public List<ShipmentDto> getAllShipmentsBySupplierId(int supplierId) {
        List<Shipment> shipments = shipmentRepository.findBySupplierId(supplierId);

        if (shipments.isEmpty()) {
            throw new RuntimeException("Nema isporuka za dobavljača sa ID-jem: " + supplierId);
        }

        // Mapiranje entiteta u DTO
        return shipments.stream()
                .map(shipment -> new ShipmentDto(
                        shipment.getId(),
                        shipment.getArrivalDate(),
                        shipment.getArrivalTime(),
                        shipment.getSupplier().getName()
                ))
                .collect(Collectors.toList());
    }

    // Metoda za dohvatanje poslednjih 5 isporuka za određenog dobavljača
    public List<ShipmentDto> getRecentShipments(int supplierId) {
        List<Shipment> shipments = shipmentRepository.findTop5BySupplierIdOrderByArrivalTimeDesc(supplierId);

        if (shipments.isEmpty()) {
            throw new RuntimeException("Nema isporuka za dobavljača sa ID-jem: " + supplierId);
        }

        // Mapiranje entiteta u DTO
        return shipments.stream()
                .map(shipment -> new ShipmentDto(
                        shipment.getId(),
                        shipment.getArrivalTime(),
                        shipment.getSupplier().getName()
                ))
                .collect(Collectors.toList());
    }

    public Supplier addSupplier(Supplier supplier) {
        // Proveravamo da li je adresa null
        Address existingAddress = null;

        if (supplier.getAddress() != null && supplier.getAddress().getId() != -1) {
            existingAddress = addressRepository.findById(supplier.getAddress().getId()).orElse(null);
        }

        if (existingAddress == null && supplier.getAddress() != null) {
            // Ako adresa ne postoji, sačuvaj novu
            existingAddress = addressRepository.save(supplier.getAddress());
        }

        // Kreiramo novog dobavljača i postavljamo mu vrednosti
        Supplier newSupplier = new Supplier();
        newSupplier.setName(supplier.getName());
        newSupplier.setEmail(supplier.getEmail());
        newSupplier.setPhone(supplier.getPhone());
        newSupplier.setAddress(existingAddress); // Postavljamo pronađenu ili novu adresu

        // Čuvamo u bazi i vraćamo sačuvanog dobavljača
        return supplierRepository.save(newSupplier);
    }


}
