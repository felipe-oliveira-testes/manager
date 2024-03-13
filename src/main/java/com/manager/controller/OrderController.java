package com.manager.controller;

import com.manager.entity.OrderDTO;
import com.manager.exception.ChangeNotAllowed;
import com.manager.exception.EntityNotFound;
import com.manager.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/orders")
public class OrderController {

    @Autowired
    OrderService orderService;

    @GetMapping
    public ResponseEntity<List<OrderDTO>> getOrders() {
        List<OrderDTO> result = orderService.getOrders();
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderDTO> getOrderById(@PathVariable(required = true) Long id) {
        OrderDTO result = null;
        try {
            result = orderService.getOrderById(id);
        } catch (EntityNotFound e) {
            return ResponseEntity
                    .notFound()
                    .build();
        }
        return ResponseEntity.ok(result);
    }

    @PostMapping
    public ResponseEntity<?> createOrder(@RequestBody(required = true) OrderDTO dto) {
        OrderDTO dtoCreated = null;
        try {
            dtoCreated = orderService.createOrder(dto);
        } catch (ChangeNotAllowed e) {
            return ResponseEntity
                    .badRequest()
                    .body(e.getMessage());
        }
        return ResponseEntity.status(201)
                .body(dtoCreated);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateOrder(
            @PathVariable(required = true) Long id,
            @RequestBody(required = true) OrderDTO dto) {
        OrderDTO result = null;
        try {
            result = orderService.updateOrder(id, dto);
        } catch (EntityNotFound e) {
            return ResponseEntity
                    .notFound()
                    .build();
        } catch (ChangeNotAllowed e) {
            return ResponseEntity
                    .badRequest()
                    .body(e.getMessage());
        }
        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteOrderById(@PathVariable(required = true) Long id) {
        try {
            orderService.deleteOrder(id);
        } catch (EntityNotFound e) {
            return ResponseEntity
                    .notFound()
                    .build();
        } catch (ChangeNotAllowed e) {
            return ResponseEntity
                    .badRequest()
                    .body(e.getMessage());
        }
        return ResponseEntity.noContent().build();
    }
}
