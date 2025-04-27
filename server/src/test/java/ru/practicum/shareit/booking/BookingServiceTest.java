package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.config.StateMachineFactory;
import ru.practicum.shareit.booking.model.*;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.extention.ConditionsNotMetException;
import ru.practicum.shareit.extention.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceTest {

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private UserService userService;

    @Mock
    private ItemService itemService;

    @Mock
    private StateMachineFactory<BookingStatusType, BookingEvent> stateMachineFactory;

    @Mock
    private StateMachine<BookingStatusType, BookingEvent> stateMachine;

    @InjectMocks
    private BookingService bookingService;

    private User owner;
    private User booker;
    private Item item;
    private Booking booking;
    private BookingDto bookingDto;

    @BeforeEach
    void setUp() {
        owner = User.builder().id(1L).name("Owner").email("owner@email.com").build();
        booker = User.builder().id(2L).name("Booker").email("booker@email.com").build();
        item = Item.builder()
                .id(1L)
                .name("Item")
                .description("Description")
                .isAvailable(true)
                .owner(owner)
                .build();

        booking = Booking.builder()
                .id(1L)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .item(item)
                .booker(booker)
                .status(BookingStatusType.WAITING)
                .build();

        bookingDto = BookingDto.builder()
                .id(1L)
                .itemId(item.getId())
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .status(BookingStatusType.WAITING)
                .build();
    }

    @Test
    void getBookingDto_whenUserIsOwner_thenReturnBooking() {
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));

        BookingDto result = bookingService.getBookingDto(1L, 1L);

        assertNotNull(result);
        assertEquals(booking.getId(), result.getId());
        assertEquals(item.getId(), result.getItem().getId());
    }

    @Test
    void getBookingDto_whenUserIsBooker_thenReturnBooking() {
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));

        BookingDto result = bookingService.getBookingDto(1L, 2L);

        assertNotNull(result);
        assertEquals(booking.getId(), result.getId());
        assertEquals(booker.getId(), result.getBooker().getId());
    }

    @Test
    void getBookingDto_whenUserNotRelated_thenThrowException() {
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));

        assertThrows(ConditionsNotMetException.class,
                () -> bookingService.getBookingDto(1L, 3L));
    }

    @Test
    void createBooking_whenValid_thenReturnCreatedBooking() {
        when(userService.getUser(anyLong())).thenReturn(booker);
        when(itemService.getItem(anyLong())).thenReturn(item);
        when(bookingRepository.findByItemIdAndEndIsAfterAndStartIsBefore(anyLong(), any(), any()))
                .thenReturn(Optional.empty());
        when(bookingRepository.saveAndFlush(any(Booking.class))).thenReturn(booking);

        BookingDto result = bookingService.createBooking(bookingDto, 2L);

        assertNotNull(result);
        assertEquals(booking.getId(), result.getId());
        verify(bookingRepository).saveAndFlush(any(Booking.class));
    }

    @Test
    void createBooking_whenItemNotAvailable_thenThrowException() {
        item.setIsAvailable(false);
        when(userService.getUser(anyLong())).thenReturn(booker);
        when(itemService.getItem(anyLong())).thenReturn(item);

        assertThrows(ValidationException.class,
                () -> bookingService.createBooking(bookingDto, 2L));
    }

    @Test
    void createBooking_whenOwnerBooksOwnItem_thenThrowException() {
        when(userService.getUser(anyLong())).thenReturn(owner);
        when(itemService.getItem(anyLong())).thenReturn(item);

        assertThrows(ValidationException.class,
                () -> bookingService.createBooking(bookingDto, 1L));
    }

    @Test
    void createBooking_whenItemAlreadyBooked_thenThrowException() {
        when(userService.getUser(anyLong())).thenReturn(booker);
        when(itemService.getItem(anyLong())).thenReturn(item);
        when(bookingRepository.findByItemIdAndEndIsAfterAndStartIsBefore(anyLong(), any(), any()))
                .thenReturn(Optional.of(booking));

        assertThrows(ConditionsNotMetException.class,
                () -> bookingService.createBooking(bookingDto, 2L));
    }

    @Test
    void readByBookerAndState_whenAllState_thenReturnAllBookings() {
        when(bookingRepository.findAllByBookerIdOrderByStartAsc(anyLong()))
                .thenReturn(List.of(booking));

        List<BookingDto> result = bookingService.readByBookerAndState(BookingState.ALL, 2L);

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void readByBookerAndState_whenCurrentState_thenReturnCurrentBookings() {
        booking.setStart(LocalDateTime.now().minusDays(1));
        booking.setEnd(LocalDateTime.now().plusDays(1));

        when(bookingRepository.findAllByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(
                anyLong(), any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(List.of(booking));

        List<BookingDto> result = bookingService.readByBookerAndState(BookingState.CURRENT, 2L);

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void readByOwnerAndState_whenAllState_thenReturnAllBookings() {
        when(bookingRepository.findAllByItemOwnerIdOrderByStartAsc(anyLong()))
                .thenReturn(List.of(booking));

        List<BookingDto> result = bookingService.readByOwnerAndState(BookingState.ALL, 1L);

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void readByOwnerAndState_whenFutureState_thenReturnFutureBookings() {
        when(bookingRepository.findAllByItemOwnerIdAndStartAfterOrderByStartDesc(
                anyLong(), any(LocalDateTime.class)))
                .thenReturn(List.of(booking));

        List<BookingDto> result = bookingService.readByOwnerAndState(BookingState.FUTURE, 1L);

        assertNotNull(result);
        assertEquals(1, result.size());
    }
}