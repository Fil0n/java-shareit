package ru.practicum.shareit.booking.model;

import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.ItemMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.model.UserMapper;

public class BookingMapper {
    public static Booking toBooking(BookingDto bookingDto, Item item, User user) {
        return Booking.builder()
                .end(bookingDto.getEnd())
                .start(bookingDto.getStart())
                .status(BookingStatusType.WAITING)
                .item(item)
                .booker(user)
                .build();
    }

    public static BookingDto toBookingDto(Booking booking) {
        return BookingDto.builder()
                .id(booking.getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .status(booking.getStatus())
                .booker(UserMapper.toUserDto(booking.getBooker()))
                .item(ItemMapper.toItemDto(booking.getItem()))
                .build();
    }
}
