package com.asss.www.ApotekarskaUstanova.Service;

import com.asss.www.ApotekarskaUstanova.Dao.ProductRepository;
import com.asss.www.ApotekarskaUstanova.Dao.SalesItemRepository;
import com.asss.www.ApotekarskaUstanova.Dao.SalesRepository;
import com.asss.www.ApotekarskaUstanova.Dto.SalesItemDto;
import com.asss.www.ApotekarskaUstanova.Entity.Product;
import com.asss.www.ApotekarskaUstanova.Entity.Sales;
import com.asss.www.ApotekarskaUstanova.Entity.SalesItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SalesItemService {

    @Autowired
    private SalesItemRepository salesItemRepository;

    @Autowired
    private SalesRepository salesRepository;

    @Autowired
    private ProductRepository productRepository;

    public SalesItem saveSalesItem(SalesItemDto salesItemDto) {
        System.out.println("Sales ID koji tražim: " + salesItemDto.getSalesId());

        Sales sales = salesRepository.findById(salesItemDto.getSalesId())
                .orElseThrow(() -> new RuntimeException("Sales ID nije pronađen"));

        Product product = productRepository.findById(salesItemDto.getProductId())
                .orElseThrow(() -> new RuntimeException("Product ID nije pronađen"));

        SalesItem salesItem = new SalesItem(
                sales,
                product,
                salesItemDto.getReceiptType(),
                salesItemDto.getQuantity(),
                salesItemDto.getTotalPrice()
        );

        return salesItemRepository.save(salesItem);
    }

    public List<SalesItem> getAllSalesItems() {
        return salesItemRepository.findAll();
    }
}
