package ru.practicum.shareit.booking;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoCreate;

import javax.validation.Valid;
import java.util.List;

/**
 * TODO Sprint add-bookings.
 */
@RestController
@RequestMapping(path = "/bookings")
public class BookingController {
    private final BookingService bookingService;

    @Autowired
    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping
    public BookingDto create(@RequestHeader("X-Sharer-User-Id") Long bookerId,
                             @Valid @RequestBody BookingDtoCreate bookingDtoCreate) {
        return bookingService.create(bookingDtoCreate, bookerId);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto update(@PathVariable Long bookingId, @RequestHeader("X-Sharer-User-Id") Long bookerId,
                             @RequestParam Boolean approved) {
        return bookingService.update(bookingId, bookerId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingDto getBooking(@PathVariable Long bookingId,
                                 @RequestHeader("X-Sharer-User-Id") Long bookerId) {
        return bookingService.getBooking(bookingId, bookerId);
    }

    @GetMapping
    public List<BookingDto> getUserBookings(@RequestParam(defaultValue = "ALL") State state,
                                            @RequestHeader("X-Sharer-User-Id") Long bookerId) {
        return bookingService.getUserBookings(bookerId, state);
    }

    @GetMapping("/owner")
    public List<BookingDto> getUserItemsBookings(@RequestParam(defaultValue = "ALL") State state,
                                                 @RequestHeader("X-Sharer-User-Id") Long bookerId) {
        return bookingService.getUserItemsBookings(bookerId, state);
    }
}
