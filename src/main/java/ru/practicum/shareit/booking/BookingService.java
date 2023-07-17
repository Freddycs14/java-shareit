package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoCreate;

import java.util.List;

public interface BookingService {
    BookingDto create(BookingDtoCreate bookingDtoCreate, Long userId);

    BookingDto update(Long bookingId, Long userId, Boolean approved);

    BookingDto getBooking(Long bookingId, Long userId);

    List<BookingDto> getUserBookings(Long userId, State state, int from, int size);

    List<BookingDto> getUserItemsBookings(Long userId, State state, int from, int size);
}
