package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoCreate;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.exception.ValidationStateException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
public class BookingServiceImplTest {
    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ItemRepository itemRepository;

    @InjectMocks
    private BookingServiceImpl bookingService;

    private User owner;
    private User booker;
    private Item item;
    private BookingDtoCreate bookingDtoCreate;
    private LocalDateTime now = LocalDateTime.now();

    @BeforeEach
    public void setUp() {
        owner = User.builder().id(1L).name("Walter").email("w.white@gmail.com").build();
        booker = User.builder().id(2L).name("Jesse").email("j.pinkman@gmail.com").build();
        item = Item.builder().id(1L).name("name").description("description").owner(owner).available(true).build();
        bookingDtoCreate = BookingDtoCreate.builder().id(1L).itemId(item.getId())
                .start(now.plusHours(1)).end(now.plusHours(3))
                .build();
    }

    @Test
    public void shouldCreateBookingTest() {
        Booking booking = BookingMapper.toBookingDtoCreate(bookingDtoCreate, item, booker);
        Mockito.when(userRepository.findById(booker.getId())).thenReturn(Optional.of(booker));
        Mockito.when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));
        Mockito.when(bookingRepository.save(booking)).thenReturn(booking);
        BookingDto result = bookingService.create(bookingDtoCreate, booker.getId());
        assertEquals(booking.getStart(), result.getStart());
        assertEquals(booking.getEnd(), result.getEnd());
        assertEquals(booking.getItem().getId(), result.getItem().getId());
        assertEquals(booking.getBooker().getId(), result.getBooker().getId());
    }

    @Test
    public void shouldNotCreateBookingNotValidTest() {
        bookingDtoCreate.setStart(null);
        assertThrows(ValidationException.class, () -> bookingService.create(bookingDtoCreate, booker.getId()));

        bookingDtoCreate.setStart(bookingDtoCreate.getEnd());
        assertThrows(ValidationException.class, () -> bookingService.create(bookingDtoCreate, booker.getId()));

        bookingDtoCreate.setStart(now.minusHours(2));
        bookingDtoCreate.setEnd(now.minusHours(1));
        Mockito.when(userRepository.findById(booker.getId())).thenReturn(Optional.of(booker));
        Mockito.when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));
        assertThrows(ValidationException.class, () -> bookingService.create(bookingDtoCreate, booker.getId()));

        bookingDtoCreate.setEnd(now.minusHours(3));
        assertThrows(ValidationException.class, () -> bookingService.create(bookingDtoCreate, booker.getId()));

        bookingDtoCreate.setItemId(null);
        bookingDtoCreate.setStart(now.plusHours(1));
        bookingDtoCreate.setEnd(now.plusHours(3));
        assertThrows(ValidationException.class, () -> bookingService.create(bookingDtoCreate, booker.getId()));


    }

    @Test
    public void shouldNotCreateBookingIfBookerEqualsOwnerTest() {
        assertThrows(UserNotFoundException.class, () -> bookingService.create(bookingDtoCreate, owner.getId()));
    }

    @Test
    public void shouldNotCreateBookingIfItemNotAvailableTest() {
        item.setAvailable(false);
        Mockito.when(userRepository.findById(booker.getId())).thenReturn(Optional.of(booker));
        Mockito.when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));
        assertThrows(ValidationException.class, () -> bookingService.create(bookingDtoCreate, booker.getId()));
    }

    @Test
    public void shouldUpdateBookingTest() {
        Long bookerId = 3L;
        Boolean approved = true;
        Booking booking = BookingMapper.toBookingDtoCreate(bookingDtoCreate, item, booker);
        Mockito.when(bookingRepository.findById(booking.getId())).thenReturn(Optional.of(booking));
        BookingDto result = bookingService.update(booking.getId(), bookerId, approved);
        assertEquals(approved ? BookingStatus.APPROVED : BookingStatus.REJECTED, booking.getStatus());
        Mockito.verify(bookingRepository, times(1)).save(booking);
        assertNotNull(result);
        assertEquals(booking.getId(), result.getId());
        assertEquals(booking.getStart(), result.getStart());
        assertEquals(booking.getEnd(), result.getEnd());
    }

    @Test
    public void shouldNotUpdateBookingIfBookerEqualsOwnerTest() {
        Long bookerId = 2L;
        Boolean approved = true;
        Booking booking = BookingMapper.toBookingDtoCreate(bookingDtoCreate, item, booker);
        Mockito.when(bookingRepository.findById(booking.getId())).thenReturn(Optional.of(booking));
        assertThrows(UserNotFoundException.class, () -> bookingService.update(booking.getId(), bookerId, approved));
    }

    @Test
    public void shouldNotUpdateBookingIfStatusApprovedTest() {
        Long bookerId = 3L;
        Boolean approved = true;
        Booking booking = BookingMapper.toBookingDtoCreate(bookingDtoCreate, item, booker);
        booking.setStatus(BookingStatus.APPROVED);
        Mockito.when(bookingRepository.findById(booking.getId())).thenReturn(Optional.of(booking));
        assertThrows(ValidationException.class, () -> bookingService.update(booking.getId(), bookerId, approved));
    }

    @Test
    public void shouldGetBookingTest() {
        Booking booking = BookingMapper.toBookingDtoCreate(bookingDtoCreate, item, booker);
        Mockito.when(bookingRepository.findById(booking.getId())).thenReturn(Optional.of(booking));
        Mockito.when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));
        BookingDto result = bookingService.getBooking(booking.getId(), booker.getId());
    }

    @Test
    public void shouldGetUserBookingsTest() {
        int from = 0;
        int size = 10;
        Sort sort = Sort.by("start").descending();
        Booking booking = BookingMapper.toBookingDtoCreate(bookingDtoCreate, item, booker);
        Mockito.when(userRepository.findById(booker.getId())).thenReturn(Optional.of(booker));

        Mockito.when(bookingRepository.findAllByBookerIdOrderByStartDesc(booker.getId(),
                PageRequest.of(from / size, size))).thenReturn(List.of(booking));
        List<BookingDto> resultAll = bookingService.getUserBookings(booker.getId(), State.ALL, from, size);
        assertNotNull(resultAll, "Список пустой");

        Mockito.when(bookingRepository.findAllByBookerAndStatusEquals(booker, BookingStatus.WAITING,
                PageRequest.of(from / size, size, sort))).thenReturn(List.of(booking));
        List<BookingDto> resultWaiting = bookingService.getUserBookings(booker.getId(), State.WAITING, from, size);
        assertNotNull(resultWaiting, "Список пустой");

        Mockito.when(bookingRepository.findAllByBookerAndStatusEquals(booker, BookingStatus.APPROVED,
                PageRequest.of(from / size, size, sort))).thenReturn(List.of(booking));
        List<BookingDto> resultApproved = bookingService.getUserBookings(booker.getId(), State.APPROVED, from, size);
        assertNotNull(resultApproved, "Список пустой");

        Mockito.when(bookingRepository.findAllByBookerAndStatusEquals(booker, BookingStatus.REJECTED,
                PageRequest.of(from / size, size, sort))).thenReturn(List.of(booking));
        List<BookingDto> resultRejected = bookingService.getUserBookings(booker.getId(), State.REJECTED, from, size);
        assertNotNull(resultRejected, "Список пустой");
    }

    @Test
    public void shouldGetUsersBookingWithUnsupportedStatusTest() {
        int from = 0;
        int size = 10;
        Mockito.when(userRepository.findById(booker.getId())).thenReturn(Optional.of(booker));
        assertThrows(ValidationStateException.class, () ->
                bookingService.getUserBookings(booker.getId(), State.UNSUPPORTED_STATUS, from, size));
    }

    @Test
    public void shouldGetUserItemsBookingsTest() {
        int from = 0;
        int size = 10;
        Sort sort = Sort.by("start").descending();
        Booking booking = BookingMapper.toBookingDtoCreate(bookingDtoCreate, item, booker);
        Mockito.when(userRepository.findById(booker.getId())).thenReturn(Optional.of(booker));

        Mockito.when(bookingRepository.findAllByItemOwnerIdOrderByStartDesc(booker.getId(),
                PageRequest.of(from / size, size))).thenReturn(List.of(booking));
        List<BookingDto> resultAll = bookingService.getUserItemsBookings(booker.getId(), State.ALL, from, size);
        assertNotNull(resultAll, "Список пустой");

        Mockito.when(bookingRepository.findAllByItem_OwnerAndStatusEquals(booker, BookingStatus.WAITING,
                PageRequest.of(from / size, size, sort))).thenReturn(List.of(booking));
        List<BookingDto> resultWaiting = bookingService.getUserItemsBookings(booker.getId(), State.WAITING, from, size);
        assertNotNull(resultWaiting, "Список пустой");

        Mockito.when(bookingRepository.findAllByItem_OwnerAndStatusEquals(booker, BookingStatus.APPROVED,
                PageRequest.of(from / size, size, sort))).thenReturn(List.of(booking));
        List<BookingDto> resultApproved = bookingService.getUserItemsBookings(booker.getId(), State.APPROVED, from, size);
        assertNotNull(resultApproved, "Список пустой");

        Mockito.when(bookingRepository.findAllByItem_OwnerAndStatusEquals(booker, BookingStatus.REJECTED,
                PageRequest.of(from / size, size, sort))).thenReturn(List.of(booking));
        List<BookingDto> resultRejected = bookingService.getUserItemsBookings(booker.getId(), State.REJECTED, from, size);
        assertNotNull(resultRejected, "Список пустой");
    }

    @Test
    public void shouldGetUserItemsBookingWithUnsupportedStatusTest() {
        int from = 0;
        int size = 10;
        Mockito.when(userRepository.findById(booker.getId())).thenReturn(Optional.of(booker));
        assertThrows(ValidationStateException.class, () ->
                bookingService.getUserItemsBookings(booker.getId(), State.UNSUPPORTED_STATUS, from, size));
    }
}
