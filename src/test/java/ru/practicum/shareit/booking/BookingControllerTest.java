package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.ShareItApp;
import ru.practicum.shareit.booking.controller.BookingController;
import ru.practicum.shareit.booking.model.BookingDto;
import ru.practicum.shareit.booking.model.BookingStatusType;
import ru.practicum.shareit.item.controller.ItemController;
import ru.practicum.shareit.item.model.ItemDto;
import ru.practicum.shareit.user.controller.UserController;
import ru.practicum.shareit.user.model.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = ShareItApp.class)
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class BookingControllerTest {
    static int userCount = 0;
    static int itemCount = 0;

    @Autowired
    private UserController userController;

    @Autowired
    private UserService userService;

    @Autowired
    private ItemController itemController;

    @Autowired
    private BookingController bookingController;

    @Test
    void bookingControllerCreatesBooking() {
        UserDto userDto = getUserDto(userCount);
        userDto = userController.create(userDto);

        UserDto bookerDto = getUserDto(userCount);
        bookerDto = userController.create(bookerDto);

        ItemDto itemDto = getItemDto(itemCount);
        itemDto = itemController.create(itemDto, userDto.getId());

        BookingDto bookingDto = getBookingDto(itemDto.getId(), bookerDto.getId());
        BookingDto createdBooking = bookingController.create(bookingDto, bookerDto.getId());

        assertNotNull(createdBooking.getId());
        assertEquals(bookingDto.getBooker().getId(), createdBooking.getBooker().getId());
    }

    @Test
    void bookingControllerUpdatesStatus() {
        UserDto userDto = getUserDto(userCount);
        userDto = userController.create(userDto);

        UserDto bookerDto = getUserDto(userCount);
        bookerDto = userController.create(bookerDto);

        ItemDto itemDto = getItemDto(itemCount);
        itemDto = itemController.create(itemDto, userDto.getId());

        BookingDto bookingDto = getBookingDto(itemDto.getId(), bookerDto.getId());
        BookingDto createdBooking = bookingController.create(bookingDto, bookerDto.getId());

        BookingDto updatedBooking = bookingController.updateStatus(createdBooking.getId(), userDto.getId(), true);
        assertEquals(BookingStatusType.APPROVED, updatedBooking.getStatus());
    }

    private UserDto getUserDto(int id) {
        userCount++;
        return UserDto.builder()
                .name("User" + id)
                .email("user" + id + "@mail.ru")
                .build();
    }

    private ItemDto getItemDto(int id) {
        itemCount++;
        return ItemDto.builder()
                .name("Item" + id)
                .description("Description" + id)
                .isAvailable(true)
                .build();
    }

    private BookingDto getBookingDto(Long itemId, Long bookerId) {
        return BookingDto.builder()
                .itemId(itemId)
                .booker(userService.getItemDto(bookerId))
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .build();
    }
}