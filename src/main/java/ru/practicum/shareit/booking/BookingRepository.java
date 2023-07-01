package ru.practicum.shareit.booking;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    //Получение списка всех бронирований текущего пользователя:
    List<Booking> findAllByBookerIdOrderByStartDesc(Long userId);

    @Query("select b from bookings b " +
            "where b.booker.id = :userId and " +
            "b.start < :now and " +
            "b.end > :now " +
            "order by b.start desc")
    List<Booking> findCurrentBookerBookings(Long userId, LocalDateTime now);

    List<Booking> findAllByBookerIdAndStartIsAfter(Long userId, LocalDateTime now);

    List<Booking> findAllByBookerIdAndEndIsBefore(Long userId, LocalDateTime now);

    //Получение списка бронирований для всех вещей текущего пользователя
    List<Booking> findAllByItemOwnerIdOrderByStartDesc(Long userId);

    @Query("select b from bookings b " +
            "where b.item.owner.id = :userId " +
            "and b.start < :now " +
            "and b.end > :now " +
            "order by b.start asc")
    List<Booking> findBookingsByItemOwnerCurrent(Long userId, LocalDateTime now);

    List<Booking> findAllByItemOwnerIdAndStartIsAfter(Long userId, LocalDateTime start);

    List<Booking> findAllByItemOwnerIdAndEndIsBefore(Long userId, LocalDateTime end);


    List<Booking> findAllByBookerAndStatusEqualsOrderByStartDesc(User user, BookingStatus status);

    List<Booking> findAllByItem_OwnerAndStatusEqualsOrderByStartDesc(User user, BookingStatus status);

    Booking findFirstByBookerIdAndItemIdAndEndIsBeforeOrderByEndDesc(long userId, long itemId, LocalDateTime now);
}
