package ru.practicum.shareit.item.storage;

import ru.practicum.shareit.item.model.Item;

import java.util.Collection;

public interface ItemStorage {
    Collection<Item> getAll();

    Item read(Long id);

    Item create(Item item);

    Item update(Item item);

    void delete(Long id);
}
