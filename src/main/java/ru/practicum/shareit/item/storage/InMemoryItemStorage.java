package ru.practicum.shareit.item.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.model.Item;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class InMemoryItemStorage implements ItemStorage {
    private final Map<Long, Item> itemStorage = new HashMap<>();
    private Long id = 1L;

    @Override
    public Collection<Item> getAll() {
        return itemStorage.values();
    }

    @Override
    public Item read(Long id) {
        return itemStorage.get(id);
    }

    @Override
    public Item create(Item item) {
        item.setId(++id);
        itemStorage.put(item.getId(), item);
        return item;
    }

    @Override
    public Item update(Item item) {
        itemStorage.put(item.getId(), item);
        return item;
    }

    @Override
    public void delete(Long id) {
        itemStorage.remove(id);
    }
}
