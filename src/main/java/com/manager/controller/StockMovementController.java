package com.manager.controller;

import com.manager.entity.StockMovementDTO;
import com.manager.exception.ChangeNotAllowed;
import com.manager.exception.EntityNotFound;
import com.manager.service.StockMovementService;
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
@RequestMapping("/stock-movements")
public class StockMovementController {

    @Autowired
    StockMovementService stockMovementService;

    @GetMapping
    public ResponseEntity<List<StockMovementDTO>> getAllStockMovements() {
        List<StockMovementDTO> stockMovementList = stockMovementService
                .getAllStockMovements();
        return ResponseEntity.ok(stockMovementList);
    }

    @GetMapping("/{id}")
    public ResponseEntity<StockMovementDTO> getStockMovementById(@PathVariable(required = true) Long id) {
        StockMovementDTO stockMovement;
        try {
            stockMovement = stockMovementService
                    .getStockMovementById(id);
        } catch (EntityNotFound e) {
            return ResponseEntity
                    .notFound()
                    .build();
        }
        return ResponseEntity.ok(stockMovement);
    }

    @PostMapping
    public ResponseEntity<?> createStockMovements(
            @RequestBody(required = true) StockMovementDTO dto) {
        StockMovementDTO dtoCreated = null;
        try {
            dtoCreated = stockMovementService.createStockMovement(dto);
        } catch (ChangeNotAllowed e) {
            return ResponseEntity
                    .badRequest()
                    .body(e.getMessage());
        }
        return ResponseEntity.status(201)
                .body(dtoCreated);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateStockMovements(
            @PathVariable(required = true) Long id,
            @RequestBody(required = true) StockMovementDTO dto) {
        StockMovementDTO dtoChanged;
        try {
            dtoChanged = stockMovementService.updateStockMovement(id, dto);
        } catch (EntityNotFound e) {
            return ResponseEntity
                    .notFound()
                    .build();
        } catch (ChangeNotAllowed e) {
            return ResponseEntity
                    .badRequest()
                    .body(e.getMessage());
        }
        return ResponseEntity.ok(dtoChanged);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteItemById(@PathVariable(required = true) Long id) {
        try {
            stockMovementService.deleteStockMovementById(id);
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
