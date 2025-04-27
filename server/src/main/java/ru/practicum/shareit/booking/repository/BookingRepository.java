package ru.practicum.shareit.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatusType;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findAllByBookerIdOrderByStartAsc(Long userId);

    List<Booking> findAllByBookerIdAndStatusOrderByStartDesc(Long userId, BookingStatusType bookingStatus);

    List<Booking> findAllByBookerIdAndEndBeforeOrderByStartDesc(Long userId, LocalDateTime now);

    List<Booking> findAllByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(Long userId, LocalDateTime now, LocalDateTime now1);

    List<Booking> findAllByBookerIdAndStartAfterOrderByStartDesc(Long userId, LocalDateTime now);

    List<Booking> findAllByItemOwnerIdOrderByStartAsc(Long userId);

    List<Booking> findAllByItemOwnerIdAndStatusOrderByStartDesc(Long userId, BookingStatusType bookingStatus);

    List<Booking> findAllByItemOwnerIdAndEndBeforeOrderByStartDesc(Long userId, LocalDateTime now);

    List<Booking> findAllByItemOwnerIdAndStartBeforeAndEndAfterOrderByStartDesc(Long userId, LocalDateTime now, LocalDateTime now1);

    List<Booking> findAllByItemOwnerIdAndStartAfterOrderByStartDesc(Long userId, LocalDateTime now);

    Optional<Booking> findByItemIdAndEndIsAfterAndStartIsBefore(Long id, LocalDateTime end, LocalDateTime start);

    Optional<Booking> findByItemIdAndBookerIdAndEndBefore(Long itemId, Long userId, LocalDateTime now);
}