package ru.practicum.shareit.item.model;

import org.springframework.stereotype.Component;

@Component
public class ItemMapper {
    public static ItemDto toItemDto(Item item) {
        return ItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .isAvailable(item.getIsAvailable())
                .build();
    }

    public static Item toItem(ItemDto itemDto, Long ownerId) {
        return Item.builder()
                .id(itemDto.getId())
                .name(itemDto.getName())
                .description(itemDto.getDescription())
                .isAvailable(itemDto.getIsAvailable())
                .owner(ownerId)
                .build();
    }
}
