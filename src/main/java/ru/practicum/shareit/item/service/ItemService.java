package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.extention.ConditionsNotMetException;
import ru.practicum.shareit.extention.ExceptionMessages;
import ru.practicum.shareit.extention.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.ItemMapper;
import ru.practicum.shareit.item.model.ItemDto;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemService {
    private final ItemStorage itemStorage;
    private final UserService userService;


    public ItemDto read(Long id) {
        return ItemMapper.toItemDto(itemStorage.read(id));
    }

    public List<ItemDto> getUserItems(Long userId) {
        userService.exists(userId);
        return itemStorage.getAll()
                .stream()
                .filter(item -> item.getOwner().equals(userId))
                .map(ItemMapper::toItemDto)
                .toList();
    }

    public Item getItem(Long id, Long userId) {
        userService.exists(userId);
        exists(id);
        return itemStorage.read(id);
    }

    public ItemDto create(ItemDto itemDto, Long userId) {
        userService.exists(userId);
        return ItemMapper.toItemDto(itemStorage.create(ItemMapper.toItem(itemDto, userId)));
    }

    public ItemDto update(Long id, ItemDto itemDto, Long userId) {
        userIsOwner(id, userId);

        Item item = itemStorage.read(id);
        if (itemDto.getName() != null) {
            item.setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null) {
            item.setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null) {
            item.setAvailable(itemDto.getAvailable());
        }

        return ItemMapper.toItemDto(itemStorage.update(item));
    }

    public void delete(Long itemId, Long userId) {
        userIsOwner(itemId, userId);
        itemStorage.delete(itemId);
    }

    public List<ItemDto> search(String text) {
        if (text == null || text.isBlank()) {
            return List.of();
        }
        return itemStorage.getAll().stream()
                .filter(Item::getAvailable)
                .filter(item -> item.getName().toLowerCase().contains(text.toLowerCase())
                        || item.getDescription().toLowerCase().contains(text.toLowerCase()))
                .map(ItemMapper::toItemDto)
                .toList();
    }


    public void exists(Long id) throws ConditionsNotMetException {
        Optional.ofNullable(itemStorage.read(id))
                .orElseThrow(() -> new NotFoundException(String.format(ExceptionMessages.ITEM_NOT_FOUND_ERROR, id)));
    }

    public void userIsOwner(Long id, Long userId) {
        userService.exists(userId);
        if (!itemStorage.read(id).getOwner().equals(userId)) {
            throw new ConditionsNotMetException("Пользователь не владелец предмета");
        }
    }
}
