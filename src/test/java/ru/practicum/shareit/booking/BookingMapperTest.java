package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoCreate;
import ru.practicum.shareit.booking.dto.BookingForItemDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class BookingMapperTest {
    private Item item;
    private User booker;
    private User owner;

    @BeforeEach
    public void setUp() {
        owner = User.builder().id(1L).name("name owner").email("owner@gmail.com").build();
        booker = User.builder().id(2L).name("name booker").email("booker@gmail.com").build();
        item = Item.builder().id(1L).name("name").owner(owner).description("description").build();
    }

    @Test
    public void toBookingDtoTest() {
        Booking booking = Booking.builder().id(1L).item(item).booker(booker).status(BookingStatus.WAITING)
                .start(LocalDateTime.of(2023, 7, 10, 12, 0))
                .end(LocalDateTime.of(2023, 7, 10, 14, 0))
                .build();
        BookingDto bookingDto = BookingMapper.toBookingDto(booking);
        assertEquals(1L, bookingDto.getId());
        assertEquals(booking.getStart(), bookingDto.getStart());
        assertEquals(booking.getEnd(), bookingDto.getEnd());
        assertEquals(booking.getBooker().getId(), bookingDto.getBooker().getId());
    }

    @Test
    public void toBookingForItemDtoTest() {
        Booking booking = Booking.builder().id(1L).item(item).booker(booker).status(BookingStatus.WAITING)
                .start(LocalDateTime.of(2023, 7, 10, 12, 0))
                .end(LocalDateTime.of(2023, 7, 10, 14, 0))
                .build();
        BookingForItemDto bookingForItemDto = BookingMapper.toBookingForItemDto(booking);
        assertEquals(1L, bookingForItemDto.getId());
        assertEquals(booking.getStart(), bookingForItemDto.getStart());
        assertEquals(booking.getEnd(), bookingForItemDto.getEnd());
        assertEquals(booking.getBooker().getId(), bookingForItemDto.getBookerId());
    }

    @Test
    public void toBookingDtoCreateTest() {
        BookingDtoCreate bookingDtoCreate = BookingDtoCreate.builder().id(1L).itemId(item.getId())
                .start(LocalDateTime.of(2023, 7, 10, 12, 0))
                .end(LocalDateTime.of(2023, 7, 10, 14, 0))
                .build();
        Booking booking = BookingMapper.toBookingDtoCreate(bookingDtoCreate, item, booker);
        assertEquals(bookingDtoCreate.getId(), booking.getId());
        assertEquals(bookingDtoCreate.getStart(), booking.getStart());
        assertEquals(bookingDtoCreate.getEnd(), booking.getEnd());
        assertEquals(bookingDtoCreate.getItemId(), booking.getItem().getId());
    }
}
