package ru.practicum.shareit.request.model;

import ru.practicum.shareit.item.model.ItemDto;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

public class RequestMapper {
    public static RequestDto toItemRequestDto(Request itemRequest) {
        return RequestDto.builder()
                .id(itemRequest.getId())
                .description(itemRequest.getDescription())
                .created(itemRequest.getCreated())
                .build();
    }

    public static RequestDto toItemRequestDto(Request itemRequest, List<ItemDto> items) {
        return RequestDto.builder()
                .id(itemRequest.getId())
                .description(itemRequest.getDescription())
                .created(itemRequest.getCreated())
                .items(items)
                .build();
    }

    public static Request toItemRequest(RequestDto itemRequestDto, User user) {
        return Request.builder()
                .description(itemRequestDto.getDescription())
                .created(LocalDateTime.now())
                .requester(user)
                .build();
    }
}
