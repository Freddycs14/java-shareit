package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.exception.ItemNotFoundException;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.comment.CommentRepository;
import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.comment.mapper.CommentMapper;
import ru.practicum.shareit.item.comment.model.Comment;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

@ExtendWith(MockitoExtension.class)
public class ItemServiceImplTest {
    @Mock
    private ItemRepository itemRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private CommentRepository commentRepository;

    @InjectMocks
    private ItemServiceImpl itemService;

    private User owner;
    private User requestor;
    private Item item;
    private ItemRequest itemRequest;

    @BeforeEach
    public void setUp() {
        owner = User.builder().id(1L).name("Walter").email("w.white@gmail.com").build();
        requestor = User.builder().id(2L).name("Jesse").email("j.pinkman@gmail.com").build();
        itemRequest = ItemRequest.builder().id(1L).requestor(requestor).description("description").created(LocalDateTime.now()).build();
        item = Item.builder().id(1L).name("name").description("description").owner(owner).available(true).requestId(itemRequest.getId()).build();
    }


    @Test
    public void shouldCreateItemTest() {
        item.setRequestId(itemRequest.getId());
        ItemDto itemDto = ItemMapper.toItemDto(item);
        Mockito.when(userRepository.findById(owner.getId())).thenReturn(Optional.of(owner));
        ItemDto result = itemService.create(owner.getId(), itemDto);
        assertEquals(item, ItemMapper.toItem(result, owner));
    }

    @Test
    public void shouldCreateItemWithNullOwnerIdTest() {
        Long userId = null;
        ItemDto itemDto = ItemMapper.toItemDto(item);
        assertThrows(ValidationException.class, () -> itemService.create(userId, itemDto));
    }

    @Test
    public void shouldUpdateItemTest() {
        Long itemId = item.getId();
        Item updateItem = Item.builder().name("Update name").description("Update descriptionOne").owner(owner).available(true).requestId(itemRequest.getId()).build();
        updateItem.setId(itemId);
        ItemDto updateItemDto = ItemMapper.toItemDto(updateItem);
        Mockito.when(itemRepository.findById(itemId)).thenReturn(Optional.of(updateItem));
        Mockito.when(itemRepository.save(updateItem)).thenReturn(updateItem);
        ItemDto result = itemService.update(itemId, owner.getId(), updateItemDto);
        assertEquals(updateItemDto, result);
    }

    @Test
    public void shouldUpdateItemWithNullOwnerIdTest() {
        Long userId = null;
        ItemDto itemDto = ItemDto.builder().id(1L).name("Update name").description("Update descriptionOne").available(true).build();
        assertThrows(ValidationException.class, () -> itemService.update(itemDto.getId(), userId, itemDto));
    }

    @Test
    public void shouldUpdateItemWhenItemNotFoundTest() {
        ItemDto itemDto = ItemDto.builder().name("Update name").description("Update descriptionOne").available(true).build();
        assertThrows(ItemNotFoundException.class, () -> itemService.update(itemDto.getId(), owner.getId(), itemDto));
    }

    @Test
    public void shouldUpdateItemWhenUserNotOwnerTest() {
        Long itemId = item.getId();
        Item updateItem = Item.builder().name("Update name").description("Update descriptionOne").owner(owner).available(true).requestId(itemRequest.getId()).build();
        updateItem.setId(itemId);
        ItemDto updateItemDto = ItemMapper.toItemDto(updateItem);
        Mockito.when(itemRepository.findById(itemId)).thenReturn(Optional.of(updateItem));
        assertThrows(UserNotFoundException.class, () -> itemService.update(itemId, requestor.getId(), updateItemDto));
    }

    @Test
    public void shouldGetItemByIdTest() {
        ItemDto itemDto = ItemMapper.toItemDto(item);
        itemDto.setComments(new ArrayList<>());
        Booking booking = Booking.builder().item(item).booker(requestor).start(LocalDateTime.now().minusMinutes(1)).end(LocalDateTime.now()).build();
        Mockito.when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));
        Mockito.when(bookingRepository.findAll()).thenReturn(Arrays.asList(booking));
        ItemDto result = itemService.getItemById(requestor.getId(), item.getId());
        assertNotNull(result);
        assertEquals(itemDto, result);
    }

    @Test
    public void shouldGetItemByIdWithNullOwnerIdTest() {
        Long itemId = 1L;
        Long requestorId = null;
        assertThrows(ValidationException.class, () -> itemService.getItemById(requestorId, itemId));
    }

    @Test
    public void shouldGetItemByIdWithNullItemIdTest() {
        Long itemId = null;
        Long requestorId = 1L;
        assertThrows(ValidationException.class, () -> itemService.getItemById(requestorId, itemId));
    }

    @Test
    public void shouldGetItemByIdWhenItemNotFoundTest() {
        Long itemId = 1L;
        Long requestorId = 1L;
        Mockito.when(itemRepository.findById(itemId)).thenReturn(Optional.empty());
        assertThrows(ItemNotFoundException.class, () -> itemService.getItemById(requestorId, itemId));
    }

    @Test
    public void shouldGetUserItemsTest() {
        Long userId = 1L;
        int from = 0;
        int size = 10;
        Item item2 = Item.builder().id(2L).name("name2").description("description2").owner(owner).available(true).build();
        List<Item> items = new ArrayList<>();
        items.add(item);
        items.add(item2);
        Mockito.when(itemRepository.findItemByOwnerId(eq(userId), any(PageRequest.class))).thenReturn(items);
        List<ItemDto> result = itemService.getUserItems(userId, from, size);
        assertEquals(items.size(), result.size());
        assertNotNull(result);
    }

    @Test
    public void shouldGetUserItemsWithNullOwnerIdTest() {
        Long userId = null;
        int from = 0;
        int size = 10;
        assertThrows(ValidationException.class, () -> itemService.getUserItems(userId, from, size));
    }

    @Test
    public void shouldSearchItems() {
        List<Item> items = new ArrayList<>();
        items.add(item);
        ItemDto itemDto = ItemMapper.toItemDto(item);
        itemDto.setComments(new ArrayList<>());
        Mockito.when(itemRepository.search("name", PageRequest.of(1, 1))).thenReturn(items);
        List<ItemDto> result = itemService.searchItems("name", 1, 1);
        assertEquals(1, result.size());
        assertEquals(itemDto, result.get(0));
    }

    @Test
    public void shouldCreateCommentTest() {
        Booking booking = Booking.builder().item(item).booker(requestor).status(BookingStatus.APPROVED).start(LocalDateTime.now().minusMinutes(1)).end(LocalDateTime.now()).build();
        booking.setId(1L);
        CommentDto commentDto = CommentDto.builder().text("comment").build();
        Comment comment = CommentMapper.toComment(commentDto, item, requestor);
        Mockito.when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));
        Mockito.when(userRepository.findById(requestor.getId())).thenReturn(Optional.of(requestor));
        Mockito.when(bookingRepository.findFirstByBookerIdAndItemIdAndEndIsBefore(any(Long.class), any(Long.class),
                any(LocalDateTime.class), eq(Sort.by("end").descending()))).thenReturn(booking);
        Mockito.when(commentRepository.save(any(Comment.class))).thenAnswer(invocationOnMock -> {
            comment.setId(1L);
            return comment;
        });
        CommentDto result = itemService.create(commentDto, item.getId(), requestor.getId());
        assertEquals(commentDto.getText(), result.getText());
        assertEquals(ItemMapper.toItemDto(item), result.getItemDto());
        assertEquals(requestor.getName(), result.getAuthorName());
    }

    @Test
    public void shouldCreateCommentWhenOwnerNotFoundTest() {
        Long userId = 5L;
        Booking booking = Booking.builder().item(item).booker(requestor).status(BookingStatus.APPROVED).start(LocalDateTime.now().minusMinutes(1)).end(LocalDateTime.now()).build();
        booking.setId(1L);
        CommentDto commentDto = CommentDto.builder().text("comment").build();
        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> itemService.create(commentDto, item.getId(), userId));
    }

    @Test
    public void shouldCreateCommentWhenItemNotFoundTest() {
        Long itemId = 5L;
        Booking booking = Booking.builder().item(item).booker(requestor).status(BookingStatus.APPROVED).start(LocalDateTime.now().minusMinutes(1)).end(LocalDateTime.now()).build();
        booking.setId(1L);
        CommentDto commentDto = CommentDto.builder().text("comment").build();
        Mockito.when(itemRepository.findById(itemId)).thenReturn(Optional.empty());
        Mockito.when(userRepository.findById(requestor.getId())).thenReturn(Optional.of(requestor));

        assertThrows(ItemNotFoundException.class, () -> itemService.create(commentDto, itemId, requestor.getId()));
    }
}
