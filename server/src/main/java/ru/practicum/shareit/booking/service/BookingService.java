package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.config.StateMachineFactory;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingDto;
import ru.practicum.shareit.booking.model.BookingEvent;
import ru.practicum.shareit.booking.model.BookingMapper;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.model.BookingStatusType;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.extention.ConditionsNotMetException;
import ru.practicum.shareit.extention.ExceptionMessages;
import ru.practicum.shareit.extention.NotFoundException;
import ru.practicum.shareit.extention.ValidationException;
import ru.practicum.shareit.extention.WrongUserExeption;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BookingService {
    private final BookingRepository bookingRepository;
    private final UserService userService;
    private final ItemService itemService;
    private final StateMachineFactory<BookingStatusType, BookingEvent> stateMachineFactory;

    public BookingDto getBookingDto(Long bookingId, Long userId) {
        Booking booking = getBooking(bookingId);
        if (!booking.getBooker().getId().equals(userId) && !booking.getItem().getOwner().getId().equals(userId)) {
            throw new ConditionsNotMetException(ExceptionMessages.BOOKING_GET_INFO_ERROR);
        }
        return BookingMapper.toBookingDto(booking);
    }

    public List<BookingDto> readByBookerAndState(BookingState state, Long userId) {
        List<Booking> bookings = switch (state) {
            case ALL -> bookingRepository.findAllByBookerIdOrderByStartAsc(userId);
            case WAITING ->
                    bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(userId, BookingStatusType.WAITING);
            case REJECTED ->
                    bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(userId, BookingStatusType.REJECTED);
            case PAST -> bookingRepository.findAllByBookerIdAndEndBeforeOrderByStartDesc(userId, LocalDateTime.now());
            case CURRENT ->
                    bookingRepository.findAllByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(userId, LocalDateTime.now(), LocalDateTime.now());
            case FUTURE ->
                    bookingRepository.findAllByBookerIdAndStartAfterOrderByStartDesc(userId, LocalDateTime.now());
        };

        return bookings.stream()
                .map(BookingMapper::toBookingDto)
                .toList();
    }

    public List<BookingDto> readByOwnerAndState(BookingState state, Long userId) {
        userService.getUser(userId);
        List<Booking> bookings = switch (state) {
            case ALL -> bookingRepository.findAllByItemOwnerIdOrderByStartAsc(userId);
            case WAITING ->
                    bookingRepository.findAllByItemOwnerIdAndStatusOrderByStartDesc(userId, BookingStatusType.WAITING);
            case REJECTED ->
                    bookingRepository.findAllByItemOwnerIdAndStatusOrderByStartDesc(userId, BookingStatusType.REJECTED);
            case PAST ->
                    bookingRepository.findAllByItemOwnerIdAndEndBeforeOrderByStartDesc(userId, LocalDateTime.now());
            case CURRENT ->
                    bookingRepository.findAllByItemOwnerIdAndStartBeforeAndEndAfterOrderByStartDesc(userId, LocalDateTime.now(), LocalDateTime.now());
            case FUTURE ->
                    bookingRepository.findAllByItemOwnerIdAndStartAfterOrderByStartDesc(userId, LocalDateTime.now());
        };

        return bookings.stream()
                .map(BookingMapper::toBookingDto)
                .toList();
    }

    public BookingDto createBooking(BookingDto bookingDto, Long userId) {
        User user = userService.getUser(userId);
        Item item = itemService.getItem(bookingDto.getItemId());

        Booking booking = BookingMapper.toBooking(bookingDto, item, user);
        validateBooking(booking);
        booking = bookingRepository.saveAndFlush(booking);

        return BookingMapper.toBookingDto(booking);
    }

    public BookingDto updateBookingStatus(Long bookingId, Long userId, Boolean approved) {
        Booking booking = getBooking(bookingId);
        if (!booking.getItem().getOwner().getId().equals(userId)) {
            throw new WrongUserExeption(ExceptionMessages.BOOKING_CHANGE_STATUS);
        }

        changeStatus(booking, approved ? BookingEvent.APPROVE : BookingEvent.REJECT);

        return BookingMapper.toBookingDto(bookingRepository.saveAndFlush(booking));
    }

    private void validateBooking(Booking booking) {
        Optional<Booking> bookingOptional = bookingRepository.findByItemIdAndEndIsAfterAndStartIsBefore(booking.getItem().getId(), booking.getEnd(), booking.getStart());

        if (bookingOptional.isPresent()) {
            throw new ConditionsNotMetException(ExceptionMessages.BOOKING_ITEM_IS_BOOKED);
        }

        Item item = booking.getItem();

        if (!item.getIsAvailable()) {
            throw new ValidationException(ExceptionMessages.BOOKING_ITEM_IS_NOT_AVAILABLE);
        }

        if (booking.getBooker().getId().equals(item.getOwner().getId())) {
            throw new ValidationException(ExceptionMessages.BOOKING_OWNER_CANT_BOOKED);
        }

        if (booking.getEnd().isBefore(booking.getStart())) {
            throw new ValidationException(ExceptionMessages.BOOKING_END_BEFORE_START);
        }
    }

    private Booking getBooking(Long bookingId) {
        return Optional.ofNullable(bookingRepository.findById(bookingId))
                .orElseThrow(() -> new NotFoundException(String.format(ExceptionMessages.BOOKING_NOT_FOUND_ERROR, bookingId))).get();
    }

    private void changeStatus(Booking booking, BookingEvent bookingEvent) {
        StateMachine<BookingStatusType, BookingEvent> sm = stateMachineFactory.getStateMachine(booking.getId().toString());
        sm.start();
        sm.getExtendedState().getVariables().put("booking", booking);
        sm.sendEvent(bookingEvent);

        booking.setStatus(sm.getState().getId());
        sm.stop();
    }
}
