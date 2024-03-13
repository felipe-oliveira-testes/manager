package com.manager.controller;

import com.manager.entity.ItemDTO;
import com.manager.exception.EntityNotFound;
import com.manager.service.ItemService;
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
@RequestMapping("/items")
public class ItemController {

    @Autowired
    ItemService itemService;

    @GetMapping
    public ResponseEntity<List<ItemDTO>> getItems() {
        List<ItemDTO> items = itemService.getItems();
        return ResponseEntity.ok(items);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ItemDTO> getItemById(@PathVariable(required = true) Long id) {
        try {
            ItemDTO item = itemService.getItemById(id);
            return ResponseEntity.ok(item);
        } catch (EntityNotFound e) {
            return ResponseEntity
                    .notFound()
                    .build();
        }
    }

    @PostMapping
    public ResponseEntity<ItemDTO> createItem(@RequestBody(required = true) ItemDTO dto) {
        ItemDTO itemCreated = itemService.createItem(dto);
        return ResponseEntity.status(201)
                .body(itemCreated);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ItemDTO> updateItem(@PathVariable(required = true) Long id,
                                           @RequestBody(required = true) ItemDTO dto) {
        try {
            ItemDTO itemChanged = itemService.updateItem(id, dto);
            return ResponseEntity.ok(itemChanged);
        } catch (EntityNotFound e) {
            return ResponseEntity
                    .notFound()
                    .build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteItemById(@PathVariable(required = true) Long id) {
        itemService.deleteItemById(id);
        return ResponseEntity.noContent().build();
    }
}
