package com.asss.www.ApotekarskaUstanova.Service;

import com.asss.www.ApotekarskaUstanova.Dao.ShipmentItemsRepository;
import com.asss.www.ApotekarskaUstanova.Dto.Shipment_ItemsDto;
import com.asss.www.ApotekarskaUstanova.Entity.Shipment_Items;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ShipmentItemsService {

    @Autowired
    private ShipmentItemsRepository shipmentItemRepository;

    public List<Shipment_ItemsDto> getShipmentItems(Long shipmentId) {
        List<Shipment_Items> items = shipmentItemRepository.findByShipmentId(shipmentId);
        return items.stream()
                .map(item -> new Shipment_ItemsDto(item.getId(), item.getProduct().getName(), item.getQuantity(), item.getProduct().getPurchasePrice()))
                .collect(Collectors.toList());
    }



    public List<Shipment_ItemsDto> getShipmentItemsByShipmentId(Long shipmentId) {
        List<Shipment_Items> items = shipmentItemRepository.findByShipmentId(shipmentId);
        return items.stream()
                .map(item -> new Shipment_ItemsDto(item.getProduct().getName(), item.getQuantity()))
                .collect(Collectors.toList());
    }
}
