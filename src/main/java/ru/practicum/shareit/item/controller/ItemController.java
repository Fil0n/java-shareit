package ru.practicum.shareit.item.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import ru.practicum.shareit.comment.model.CommentDto;
import ru.practicum.shareit.comment.service.CommentService;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.item.model.ItemDto;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {
    private final ItemService itemService;
    private final CommentService commentService;

    @GetMapping
    public List<ItemDto> findAllOwned(@RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemService.getUserItems(userId);
    }

    @GetMapping("/{id}")
    public ItemDto findById(@PathVariable Long id,
                            @RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemService.getItemDto(id);
    }

    @GetMapping("/search")
    public List<ItemDto> search(@RequestParam String text,
                                @RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemService.searchItems(text);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ItemDto create(@RequestBody @Valid ItemDto item,
                          @RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemService.createItem(item, userId);
    }

    @PatchMapping("/{id}")
    public ItemDto update(@PathVariable Long id,
                          @RequestBody ItemDto item,
                          @RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemService.updateItem(id, item, userId);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto createComment(@PathVariable Long itemId, @RequestBody @Valid CommentDto commentDto,
                                    @RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemService.createItemComment(itemId, commentDto, userId);
    }

    @GetMapping("/{itemId}/comment")
    public List<CommentDto> getItemComments(@PathVariable Long itemId) {
        return commentService.getItemComments(itemId);
    }
}
