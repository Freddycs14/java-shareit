package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class BookingDataJpaTest {
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    private Item item1;
    private Item item2;
    private User booker1;
    private User booker2;
    private User owner1;
    private User owner2;
    private Booking booking1;
    private Booking booking2;
    private final LocalDateTime now = LocalDateTime.now();

    @BeforeEach
    public void setUp() {
        owner1 = User.builder().name("Walter").email("w.white@gmail.com").build();
        owner1 = userRepository.save(owner1);

        owner2 = User.builder().name("Jesse").email("j.pinkman@gmail.com").build();
        owner2 = userRepository.save(owner2);

        booker1 = User.builder().name("Soul").email("s.goodman@gmail.com").build();
        booker1 = userRepository.save(booker1);

        booker2 = User.builder().name("Mike").email("m.ehrmantraut@gmail.com").build();
        booker2 = userRepository.save(booker2);

        item1 = Item.builder().name("nameOne").description("descriptionOne").owner(owner1).available(true).build();
        item1 = itemRepository.save(item1);

        item2 = Item.builder().name("nameTwo").description("descriptionTwo").owner(owner2).available(true).build();
        item2 = itemRepository.save(item2);

        booking1 = Booking.builder().item(item1).booker(booker1).start(now.minusHours(3)).end(now.minusHours(1))
                .status(BookingStatus.APPROVED).build();
        booking1 = bookingRepository.save(booking1);
        booking2 = Booking.builder().item(item2).booker(booker2).start(now.minusDays(3)).end(now.minusDays(1))
                .status(BookingStatus.APPROVED).build();
        booking2 = bookingRepository.save(booking2);
    }

    @Test
    public void shouldGetAllByBookerIdOrderByStartDescTest() {
        List<Booking> result = bookingRepository.findAllByBookerIdOrderByStartDesc(booker1.getId(), Pageable.unpaged());
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(booking1, result.get(0));
    }

    @Test
    public void shouldGetCurrentBookerBookingsTest() {
        List<Booking> result = bookingRepository.findCurrentBookerBookings(booker1.getId(), now.minusHours(2), Pageable.unpaged());
        assertNotNull(result);
        assertEquals(booking1, result.get(0));
    }

    @Test
    public void shouldGetAllByBookerIdAndStartIsAfterTest() {
        List<Booking> result = bookingRepository.findAllByBookerIdAndStartIsAfter(booker1.getId(), now.minusHours(10), Pageable.unpaged());
        assertNotNull(result);
        assertEquals(booking1, result.get(0));
    }

    @Test
    public void shouldGetAllByBookerIdAndEndIsBeforeTest() {
        List<Booking> result = bookingRepository.findAllByBookerIdAndEndIsBefore(booker1.getId(), now, Pageable.unpaged());
        assertNotNull(result);
        assertEquals(booking1, result.get(0));
    }

    @Test
    public void shouldGetAllByItemOwnerIdOrderByStartDescTest() {
        List<Booking> result = bookingRepository.findAllByItemOwnerIdOrderByStartDesc(owner1.getId(), Pageable.unpaged());
        assertNotNull(result);
        assertEquals(booking1, result.get(0));
    }

    @Test
    public void shouldGetBookingsByItemOwnerCurrentTest() {
        List<Booking> result = bookingRepository.findBookingsByItemOwnerCurrent(owner1.getId(), now.minusHours(2), Pageable.unpaged());
        assertNotNull(result);
        assertEquals(booking1, result.get(0));
    }

    @Test
    public void shouldGetAllByItemOwnerIdAndStartIsAfterTest() {
        List<Booking> result = bookingRepository.findAllByItemOwnerIdAndStartIsAfter(owner1.getId(), now.minusHours(10), Pageable.unpaged());
        assertNotNull(result);
        assertEquals(booking1, result.get(0));
    }

    @Test
    public void shouldGetAllByItemOwnerIdAndEndIsBeforeTest() {
        List<Booking> result = bookingRepository.findAllByItemOwnerIdAndEndIsBefore(owner1.getId(), now, Pageable.unpaged());
        assertNotNull(result);
        assertEquals(booking1, result.get(0));
    }

    @Test
    public void shouldGetAllByBookerAndStatusEqualsTest() {
        List<Booking> result = bookingRepository.findAllByBookerAndStatusEquals(booker1, BookingStatus.APPROVED, Pageable.unpaged());
        assertNotNull(result);
        assertEquals(booking1, result.get(0));
    }

    @Test
    public void shouldGetAllByItem_OwnerAndStatusEqualsTest() {
        List<Booking> result = bookingRepository.findAllByItem_OwnerAndStatusEquals(owner1, BookingStatus.APPROVED, Pageable.unpaged());
        assertNotNull(result);
        assertEquals(booking1, result.get(0));
    }

    @Test
    public void shouldGetFirstByBookerIdAndItemIdAndEndIsBeforeTest() {
        Booking result = bookingRepository.findFirstByBookerIdAndItemIdAndEndIsBefore(booker1.getId(), item1.getId(), now, Sort.by("end").descending());
        assertNotNull(result);
        assertEquals(booking1, result);
    }
}
