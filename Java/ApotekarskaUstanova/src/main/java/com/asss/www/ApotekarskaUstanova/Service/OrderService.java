package com.asss.www.ApotekarskaUstanova.Service;

import com.asss.www.ApotekarskaUstanova.Dto.OrderDto;
import com.asss.www.ApotekarskaUstanova.Entity.Order;
import com.asss.www.ApotekarskaUstanova.Repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    public Integer createOrder(OrderDto orderDto) {
        Order order = new Order();
        order.setSupplierId(orderDto.getSupplierId());
        order.setSelectedDate(orderDto.getSelectedDate());
        order.setSelectedTime(orderDto.getSelectedTime());

        Order savedOrder = orderRepository.save(order);
        return savedOrder.getId(); // Vrati ID kreiranog order-a
    }
}