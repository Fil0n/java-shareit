package ru.practicum.shareit.item.service;

import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.comment.model.CommentDto;
import ru.practicum.shareit.comment.service.CommentService;
import ru.practicum.shareit.extention.ConditionsNotMetException;
import ru.practicum.shareit.extention.ExceptionMessages;
import ru.practicum.shareit.extention.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.ItemMapper;
import ru.practicum.shareit.item.model.ItemDto;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemService {
    private final UserService userService;
    private final ItemRepository itemRepository;
    private final CommentService commentService;
    private final BookingRepository bookingRepository;

    public ItemDto read(Long id) {
        return ItemMapper.toItemDto(getItem(id), commentService.getItemComments(id));
    }

    public Item getItem(Long id) {
        return exists(id);
    }

    public List<ItemDto> getUserItems(Long userId) {
        userService.exists(userId);
        return itemRepository.findAllByOwnerId(userId)
                .stream()
                .map(ItemMapper::toItemDto)
                .toList();
    }

    public ItemDto create(ItemDto itemDto, Long userId) {
        userService.exists(userId);
        return ItemMapper.toItemDto(itemRepository.saveAndFlush(ItemMapper.toItem(itemDto, userService.exists(userId))));
    }

    public ItemDto update(Long id, ItemDto itemDto, Long userId) {
        userIsOwner(id, userId);

        Item item = exists(id);
        if (itemDto.getName() != null) {
            item.setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null) {
            item.setDescription(itemDto.getDescription());
        }
        if (itemDto.getIsAvailable() != null) {
            item.setIsAvailable(itemDto.getIsAvailable());
        }

        return ItemMapper.toItemDto(itemRepository.saveAndFlush(item));
    }

    public void delete(Long itemId, Long userId) {
        userIsOwner(itemId, userId);
        itemRepository.deleteById(itemId);
    }

    public List<ItemDto> search(String text) {
        if (text == null || text.isBlank()) {
            return List.of();
        }
        return itemRepository.findAllBySearch(text).stream()
                .map(ItemMapper::toItemDto)
                .toList();
    }

    public Item exists(Long id) {
        if (id == null) {
            throw new ConditionsNotMetException(ExceptionMessages.NOT_FOUND_ITEM);
        }

        return itemRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format(ExceptionMessages.ITEM_NOT_FOUND_ERROR, id)));
    }

    public void userIsOwner(Long id, Long userId) {
        if (userId == null) {
            throw new ConditionsNotMetException(ExceptionMessages.NOT_FOUND_USER);
        }

        if (!exists(id).getOwner().getId().equals(userId)) {
            throw new ConditionsNotMetException("Пользователь не владелец предмета");
        }
    }

    public CommentDto createComment(Long itemId, CommentDto commentDto, Long userId) {
        Item item = exists(itemId);
        userService.exists(userId);

        bookingRepository.findByItemIdAndBookerIdAndEndBefore(itemId, userId, LocalDateTime.now())
                .orElseThrow(() -> new ValidationException(ExceptionMessages.NOT_WAS_RENT));


        return commentService.create(item, commentDto, userService.exists(userId));
    }
}
