package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoCreate;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class BookingControllerTest {

    @Mock
    private BookingService service;
    @InjectMocks
    private BookingController controller;

    private User owner;
    private Item item;
    private User booker;
    private BookingDtoCreate bookingDtoCreate;
    private Booking booking;

    @BeforeEach
    public void setUp() {
        owner = User.builder().id(1L).name("name").email("email@gmail.com").build();
        item = Item.builder().id(1L).name("name").owner(owner).description("description").build();
        booker = User.builder().id(2L).name("name booker").email("booker@gmail.com").build();
        bookingDtoCreate = BookingDtoCreate.builder().id(1L).itemId(item.getId())
                .start(LocalDateTime.now()).end(LocalDateTime.now().plusSeconds(1)).build();
        booking = BookingMapper.toBookingDtoCreate(bookingDtoCreate, item, booker);
    }

    @Test
    public void shouldCreateBookingTest() {
        BookingDto bookingDto = BookingMapper.toBookingDto(booking);
        Mockito.when((service.create(bookingDtoCreate, booker.getId()))).thenReturn(bookingDto);
        BookingDto result = controller.create(booker.getId(), bookingDtoCreate);
        assertEquals(bookingDto, result);
    }

    @Test
    public void shouldUpdateBookingTest() {
        Booking bookingUpdate = Booking.builder().item(item).booker(booker).start(LocalDateTime.now())
                .end(LocalDateTime.now().plusSeconds(1)).status(BookingStatus.REJECTED).build();
        bookingUpdate.setId(1L);
        BookingDto bookingDto = BookingMapper.toBookingDto(bookingUpdate);
        Mockito.when(service.update(bookingUpdate.getId(), booker.getId(), true)).thenReturn(bookingDto);
        BookingDto result = controller.update(booking.getId(), booker.getId(), true);
        assertEquals(bookingDto, result);
        assertEquals(bookingDto.getStatus(), result.getStatus());
    }

    @Test
    public void shouldGetBookingTest() {
        BookingDto bookingDto = BookingMapper.toBookingDto(booking);
        Mockito.when(service.getBooking(booking.getId(), booker.getId())).thenReturn(bookingDto);
        BookingDto result = controller.getBooking(booking.getId(), booker.getId());
        assertEquals(bookingDto, result);
    }

    @Test
    public void shouldGetUserBookingsTest() {
        int from = 1;
        int size = 10;
        BookingDto bookingDto = BookingMapper.toBookingDto(booking);
        List<BookingDto> bookings = List.of(bookingDto);
        Mockito.when(service.getUserBookings(booker.getId(), State.ALL, from, size)).thenReturn(bookings);
        List<BookingDto> result = controller.getUserBookings(State.ALL, booker.getId(), from, size);
        assertEquals(bookings, result);
    }

    @Test
    public void shouldGetUserItemsBookingsTest() {
        int from = 1;
        int size = 10;
        BookingDto bookingDto = BookingMapper.toBookingDto(booking);
        List<BookingDto> bookings = List.of(bookingDto);
        Mockito.when(service.getUserItemsBookings(booker.getId(), State.ALL, from, size)).thenReturn(bookings);
        List<BookingDto> result = controller.getUserItemsBookings(State.ALL, booker.getId(), from, size);
        assertEquals(bookings, result);
    }
}
