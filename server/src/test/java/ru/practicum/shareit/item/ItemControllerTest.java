package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class ItemControllerTest {
    @Mock
    private ItemService itemService;

    @InjectMocks
    private ItemController itemController;

    private Item item;
    private User user;

    @BeforeEach
    public void setUp() {
        user = User.builder().id(1L).name("name").email("email@gmail.com").build();
        item = Item.builder().id(1L).name("name").owner(user).description("description").build();
    }

    @Test
    public void shouldCreateItemTest() {
        ItemDto itemDto = ItemMapper.toItemDto(item);
        Mockito.when(itemService.create(user.getId(), itemDto)).thenReturn(itemDto);
        ItemDto result = itemController.create(user.getId(), itemDto);
        assertEquals(itemDto, result);
    }

    @Test
    public void shouldUpdateItemTest() {
        Item itemNewName = Item.builder().name("update name").owner(user).description("description").build();
        itemNewName.setId(item.getId());
        ItemDto itemNewNameDto = ItemMapper.toItemDto(itemNewName);
        Mockito.when(itemService.update(item.getId(), user.getId(), itemNewNameDto)).thenReturn(itemNewNameDto);
        ItemDto result = itemController.update(user.getId(), itemNewNameDto, itemNewNameDto.getId());
        assertEquals(itemNewNameDto, result);
    }

    @Test
    public void shouldGetItemByIdTest() {
        ItemDto itemDto = ItemMapper.toItemDto(item);
        Mockito.when(itemService.getItemById(user.getId(), item.getId())).thenReturn(itemDto);
        ItemDto result = itemController.getItemById(user.getId(), item.getId());
        assertEquals(itemDto, result);
    }

    @Test
    public void shouldGetItemsByUserTest() {
        ItemDto itemDto = ItemMapper.toItemDto(item);
        List<ItemDto> items = List.of(itemDto);
        int from = 0;
        int size = 10;
        Mockito.when(itemService.getUserItems(user.getId(), from, size)).thenReturn(items);
        List<ItemDto> result = itemController.getItemsByUser(user.getId(), from, size);
        assertEquals(items, result);
    }

    @Test
    public void shouldSearchItemsTest() {
        String text = "name";
        int from = 0;
        int size = 10;
        ItemDto itemDto = ItemMapper.toItemDto(item);
        List<ItemDto> items = List.of(itemDto);
        Mockito.when(itemService.searchItems(text, from, size)).thenReturn(items);
        List<ItemDto> result = itemController.searchItems(text, from, size);
        assertEquals(items, result);
    }

    @Test
    public void shouldCreateCommentTest() {
        User booker = User.builder().id(2L).name("Booker name").email("booker@gmail.com").build();
        ItemDto itemDto = ItemMapper.toItemDto(item);
        CommentDto commentDto = CommentDto.builder().id(1L).text("comment").itemDto(itemDto).authorName("Booker name")
                .created(LocalDateTime.now()).build();
        Mockito.when(itemService.create(commentDto, item.getId(), booker.getId())).thenReturn(commentDto);
        CommentDto result = itemController.create(commentDto, booker.getId(), itemDto.getId());
        assertEquals(commentDto, result);
    }
}
