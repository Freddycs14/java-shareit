package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoCreate;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@Transactional
public class BookingIntegrationTest {
    @Autowired
    private BookingService service;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ItemRepository itemRepository;
    private User owner;
    private User booker;
    private Item item;
    private BookingDtoCreate bookingDtoCreate;
    private LocalDateTime now = LocalDateTime.now();

    @BeforeEach
    public void setUp() {
        owner = User.builder().name("Walter").email("w.white@gmail.com").build();
        owner = userRepository.save(owner);
        booker = User.builder().name("Jesse").email("j.pinkman@gmail.com").build();
        booker = userRepository.save(booker);
        item = Item.builder().name("name").description("description").owner(owner).available(true).build();
        item = itemRepository.save(item);
        bookingDtoCreate = BookingDtoCreate.builder().itemId(item.getId())
                .start(now.plusHours(1)).end(now.plusHours(3))
                .build();
    }

    @Test
    public void shouldCreateBookingTest() {
        BookingDto bookingDto = BookingMapper.toBookingDto(BookingMapper.toBookingDtoCreate(bookingDtoCreate, item, booker));
        bookingDto.setId(1L);
        BookingDto result = service.create(bookingDtoCreate, booker.getId());
        assertNotNull(result);
        assertEquals(bookingDto.getStart(), result.getStart());
        assertEquals(bookingDto.getEnd(), result.getEnd());
        assertEquals(bookingDto.getBooker(), result.getBooker());
    }
}
