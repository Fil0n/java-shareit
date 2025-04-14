package ru.practicum.shareit.booking.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.booking.model.BookingDto;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.service.BookingService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/bookings")
public class BookingController {
    private final BookingService bookingService;

    @PostMapping
    public BookingDto create(@Valid @RequestBody
                             @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) BookingDto bookingDto,
                             @RequestHeader("X-Sharer-User-Id") Long userId) {
        return bookingService.createBooking(bookingDto, userId);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto updateStatus(@PathVariable Long bookingId,
                                   @RequestHeader("X-Sharer-User-Id") Long userId,
                                   @RequestParam Boolean approved) {
        return bookingService.updateBookingStatus(bookingId, userId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingDto findById(@PathVariable Long bookingId,
                               @RequestHeader("X-Sharer-User-Id") Long userId) {
        return bookingService.getBookingDto(bookingId, userId);
    }

    @GetMapping
    public List<BookingDto> findAllByBookerAndState(@RequestParam(required = false, defaultValue = "ALL") BookingState state,
                                                    @RequestHeader("X-Sharer-User-Id") Long userId) {
        return bookingService.readByBookerAndState(state, userId);
    }

    @GetMapping("/owner")
    public List<BookingDto> findAllByOwnerAndState(@RequestParam(required = false, defaultValue = "ALL") BookingState state,
                                                   @RequestHeader("X-Sharer-User-Id") Long userId) {
        return bookingService.readByOwnerAndState(state, userId);
    }
}
