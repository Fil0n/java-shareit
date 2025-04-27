package ru.practicum.shareit.booking.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.client.BookingClient;
import ru.practicum.shareit.booking.model.BookingDto;
import ru.practicum.shareit.booking.model.BookingState;

@Controller
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
@Validated
public class BookingController {
    private final BookingClient bookingClient;

    @GetMapping
    public ResponseEntity<Object> findAllByBookerAndState(@RequestHeader("X-Sharer-User-Id") long userId,
                                                          @RequestParam(name = "state", defaultValue = "all") String state) {
        BookingState bookingState = BookingState.from(state)
                .orElseThrow(() -> new IllegalArgumentException("Unknown state: " + state));
        log.info("Get booking with state {}, userId={}", state, userId);
        return bookingClient.findAllByBookerAndState(userId, bookingState);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> findById(@RequestHeader("X-Sharer-User-Id") long userId,
                                           @PathVariable Long bookingId) {
        log.info("Get booking {}, userId={}", bookingId, userId);
        return bookingClient.findById(userId, bookingId);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> findAllByItemOwnerAndState(@RequestHeader("X-Sharer-User-Id") long userId,
                                                             @RequestParam(name = "state", defaultValue = "all") String state) {
        BookingState bookingState = BookingState.from(state)
                .orElseThrow(() -> new IllegalArgumentException("Unknown state: " + state));
        log.info("Get booking with state {}, ownerId={}", state, userId);
        return bookingClient.findAllByOwnerAndState(userId, bookingState);

    }

    @PostMapping
    public ResponseEntity<Object> create(@RequestHeader("X-Sharer-User-Id") long userId,
                                         @RequestBody @Valid BookingDto bookingDto) {
        log.info("Creating booking {}, userId={}", bookingDto, userId);
        return bookingClient.createBooking(userId, bookingDto);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> updateStatus(@PathVariable Long bookingId,
                                               @RequestHeader("X-Sharer-User-Id") Long userId,
                                               @RequestParam Boolean approved) {
        return bookingClient.updateStatus(userId, bookingId, approved);
    }


}
