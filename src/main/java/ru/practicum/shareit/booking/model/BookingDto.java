package ru.practicum.shareit.booking.model;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.item.model.ItemDto;
import ru.practicum.shareit.user.model.UserDto;

import java.time.LocalDateTime;

@Data
@Builder
public class BookingDto {
    private Long id;
    private ItemDto item;
    private Long itemId;
    private UserDto booker;
    private LocalDateTime start;
    private LocalDateTime end;
    private BookingStatusType status;
}
