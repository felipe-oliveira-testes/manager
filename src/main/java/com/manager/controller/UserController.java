package com.manager.controller;

import com.manager.entity.User;
import com.manager.exception.EntityNotFound;
import com.manager.service.UserService;
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
@RequestMapping("/users")
public class UserController {
    @Autowired
    UserService userService;

    @GetMapping
    public ResponseEntity<List<User>> getUsers() {
        List<User> users = userService.getUsers();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable(required = true) Long id) {
        try {
            User user = userService.getUserById(id);
            return ResponseEntity.ok(user);
        } catch (EntityNotFound e) {
            return ResponseEntity
                    .notFound()
                    .build();
        }
    }

    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody(required = true) User user) {
        User userCreated = userService.createUser(user);
        return ResponseEntity.status(201)
                .body(userCreated);
    }

    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(@PathVariable(required = true) Long id,
                           @RequestBody(required = true) User user) {
        try {
            User userChanged = userService.updateUser(id, user);
            return ResponseEntity.ok(userChanged);
        } catch (EntityNotFound e) {
            return ResponseEntity
                    .notFound()
                    .build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Long> deleteUserById(@PathVariable(required = true) Long id) {
        userService.deleteUserById(id);
        return ResponseEntity.noContent().build();
    }
}
