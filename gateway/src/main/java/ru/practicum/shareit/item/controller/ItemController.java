package ru.practicum.shareit.item.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.comment.model.CommentDto;
import ru.practicum.shareit.item.client.ItemClient;
import ru.practicum.shareit.item.model.ItemDto;

@Controller
@RequiredArgsConstructor
@RequestMapping("/items")
@Slf4j
public class ItemController {
    private final ItemClient itemClient;

    @GetMapping
    public ResponseEntity<Object> findAllOwned(@RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Get items owned by user with userId = {}", userId);
        return itemClient.findAllOwned(userId);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> findById(@PathVariable Long id,
                                           @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Get item by id = {},userId = {}", id, userId);
        return itemClient.findById(id, userId);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> search(@RequestParam String text,
                                         @RequestHeader("X-Sharer-User-Id") Long userId) {

        log.info("Search for items by text = {},userId = {}", text, userId);
        return itemClient.search(text, userId);
    }

    @PostMapping
    public ResponseEntity<Object> create(@RequestBody @Valid ItemDto item,
                                         @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Create new item = {},userId = {}", item, userId);
        return itemClient.createItem(item, userId);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Object> update(@PathVariable Long id,
                                         @RequestBody ItemDto item,
                                         @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Update item with id = {}, {},userId = {}", id, item, userId);
        return itemClient.updateItem(id, item, userId);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> createComment(@PathVariable Long itemId, @RequestBody @Valid CommentDto commentDto,
                                                @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Create comment for item with id = {}, {},userId = {}", itemId, commentDto, userId);
        return itemClient.createComment(itemId, commentDto, userId);
    }
}