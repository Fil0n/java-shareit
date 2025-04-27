package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatusType;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.comment.model.CommentDto;
import ru.practicum.shareit.comment.service.CommentService;
import ru.practicum.shareit.extention.ConditionsNotMetException;
import ru.practicum.shareit.extention.ExceptionMessages;
import ru.practicum.shareit.extention.NotFoundException;
import ru.practicum.shareit.extention.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.ItemMapper;
import ru.practicum.shareit.item.model.ItemDto;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.request.repository.RequestRepository;
import ru.practicum.shareit.user.model.User;
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
    private final RequestRepository requestRepository;

    public ItemDto getItemDto(Long id) {
        return ItemMapper.toItemDto(getItem(id), commentService.getItemComments(id));
    }

    public List<ItemDto> getUserItems(Long userId) {
        userService.getUser(userId);
        return itemRepository.findAllByOwnerId(userId)
                .stream()
                .map(ItemMapper::toItemDto)
                .toList();
    }

    public ItemDto createItem(ItemDto itemDto, Long userId) {
        userService.getUser(userId);
        Item item = ItemMapper.toItem(itemDto, userService.getUser(userId));
        if (itemDto.getRequestId() != null) {
            Request request = requestRepository.findById(itemDto.getRequestId()).orElseThrow(() -> new NotFoundException("Запрос не найден"));
            item.setRequest(request);
        }
        return ItemMapper.toItemDto(itemRepository.saveAndFlush(item));
    }

    public ItemDto updateItem(Long id, ItemDto itemDto, Long userId) {
        userIsOwner(id, userId);

        Item item = getItem(id);
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

    public List<ItemDto> searchItems(String text) {
        if (text == null || text.isBlank()) {
            return List.of();
        }
        return itemRepository.findAllBySearch(text).stream()
                .map(ItemMapper::toItemDto)
                .toList();
    }

    public Item getItem(Long id) {
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

        if (!getItem(id).getOwner().getId().equals(userId)) {
            throw new ConditionsNotMetException("Пользователь не владелец предмета");
        }
    }

    public CommentDto createItemComment(Long itemId, CommentDto commentDto, Long userId) {
        Item item = getItem(itemId);
        User user = userService.getUser(userId);

        Booking booking = bookingRepository.findByItemIdAndBookerIdAndEndBefore(itemId, userId, LocalDateTime.now())
                .orElseThrow(() -> new ValidationException(ExceptionMessages.NOT_WAS_RENT));

        if (!booking.getStatus().equals(BookingStatusType.APPROVED)) {
            throw new ValidationException(ExceptionMessages.NOT_WAS_RENT);
        }

        return commentService.createComment(item, commentDto, user);
    }
}
