package ru.practicum.shareit.item;

import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {
    ItemDto create(Long userId, ItemDto itemDto);

    ItemDto update(Long id, Long userId, ItemDto item);

    ItemDto getItemById(Long userId, Long itemId);

    List<ItemDto> getUserItems(Long userId, int from, int size);

    List<ItemDto> searchItems(String text, int from, int size);

    CommentDto create(CommentDto commentDto, Long itemId, Long userId);
    List<ItemDto> getItemsByRequestId (Long itemRequestId);
}
