package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@Transactional
public class ItemIntegrationTest {

    @Autowired
    private ItemService service;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private UserRepository userRepository;

    private User owner;
    private Item item;

    @BeforeEach
    public void setUp() {
        owner = User.builder().name("Walter").email("w.white@gmail.com").build();
        userRepository.save(owner);
        item = Item.builder().name("name").description("description").available(true).owner(owner).build();
        itemRepository.save(item);
    }

    @Test
    void testCreateItem() {
        ItemDto itemDto = ItemMapper.toItemDto(item);
        ItemDto result = service.create(owner.getId(), itemDto);

        assertNotNull(result.getId());
        assertEquals(itemDto.getName(), result.getName());
        assertEquals(itemDto.getAvailable(), result.getAvailable());
    }
}
