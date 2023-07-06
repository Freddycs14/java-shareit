package ru.practicum.shareit.booking;

import org.springframework.data.domain.Sort;
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

    List<Booking> findAllByBookerIdAndStartIsAfter(Long userId, LocalDateTime now, Sort sort);

    List<Booking> findAllByBookerIdAndEndIsBefore(Long userId, LocalDateTime now, Sort sort);

    //Получение списка бронирований для всех вещей текущего пользователя
    List<Booking> findAllByItemOwnerIdOrderByStartDesc(Long userId);

    @Query("select b from bookings b " +
            "where b.item.owner.id = :userId " +
            "and b.start < :now " +
            "and b.end > :now " +
            "order by b.start asc")
    List<Booking> findBookingsByItemOwnerCurrent(Long userId, LocalDateTime now);

    List<Booking> findAllByItemOwnerIdAndStartIsAfter(Long userId, LocalDateTime start, Sort sort);

    List<Booking> findAllByItemOwnerIdAndEndIsBefore(Long userId, LocalDateTime end, Sort sort);


    List<Booking> findAllByBookerAndStatusEquals(User user, BookingStatus status, Sort sort);

    List<Booking> findAllByItem_OwnerAndStatusEquals(User user, BookingStatus status, Sort sort);

    Booking findFirstByBookerIdAndItemIdAndEndIsBefore(long userId, long itemId, LocalDateTime now, Sort sort);
}
