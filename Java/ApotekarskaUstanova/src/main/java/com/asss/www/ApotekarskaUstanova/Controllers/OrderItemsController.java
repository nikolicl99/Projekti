package com.asss.www.ApotekarskaUstanova.Controllers;

import com.asss.www.ApotekarskaUstanova.Dto.OrderItemsDto;
import com.asss.www.ApotekarskaUstanova.Service.OrderItemsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/order-items")
public class OrderItemsController {

    @Autowired
    private OrderItemsService orderItemsService;

    @PostMapping
    public ResponseEntity<Void> addOrderItem(@RequestBody OrderItemsDto orderItemsDto) {
        orderItemsService.addOrderItem(orderItemsDto);
        return ResponseEntity.ok().build();
    }
}