package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoCreate;

import javax.validation.Valid;
import java.util.List;

/**
 * TODO Sprint add-bookings.
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/bookings")
public class BookingController {
    private final BookingService bookingService;

    @PostMapping
    public BookingDto create(@RequestHeader("X-Sharer-User-Id") Long bookerId,
                             @Valid @RequestBody BookingDtoCreate bookingDtoCreate) {
        log.info("Получен запрос на создание бронирования от пользователя с id={}", bookerId);
        return bookingService.create(bookingDtoCreate, bookerId);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto update(@PathVariable Long bookingId, @RequestHeader("X-Sharer-User-Id") Long bookerId,
                             @RequestParam Boolean approved) {
        log.info("Получен запрос на обновления статуса бронирования от пользователя с id={}", bookerId);
        return bookingService.update(bookingId, bookerId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingDto getBooking(@PathVariable Long bookingId,
                                 @RequestHeader("X-Sharer-User-Id") Long bookerId) {
        log.info("Получен запрос на получения бронирования по id");
        return bookingService.getBooking(bookingId, bookerId);
    }

    @GetMapping
    public List<BookingDto> getUserBookings(@RequestParam(defaultValue = "ALL") State state,
                                            @RequestHeader("X-Sharer-User-Id") Long bookerId) {
        log.info("Получен запрос на получение списка всех бронирований пользователя с id={}", bookerId);
        return bookingService.getUserBookings(bookerId, state);
    }

    @GetMapping("/owner")
    public List<BookingDto> getUserItemsBookings(@RequestParam(defaultValue = "ALL") State state,
                                                 @RequestHeader("X-Sharer-User-Id") Long bookerId) {
        log.info("Получен запрос на получение списка всех бронирований для всех вещей пользователя с id={}", bookerId);
        return bookingService.getUserItemsBookings(bookerId, state);
    }
}
