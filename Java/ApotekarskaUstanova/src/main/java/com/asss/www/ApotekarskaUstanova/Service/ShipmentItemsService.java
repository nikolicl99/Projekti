package com.asss.www.ApotekarskaUstanova.Service;

import com.asss.www.ApotekarskaUstanova.Repository.ProductBatchRepository;
import com.asss.www.ApotekarskaUstanova.Repository.ShipmentItemsRepository;
import com.asss.www.ApotekarskaUstanova.Repository.ShipmentRepository;
import com.asss.www.ApotekarskaUstanova.Dto.*;
import com.asss.www.ApotekarskaUstanova.Entity.ProductBatch;
import com.asss.www.ApotekarskaUstanova.Entity.Shipment;
import com.asss.www.ApotekarskaUstanova.Entity.Shipment_Items;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ShipmentItemsService {

    @Autowired
    private ShipmentItemsRepository shipmentItemRepository;

    private final ShipmentRepository shipmentRepository;
    private final ProductBatchRepository productBatchRepository;
    private final ShipmentItemsRepository shipmentItemsRepository;

    public ShipmentItemsService(ShipmentRepository shipmentRepository, ProductBatchRepository productBatchRepository, ShipmentItemsRepository shipmentItemsRepository) {
        this.shipmentRepository = shipmentRepository;
        this.productBatchRepository = productBatchRepository;
        this.shipmentItemsRepository = shipmentItemsRepository;
    }

    public List<Shipment_ItemsDto> getShipmentItems(Long shipmentId) {
        List<Shipment_Items> items = shipmentItemRepository.findByShipmentId(shipmentId);
        return items.stream()
                .map(item -> {
                    Shipment_ItemsDto dto = new Shipment_ItemsDto();
                    dto.setId(item.getId());
                    dto.setQuantity(item.getQuantity());

                    // Postavite ShipmentDto
                    ShipmentDto shipmentDto = new ShipmentDto(item.getShipment());
                    dto.setShipment(shipmentDto);

                    // Postavite ProductBatchDto
                    ProductBatchDto productBatchDto = new ProductBatchDto(item.getProductBatch());
                    dto.setProductBatch(productBatchDto);

                    return dto;
                })
                .collect(Collectors.toList());
    }

    public Shipment_ItemsDto addShipmentItem(Shipment_ItemsDto shipmentItemsDto) {
//        Shipment shipment = shipmentRepository.findById(shipmentItemsDto.getId()).orElse(null);
//        ProductBatch productBatch = productBatchRepository.findById(shipmentItemsDto.getId()).orElse(null);
//
//        if (shipment == null || productBatch == null) {
//            return false;
//        }
//
//        Shipment_Items shipmentItem = new Shipment_Items();
//        shipmentItem.setShipment(shipment);
//        shipmentItem.setProductBatch(productBatch);
//        shipmentItem.setQuantity(shipmentItemsDto.getQuantity());
//
//        shipmentItemsRepository.save(shipmentItem);
//        return true;

        Shipment_Items shipmentItems = convertToEntity(shipmentItemsDto);

        Shipment_Items savedShipmentItems = shipmentItemsRepository.save(shipmentItems);

        return  convertToDto(savedShipmentItems);
    }

//    public List<Shipment_ItemsDto> getShipmentItemsByShipmentId(Long shipmentId) {
//        List<Shipment_Items> items = shipmentItemRepository.findByShipmentId(shipmentId);
//        return items.stream()
//                .map(item -> new Shipment_ItemsDto(item.getProductBatch().getName(), item.getQuantity()))
//                .collect(Collectors.toList());
//    }

    private Shipment_ItemsDto convertToDto(Shipment_Items shipmentItems) {
        Shipment_ItemsDto shipmentItemsDto = new Shipment_ItemsDto();
        shipmentItemsDto.setId(shipmentItems.getId());
        shipmentItemsDto.setQuantity(shipmentItems.getQuantity());

        // Mapiranje ProductBatch u ProductBatchDto
        ProductBatchDto productBatchDto = new ProductBatchDto();
        productBatchDto.setId(shipmentItems.getProductBatch().getId());
        productBatchDto.setBatchNumber(shipmentItems.getProductBatch().getBatchNumber());
        productBatchDto.setEan13(shipmentItems.getProductBatch().getEan13());
        productBatchDto.setExpirationDate(shipmentItems.getProductBatch().getExpirationDate());
        productBatchDto.setQuantityReceived(shipmentItems.getProductBatch().getQuantityReceived());
        productBatchDto.setQuantityRemaining(shipmentItems.getProductBatch().getQuantityRemaining());
        productBatchDto.setProduct_id(shipmentItems.getProductBatch().getProduct().getId());

        // Ručno inicijalizujemo ProductDto
        ProductDto productDto = new ProductDto();
        productDto.setId(shipmentItems.getProductBatch().getProduct().getId());
        productDto.setName(shipmentItems.getProductBatch().getProduct().getName()); // Pretpostavka da Product ima name polje
        productDto.setDosage(shipmentItems.getProductBatch().getProduct().getDosage()); // Pretpostavka da Product ima dosage polje
        // Dodaj ostala polja ako postoje

        // Postavi ProductDto u ProductBatchDto
        productBatchDto.setProduct(productDto);

        // Mapiranje Shipment u ShipmentDto
        ShipmentDto shipmentDto = new ShipmentDto();
        shipmentDto.setId(shipmentItems.getShipment().getId());
        // Dodaj ostala polja iz Shipment entiteta ako postoje

        // Postavi ShipmentDto u ProductBatchDto
        productBatchDto.setShipmentDto(shipmentDto);

        // Postavi ProductBatchDto i ShipmentDto u Shipment_ItemsDto
        shipmentItemsDto.setProductBatch(productBatchDto);
        shipmentItemsDto.setShipment(shipmentDto);

        return shipmentItemsDto;
    }

    private Shipment_Items convertToEntity(Shipment_ItemsDto shipmentItemsDto) {
        Shipment_Items shipmentItems = new Shipment_Items();

        // Provera obaveznih polja
        if (shipmentItemsDto.getProduct_id() == 0) {
            throw new RuntimeException("Product ID is required");
        }
        if (shipmentItemsDto.getShipment_id() == 0) {
            throw new RuntimeException("Shipment ID is required");
        }

        // Pronalaženje ProductBatch iz baze
        ProductBatch productBatch = productBatchRepository.findById(shipmentItemsDto.getProduct_id())
                .orElseThrow(() -> new RuntimeException("Product not found with ID: " + shipmentItemsDto.getProduct_id()));

        // Pronalaženje Shipment iz baze
        Shipment shipment = shipmentRepository.findById(shipmentItemsDto.getShipment_id())
                .orElseThrow(() -> new RuntimeException("Shipment not found with ID: " + shipmentItemsDto.getShipment_id()));

        // Mapiranje polja iz DTO u entitet
        shipmentItems.setId(shipmentItemsDto.getId());
        shipmentItems.setQuantity(shipmentItemsDto.getQuantity());
        shipmentItems.setProductBatch(productBatch);
        shipmentItems.setShipment(shipment);

        return shipmentItems;
    }
}
