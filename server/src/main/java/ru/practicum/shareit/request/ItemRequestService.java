package ru.practicum.shareit.request;

import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.List;

public interface ItemRequestService {
    ItemRequestDto create(ItemRequestDto itemRequestDto, Long userId);

    List<ItemRequestDto> getUserRequest(Long userId);

    List<ItemRequestDto> getOtherUserRequests(Long userId, int from, int size);

    ItemRequestDto getRequest(Long userId, Long requestId);
}
