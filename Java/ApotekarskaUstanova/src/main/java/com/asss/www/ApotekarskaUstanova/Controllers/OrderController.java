package com.asss.www.ApotekarskaUstanova.Controllers;

import com.asss.www.ApotekarskaUstanova.Dto.OrderDto;
import com.asss.www.ApotekarskaUstanova.Entity.Order;
import com.asss.www.ApotekarskaUstanova.Service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @PostMapping
    public ResponseEntity<Integer> createOrder(@RequestBody OrderDto orderDto) {
        Integer orderId = orderService.createOrder(orderDto);
        return ResponseEntity.ok(orderId);
    }

    @GetMapping("/not-received")
    public ResponseEntity<List<OrderDto>> getNotReceivedOrders() {
        List<OrderDto> orders = orderService.getNotReceivedOrders();
        return ResponseEntity.ok(orders);
    }

    @PutMapping("/orders/mark-acquired/{id}")
    public ResponseEntity<?> markOrderAsAcquired(@PathVariable int id) {
        boolean updated = orderService.markOrderAsAcquired(id);
        if (!updated) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok().build();
    }
}