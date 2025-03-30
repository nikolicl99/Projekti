package com.asss.www.ApotekarskaUstanova.Service;

import com.asss.www.ApotekarskaUstanova.Dto.OrderItemsDto;
import com.asss.www.ApotekarskaUstanova.Entity.OrderItems;
import com.asss.www.ApotekarskaUstanova.Repository.OrderItemsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OrderItemsService {

    @Autowired
    private OrderItemsRepository orderItemsRepository;

    public void addOrderItem(OrderItemsDto orderItemsDto) {
        OrderItems orderItem = new OrderItems();
        orderItem.setOrderId(orderItemsDto.getOrderId());
        orderItem.setProductId(orderItemsDto.getProductId());
        orderItem.setQuantity(orderItemsDto.getQuantity());

        orderItemsRepository.save(orderItem);
    }
}