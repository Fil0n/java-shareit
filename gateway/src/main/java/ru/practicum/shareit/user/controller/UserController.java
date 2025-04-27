package ru.practicum.shareit.user.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.client.UserClient;
import ru.practicum.shareit.user.model.UserDto;

@Controller
@RequiredArgsConstructor
@RequestMapping(path = "/users")
@Slf4j
public class UserController {
    private final UserClient userClient;

    @GetMapping("/{id}")
    public ResponseEntity<Object> findById(@PathVariable Long id) {
        log.info("Get user with id = {}", id);
        return userClient.findById(id);
    }

    @PostMapping
    public ResponseEntity<Object> create(@RequestBody @Valid UserDto user) {
        log.info("Create user {}", user);
        return userClient.createUser(user);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Object> update(@PathVariable Long id, @RequestBody UserDto user) {
        log.info("Update user with id = {}, {}", id, user);
        return userClient.updateUser(id, user);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> delete(@PathVariable long id) {
        log.info("Delete user with id = {}", id);
        return userClient.deleteUser(id);
    }
}