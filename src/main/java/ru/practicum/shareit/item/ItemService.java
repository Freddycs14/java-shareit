package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {
    ItemDto create(long userId, ItemDto itemDto);

    ItemDto update(long id, long userId, ItemDto item);

    ItemDto getItemById(long itemId);

    List<ItemDto> getUserItems(long userId);

    List<ItemDto> searchItems(String text);
}
