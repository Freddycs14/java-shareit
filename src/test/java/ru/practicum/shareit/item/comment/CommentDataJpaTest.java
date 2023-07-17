package ru.practicum.shareit.item.comment;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.comment.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.data.domain.Sort.Direction.DESC;

@DataJpaTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class CommentDataJpaTest {
    private final CommentRepository commentRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;

    private Item itemOne;
    private Item itemTwo;
    private User userOne;
    private User userTwo;
    private Comment commentOne;
    private Comment commentTwo;
    private Booking bookingOne;
    private Booking bookingTwo;

    @BeforeEach
    public void setUp() {
        userOne = User.builder().name("Walter").email("w.white@gmail.com").build();
        userOne = userRepository.save(userOne);

        userTwo = User.builder().name("Jesse").email("j.pinkman@gmail.com").build();
        userTwo = userRepository.save(userTwo);

        itemOne = Item.builder().name("nameOne").description("descriptionOne").owner(userOne).available(true).build();
        itemOne = itemRepository.save(itemOne);

        itemTwo = Item.builder().name("nameTwo").description("descriptionTwo").owner(userTwo).available(true).build();
        itemTwo = itemRepository.save(itemTwo);

        bookingOne = Booking.builder().booker(userTwo).item(itemTwo).start(LocalDateTime.now()).end(LocalDateTime.now().plusNanos(1)).build();
        bookingOne = bookingRepository.save(bookingOne);

        bookingTwo = Booking.builder().booker(userOne).item(itemTwo).start(LocalDateTime.now()).end(LocalDateTime.now().plusNanos(1)).build();
        bookingTwo = bookingRepository.save(bookingTwo);

        commentOne = Comment.builder().item(itemOne).text("text").user(userTwo).created(LocalDateTime.now()).build();
        commentOne = commentRepository.save(commentOne);

        commentTwo = Comment.builder().item(itemTwo).text("text").user(userOne).created(LocalDateTime.now().plusNanos(1)).build();
        commentTwo = commentRepository.save(commentTwo);

    }

    @Test
    public void shouldFindAllByItem() {
        List<Item> items = List.of(itemOne, itemTwo);
        List<Comment> comments = commentRepository.findAllByItemIn(items, Sort.by(DESC, "created"));
        assertEquals(2, comments.size());
        assertEquals(comments.get(0), commentTwo);
    }
}
