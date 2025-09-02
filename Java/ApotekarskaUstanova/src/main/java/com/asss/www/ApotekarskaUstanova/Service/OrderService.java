package com.asss.www.ApotekarskaUstanova.Service;

import com.asss.www.ApotekarskaUstanova.Dto.OrderDto;
import com.asss.www.ApotekarskaUstanova.Dto.SupplierDto;
import com.asss.www.ApotekarskaUstanova.Entity.Order;
import com.asss.www.ApotekarskaUstanova.Entity.Supplier;
import com.asss.www.ApotekarskaUstanova.Repository.OrderRepository;
import com.asss.www.ApotekarskaUstanova.Repository.SupplierRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private SupplierRepository supplierRepository;

    public Integer createOrder(OrderDto orderDto) {
        Order order = new Order();
        order.setSupplierId(orderDto.getSupplierId());
        order.setSelectedDate(orderDto.getSelectedDate());
        order.setSelectedTime(orderDto.getSelectedTime());
        order.setAcquired(orderDto.getAcquired());

        Order savedOrder = orderRepository.save(order);
        return savedOrder.getId(); // Vrati ID kreiranog order-a
    }

    public List<OrderDto> getNotReceivedOrders() {
        List<Order> orders = orderRepository.findByAcquiredFalse(); // acquired == 0

        return orders.stream()
                .map(order -> {
                    OrderDto dto = new OrderDto();
                    dto.setId(order.getId());
                    dto.setSupplierId(order.getSupplierId());

                    // Dodajte ime dobavljača
                    Supplier supplier = supplierRepository.findById(order.getSupplierId()).orElse(null);
                    if (supplier != null) {
                        SupplierDto supplierDto = new SupplierDto();
                        supplierDto.setId(supplier.getId());
                        supplierDto.setName(supplier.getName());
                        dto.setSupplier(supplierDto);
                    }

                    dto.setSelectedDate(order.getSelectedDate());
                    dto.setSelectedTime(order.getSelectedTime());
                    dto.setAcquired(order.getAcquired());
                    return dto;
                })
                .collect(Collectors.toList());
    }

    public boolean markOrderAsAcquired(int id) {
        Optional<Order> optionalOrder = orderRepository.findById(id);
        if (optionalOrder.isEmpty()) {
            return false;
        }

        Order order = optionalOrder.get();
        order.setAcquired(1);
        orderRepository.save(order);
        return true;
    }
}