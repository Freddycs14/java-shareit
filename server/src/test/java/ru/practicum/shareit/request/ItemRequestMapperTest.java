package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;

import java.time.LocalDateTime;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ItemRequestMapperTest {

    private final UserDto user = UserDto.builder().id(1L).name("Walter").email("w.white@gmail.com").build();

    @Test
    public void toItemRequestTest() {
        ItemRequestDto itemRequestDto = ItemRequestDto.builder().id(1L).description("blue ice").created(LocalDateTime.now()).requestor(user).items(new ArrayList<>()).build();
        ItemRequest itemRequest = ItemRequestMapper.toItemRequest(itemRequestDto);
        assertEquals(itemRequestDto.getId(), itemRequest.getId());
        assertEquals(itemRequestDto.getDescription(), itemRequest.getDescription());
        assertEquals(itemRequestDto.getRequestor(), UserMapper.toUserDto(itemRequest.getRequestor()));
        assertEquals(itemRequestDto.getCreated(), itemRequest.getCreated());
    }

    @Test
    public void toItemRequestDtoTest() {
        ItemRequest itemRequest = ItemRequest.builder().id(1L).description("white ice").created(LocalDateTime.now()).requestor(UserMapper.toUser(user)).build();
        ItemRequestDto itemRequestDto = ItemRequestMapper.toItemRequestDto(itemRequest, new ArrayList<>());
        assertEquals(itemRequestDto.getId(), itemRequest.getId());
        assertEquals(itemRequestDto.getDescription(), itemRequest.getDescription());
        assertEquals(itemRequestDto.getRequestor(), UserMapper.toUserDto(itemRequest.getRequestor()));
        assertEquals(itemRequestDto.getCreated(), itemRequest.getCreated());
    }
}
