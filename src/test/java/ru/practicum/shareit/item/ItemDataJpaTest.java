package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemDataJpaTest {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final ItemRequestRepository itemRequestRepository;

    private Item itemOne;
    private Item itemTwo;

    private ItemRequest itemRequest;
    private User userOne;
    private User userTwo;

    @BeforeEach
    public void setUp() {
        userOne = User.builder().name("Walter").email("w.white@gmail.com").build();
        userOne = userRepository.save(userOne);

        userTwo = User.builder().name("Jesse").email("j.pinkman@gmail.com").build();
        userTwo = userRepository.save(userTwo);

        itemRequest = ItemRequest.builder().requestor(userTwo).description("description").created(LocalDateTime.now()).build();
        itemRequest = itemRequestRepository.save(itemRequest);

        itemOne = Item.builder().name("nameOne").description("descriptionOne").owner(userOne).available(true).requestId(itemRequest.getId()).build();
        itemOne = itemRepository.save(itemOne);

        itemTwo = Item.builder().name("nameTwo").description("descriptionTwo").owner(userTwo).available(true).build();
        itemTwo = itemRepository.save(itemTwo);
    }

    @Test
    public void shouldFindItemByOwnerIdTest() {
        List<Item> result = itemRepository.findItemByOwnerId(userOne.getId(), Pageable.unpaged());
        assertEquals(1, result.size());
        assertEquals(itemOne, result.get(0));
    }

    @Test
    public void shouldFindAllByRequestIdTest() {
        List<Long> itemRequestIds = List.of(itemRequest.getId());
        List<Item> result = itemRepository.findAllByRequestIdIn(itemRequestIds);
        assertEquals(1, result.size());
        assertEquals(itemOne, result.get(0));
    }

    @Test
    public void shouldSearchTest() {
        List<Item> result = itemRepository.search("descriptionTwo", Pageable.unpaged());
        assertEquals(1, result.size());
        assertEquals(itemTwo, result.get(0));
    }
}
