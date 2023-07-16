package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class ItemRequestControllerTest {

    @Mock
    private ItemRequestService service;

    @InjectMocks
    private ItemRequestController controller;
    private Long userId = 1L;
    private Long requestId = 1L;

    @Test
    public void shouldCreateRequest() {
        ItemRequestDto itemRequest = ItemRequestDto.builder().build();
        ItemRequestDto itemRequestExpected = ItemRequestDto.builder().build();

        Mockito.when(service.create(itemRequest, userId)).thenReturn(itemRequestExpected);
        ItemRequestDto result = controller.created(itemRequest, userId);
        assertEquals(itemRequestExpected, result);
    }

    @Test
    public void shouldGetRequest() {
        ItemRequestDto itemRequestExpected = ItemRequestDto.builder().build();
        Mockito.when(service.getRequest(userId, requestId)).thenReturn(itemRequestExpected);

        ItemRequestDto result = controller.getRequest(userId, requestId);
        assertEquals(itemRequestExpected, result);
    }

    @Test
    public void shouldGetUserRequest() {
        List<ItemRequestDto> itemRequestsExpected = new ArrayList<>();
        Mockito.when(service.getUserRequest(userId)).thenReturn(itemRequestsExpected);

        List<ItemRequestDto> result = controller.getUserRequests(userId);
        assertEquals(itemRequestsExpected, result);
    }

    @Test
    public void shouldGetOtherUserRequest() {
        int from = 0;
        int size = 1;
        List<ItemRequestDto> itemRequestsExpected = new ArrayList<>();
        Mockito.when(service.getOtherUserRequests(userId, from, size)).thenReturn(itemRequestsExpected);

        List<ItemRequestDto> result = controller.getOtherUserRequests(userId, from, size);
        assertEquals(itemRequestsExpected, result);
    }
}
