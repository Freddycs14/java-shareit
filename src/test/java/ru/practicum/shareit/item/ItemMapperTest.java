package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ItemMapperTest {
    @Test
    public void toItemTest() {
        User user = User.builder().id(1L).name("Walter").email("w.white@gmail.com").build();
        ItemDto itemDto = ItemDto.builder().id(1L).name("name").description("description").available(true).requestId(0L).build();
        Item result = ItemMapper.toItem(itemDto, user);
        assertEquals(itemDto.getId(), result.getId());
        assertEquals(itemDto.getName(), result.getName());
        assertEquals(itemDto.getDescription(), result.getDescription());
        assertEquals(user, result.getOwner());
    }

    @Test
    public void toItemDtoTest() {
        User user = User.builder().id(1L).name("Walter").email("w.white@gmail.com").build();
        Item item = Item.builder().id(1L).name("name item").description("description").available(true).owner(user).requestId(0L).build();
        ItemDto result = ItemMapper.toItemDto(item);
        assertEquals(item.getId(), result.getId());
        assertEquals(item.getName(), result.getName());
        assertEquals(item.getDescription(), result.getDescription());
    }
}
