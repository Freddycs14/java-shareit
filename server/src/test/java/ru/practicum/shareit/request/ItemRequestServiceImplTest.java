package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.exception.ItemRequestNotFoundException;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class ItemRequestServiceImplTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private ItemRequestRepository itemRequestRepository;

    @Mock
    private ItemService itemService;

    @Mock
    private ItemRepository itemRepository;

    @InjectMocks
    private ItemRequestServiceImpl itemRequestService;

    private final Long userId = 1L;
    private final Long requestId = 1L;

    @Test
    public void shouldCreateRequest() {
        ItemRequestDto itemRequestDto = ItemRequestDto.builder().build();
        itemRequestDto.setDescription("description");
        User requestor = User.builder().id(userId).build();
        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.of(requestor));
        Mockito.when(itemRequestRepository.save(Mockito.any(ItemRequest.class))).thenReturn(new ItemRequest());
        Mockito.when(itemService.getItemsByRequestId(Mockito.any())).thenReturn(new ArrayList<>());

        ItemRequestDto result = itemRequestService.create(itemRequestDto, userId);

        assertNotNull(result);
        assertEquals(itemRequestDto.getDescription(), result.getDescription());
        assertEquals(requestor.getId(), result.getRequestor().getId());
    }

    @Test
    public void shouldCreateRequestWithEmptyDescriptionTest() {
        ItemRequestDto itemRequestDto = ItemRequestDto.builder().build();
        assertThrows(ValidationException.class, () -> itemRequestService.create(itemRequestDto, userId));
    }

    @Test
    public void shouldCreateRequestWithoutUserTest() {
        ItemRequestDto itemRequestDto = ItemRequestDto.builder().description("description").build();
        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.empty());
        assertThrows(UserNotFoundException.class, () -> itemRequestService.create(itemRequestDto, userId));
    }

    @Test
    public void shouldGetUserRequestTest() {
        User user = User.builder().id(userId).name("name").email("email").build();
        ItemRequest itemRequest = ItemRequest.builder().id(1L).description("desc").requestor(user).build();
        List<ItemRequest> itemRequests = List.of(itemRequest);
        ItemDto itemDto = ItemDto.builder().available(true).requestId(userId).name("name").description("desc").build();
        List<ItemDto> itemDtos = List.of(itemDto);

        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        Mockito.when(itemRequestRepository.findItemRequestByRequestorIdOrderByCreatedDesc(userId)).thenReturn(itemRequests);

        ItemRequestDto itemRequestDto = ItemRequestMapper.toItemRequestDto(itemRequest, itemDtos);
        List<ItemRequestDto> result = itemRequestService.getUserRequest(userId);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(itemRequestDto.getDescription(), result.get(0).getDescription());
        assertEquals(itemRequestDto.getRequestor(), result.get(0).getRequestor());
    }

    @Test
    public void shouldGetUserWithoutUserTest() {
        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.empty());
        assertThrows(UserNotFoundException.class, () -> itemRequestService.getUserRequest(userId));
    }

    @Test
    public void shouldGetOtherUserTest() {
        User user = User.builder().id(userId).name("user").email("user@gmail.com").build();
        User requestor = User.builder().id(2L).name("name").email("requestor@gmail.com").build();
        int size = 10;
        int from = 0;
        ItemRequest itemRequest = ItemRequest.builder().id(1L).description("description").requestor(requestor).build();
        List<ItemRequest> itemRequests = new ArrayList<>();
        itemRequests.add(itemRequest);
        Page<ItemRequest> page = new PageImpl<>(itemRequests);
        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        Mockito.when(itemRequestRepository.findAll(Mockito.any(PageRequest.class))).thenReturn(page);

        List<ItemRequestDto> result = itemRequestService.getOtherUserRequests(userId, from, size);

        assertNotNull(result);
        assertEquals(itemRequests.size(), result.size());
    }

        @Test
        public void shouldGetOtherUserWithoutUserTest() {
            int from = 0;
            int size = 10;
            Mockito.when(userRepository.findById(userId)).thenReturn(Optional.empty());
            assertThrows(UserNotFoundException.class, () -> itemRequestService.getOtherUserRequests(userId, from, size));
        }

        @Test
        public void shouldGetOtherUserWithInvalidParametersTest() {
            int from = -1;
            int size = 0;
            assertThrows(ValidationException.class, () -> itemRequestService.getOtherUserRequests(userId, from, size));
        }

    @Test
    public void shouldGetRequestTest() {
        User user = User.builder().id(userId).name("name").email("email").build();
        ItemRequest itemRequest = ItemRequest.builder().id(requestId).description("description").requestor(user).build();
        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        Mockito.when(itemRequestRepository.findById(requestId)).thenReturn(Optional.of(itemRequest));
        Mockito.when(itemService.getItemsByRequestId(Mockito.anyLong())).thenReturn(new ArrayList<>());

        ItemRequestDto result = itemRequestService.getRequest(userId, requestId);
        assertNotNull(result);
        assertEquals(itemRequest.getId(), result.getId());
    }

    @Test
    public void shouldGetRequestWithoutUserTest() {
        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.empty());
        assertThrows(UserNotFoundException.class, () -> itemRequestService.getRequest(userId, requestId));
    }

    @Test
    public void shouldGetRequestWithoutRequestTest() {
        User user = User.builder().id(userId).build();
        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        Mockito.when(itemRequestRepository.findById(requestId)).thenReturn(Optional.empty());
        assertThrows(ItemRequestNotFoundException.class, () -> itemRequestService.getRequest(userId, requestId));
    }
}
