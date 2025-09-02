package com.asss.www.ApotekarskaUstanova.Service;

import com.asss.www.ApotekarskaUstanova.Dto.*;
import com.asss.www.ApotekarskaUstanova.Entity.Order;
import com.asss.www.ApotekarskaUstanova.Entity.OrderItems;
import com.asss.www.ApotekarskaUstanova.Entity.Product;
import com.asss.www.ApotekarskaUstanova.Entity.Supplier;
import com.asss.www.ApotekarskaUstanova.Repository.OrderItemsRepository;
import com.asss.www.ApotekarskaUstanova.Repository.OrderRepository;
import com.asss.www.ApotekarskaUstanova.Repository.ProductRepository;
import com.asss.www.ApotekarskaUstanova.Repository.SupplierRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderItemsService {

    @Autowired
    private OrderItemsRepository orderItemsRepository;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    ProductRepository productRepository;
    @Autowired
    SupplierRepository supplierRepository;

    public void addOrderItem(OrderItemsDto orderItemsDto) {
        OrderItems orderItem = new OrderItems();
        orderItem.setOrderId(orderItemsDto.getOrderId());
        orderItem.setProductId(orderItemsDto.getProductId());
        orderItem.setQuantity(orderItemsDto.getQuantity());

        orderItemsRepository.save(orderItem);
    }

    public List<OrderItemsDto> getOrderItemsByOrderId(Integer orderId) {
        List<OrderItems> orderItems = orderItemsRepository.findDistinctByOrderId(orderId);

        return orderItems.stream()
                .map(orderItem -> {
                    OrderItemsDto dto = new OrderItemsDto();
                    dto.setId(orderItem.getId());
                    dto.setOrderId(orderItem.getOrderId());
                    dto.setProductId(orderItem.getProductId());
                    dto.setQuantity(orderItem.getQuantity());

                    // Uzmi podatke o proizvodu
                    Product product = productRepository.findById(orderItem.getProductId()).orElse(null);
                    if (product != null) {
                        ProductDto productDto = new ProductDto();
                        productDto.setId(product.getId());
                        productDto.setName(product.getName());
                        // Dodajte ostale atribute proizvoda po potrebi
                        dto.setProduct(productDto);
                    }

                    // Uzmi podatke o narudžbini i dobavljaču
                    Order order = orderRepository.findById(orderItem.getOrderId()).orElse(null);
                    if (order != null) {
                        OrderDto orderDto = new OrderDto();
                        orderDto.setId(order.getId());

                        // Uzmi ceo objekat dobavljača
                        Supplier supplier = supplierRepository.findById(order.getSupplierId()).orElse(null);
                        if (supplier != null) {
                            SupplierDto supplierDto = new SupplierDto();
                            supplierDto.setId(supplier.getId());
                            supplierDto.setName(supplier.getName());
                            // Postavite ostale atribute dobavljača po potrebi
                            orderDto.setSupplier(supplierDto);
                        }

                        orderDto.setSelectedDate(order.getSelectedDate());
                        orderDto.setSelectedTime(order.getSelectedTime());
                        orderDto.setAcquired(order.getAcquired());

                        dto.setOrder(orderDto);
                    }

                    return dto;
                })
                .collect(Collectors.toList());
    }
}