package com.asss.www.ApotekarskaUstanova.Service;

import com.asss.www.ApotekarskaUstanova.Repository.*;
import com.asss.www.ApotekarskaUstanova.Dto.*;
import com.asss.www.ApotekarskaUstanova.Entity.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ProductBatchService {
    @Autowired
    private final ProductBatchRepository productBatchRepository;

    private final ProductRepository productRepository;
    private final SupplierRepository supplierRepository;
    private final ShipmentRepository shipmentRepository;
    private final LocationRepository locationRepository;

    @Autowired
    public ProductBatchService(ProductBatchRepository productBatchRepository,
                               ProductRepository productRepository, // Inject ProductRepository
                               SupplierRepository supplierRepository,
                               ShipmentRepository shipmentRepository,
                               LocationRepository locationRepository) {
        this.productBatchRepository = productBatchRepository;
        this.productRepository = productRepository;
        this.supplierRepository = supplierRepository;
        this.shipmentRepository = shipmentRepository;
        this.locationRepository = locationRepository;
    }

    // Existing methods...

    public List<ProductBatch> getAllBatches() {
        return productBatchRepository.findAll();
    }

    public Optional<ProductBatch> getBatchById(int id) {
        return productBatchRepository.findById(id);
    }

    public int getProductIdByBatchId(Long batchId) {
        return productBatchRepository.findById(batchId)
                .map(ProductBatch::getProduct)
                .orElseThrow(() -> new RuntimeException("Product ID nije pronađen za Batch ID: " + batchId)).getId();
    }

    public ProductBatch saveBatch(ProductBatchDto productBatchDto) {
        ProductBatch productBatch = convertToEntity(productBatchDto);
        return productBatchRepository.save(productBatch);
    }

    public int getNextBatchNumberForProduct(int productId) {
        Optional<Integer> maxBatchNumberOpt = productBatchRepository.findMaxBatchNumberByProductId(productId);

        if (maxBatchNumberOpt.isPresent()) {
            return maxBatchNumberOpt.get() + 1;
        } else {
            return 1;
        }
    }

    public void deleteBatch(int id) {
        productBatchRepository.deleteById(id);
    }

    public List<ProductBatchDto> getAllProductBatches() {
        List<ProductBatch> productBatches = productBatchRepository.findAll();
        return productBatches.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public ProductBatch getBatchByEan13(Long ean13) {
        return productBatchRepository.findByEan13(ean13)
                .orElseThrow(() -> new RuntimeException("Batch sa EAN13 kodom " + ean13 + " nije pronađen."));
    }

    public int getProductIdByEan13(Long ean13) {
        ProductBatch batch = getBatchByEan13(ean13);
        return batch.getProduct().getId();
    }

    public List<ProductBatchDto> getProductBatchesByProductId(int productId) {
        return productBatchRepository.findByProductId(productId).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public List<ProductBatchDto> findByEan13(String ean13) {
        return productBatchRepository.findByEan13(ean13).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public List<ProductBatchDto> findByProductName(String name) {
        return productBatchRepository.findByProductName(name).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public ProductBatchDto addProductBatch(ProductBatchDto productBatchDto) {
        // Convert DTO to Entity
        ProductBatch productBatch = convertToEntity(productBatchDto);

        // Save the entity to the database
        ProductBatch savedBatch = productBatchRepository.save(productBatch);

        // Convert the saved entity back to DTO
        return convertToDto(savedBatch);
    }

    private ProductBatchDto convertToDto(ProductBatch productBatch) {
        ProductBatchDto dto = new ProductBatchDto();
        dto.setId(productBatch.getId());
        dto.setBatchNumber(productBatch.getBatchNumber());
        dto.setEan13(productBatch.getEan13());
        dto.setExpirationDate(productBatch.getExpirationDate());
        dto.setQuantityReceived(productBatch.getQuantityReceived());
        dto.setQuantityRemaining(productBatch.getQuantityRemaining());
//        dto.setReceivedDate(productBatch.getShipment().getArrivalDate());

        // Set ProductDto
        ProductDto productDto = new ProductDto();
        productDto.setId(productBatch.getProduct().getId());
        productDto.setName(productBatch.getProduct().getName());
        productDto.setDescription(productBatch.getProduct().getDescription());
        dto.setProduct(productDto);

        // Set LocationDto
        LocationDto locationDto = new LocationDto();
        locationDto.setId(productBatch.getLocation().getLocationId());
        locationDto.setSection(productBatch.getLocation().getSection());
        locationDto.setShelf(productBatch.getLocation().getShelf());
        locationDto.setRow(productBatch.getLocation().getRow());
        locationDto.setDescription(productBatch.getLocation().getDescription());
        dto.setLocationDto(locationDto);

        // Set ShipmentDto
        ShipmentDto shipmentDto = new ShipmentDto();
        shipmentDto.setId(productBatch.getShipment().getId());
        shipmentDto.setArrivalDate(productBatch.getShipment().getArrivalDate());
        shipmentDto.setArrivalTime(productBatch.getShipment().getArrivalTime());

        // Set SupplierDto within ShipmentDto
        SupplierDto supplierDto = new SupplierDto();
        supplierDto.setId(productBatch.getShipment().getSupplier().getId());
        supplierDto.setName(productBatch.getShipment().getSupplier().getName());
        shipmentDto.setSupplier(supplierDto);

        // Set supplierName in ShipmentDto
        shipmentDto.setSupplierName(productBatch.getShipment().getSupplier().getName());

        // Set ShipmentDto in ProductBatchDto
        dto.setShipmentDto(shipmentDto);

        return dto;
    }

    private ProductBatch convertToEntity(ProductBatchDto productBatchDto) {
        ProductBatch productBatch = new ProductBatch();

        // Proverite da li su obavezni podaci prisutni
        if (productBatchDto.getProduct_id() == 0) {
            throw new RuntimeException("Product ID is required");
        }
        if (productBatchDto.getLocation_id() == 0) {
            throw new RuntimeException("Location ID is required");
        }
        if (productBatchDto.getShipment_id() == 0) {
            throw new RuntimeException("Shipment ID is required");
        }

        // Fetch the Product entity
        Product product = productRepository.findById(productBatchDto.getProduct_id())
                .orElseThrow(() -> new RuntimeException("Product not found with ID: " + productBatchDto.getProduct_id()));

        // Fetch the Location entity
        Location location = locationRepository.findById(productBatchDto.getLocation_id())
                .orElseThrow(() -> new RuntimeException("Location not found with ID: " + productBatchDto.getLocation_id()));

        // Fetch the Shipment entity
        Shipment shipment = shipmentRepository.findById(productBatchDto.getShipment_id())
                .orElseThrow(() -> new RuntimeException("Shipment not found with ID: " + productBatchDto.getShipment_id()));

        // Set the fields
        productBatch.setProduct(product);
        productBatch.setEan13(productBatchDto.getEan13());
        productBatch.setBatchNumber(productBatchDto.getBatchNumber());
        productBatch.setExpirationDate(productBatchDto.getExpirationDate());
        productBatch.setQuantityReceived(productBatchDto.getQuantityReceived());
        productBatch.setQuantityRemaining(productBatchDto.getQuantityRemaining());
        productBatch.setLocation(location);
        productBatch.setShipment(shipment);

        return productBatch;
    }
}