package ru.practicum.shareit.item.model;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.comment.model.CommentDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;

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

    public static ItemDto toItemDto(Item item, List<CommentDto> comments) {
        ItemDto itemOwnerDto = ItemDto.builder()
                .id(item.getId())
                .isAvailable(item.getIsAvailable())
                .name(item.getName())
                .description(item.getDescription())
                .build();

        itemOwnerDto.setComments(comments);
        return itemOwnerDto;
    }

    public static Item toItem(ItemDto itemDto, User owner) {
        return Item.builder()
                .id(itemDto.getId())
                .name(itemDto.getName())
                .description(itemDto.getDescription())
                .isAvailable(itemDto.getIsAvailable())
                .owner(owner)
                .build();
    }
}
