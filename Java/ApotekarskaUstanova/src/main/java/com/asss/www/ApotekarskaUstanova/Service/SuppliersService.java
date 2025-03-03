package com.asss.www.ApotekarskaUstanova.Service;

import com.asss.www.ApotekarskaUstanova.Dao.AddressRepository;
import com.asss.www.ApotekarskaUstanova.Dao.ShipmentRepository;
import com.asss.www.ApotekarskaUstanova.Dao.SupplierRepository;
import com.asss.www.ApotekarskaUstanova.Entity.Address;
import com.asss.www.ApotekarskaUstanova.Entity.Shipment;
import com.asss.www.ApotekarskaUstanova.Entity.Supplier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

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

    public List<Supplier> getSuppliers() {
//        return supplierRepository.findAllByOrderByIdAsc();
        return supplierRepository.findAll();
    }

    public Integer getSupplierIdByName(String name) {
        Optional<Supplier> supplier = supplierRepository.findByName(name);
        return supplier.map(Supplier::getId).orElse(null);
    }

    public Supplier getSupplierById(int id) {
        return supplierRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Nije pronađen dobavljač sa ID-jem: " + id));
    }

    public List<Shipment> getRecentShipments(int supplierId) {
        return shipmentRepository.findTop5BySupplierIdOrderByArrivalTimeDesc(supplierId);
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
