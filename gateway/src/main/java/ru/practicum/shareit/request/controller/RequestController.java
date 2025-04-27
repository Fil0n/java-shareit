package ru.practicum.shareit.request.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.client.RequestClient;
import ru.practicum.shareit.request.model.RequestDto;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/requests")
@Slf4j
public class RequestController {

    private final RequestClient requestClient;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Object> create(@RequestBody @Valid RequestDto itemRequest,
                                         @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Create request {}, userId={}", itemRequest, userId);
        return requestClient.createItemRequest(itemRequest, userId);
    }

    @GetMapping
    public ResponseEntity<Object> findAllOwn(@RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Find all own requests for userId={}", userId);
        return requestClient.findAllOwn(userId);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> findById(@PathVariable Long id,
                                           @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Find request by id {}, userId={}", id, userId);
        return requestClient.findById(id, userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> findAll(@RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Find all not own requests for userId={}", userId);
        return requestClient.findAll(userId);
    }
}
