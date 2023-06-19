package ru.practicum.shareit.item;

import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemStorage {
    Item create(Item item);

    Item update(Item item);

    Item getItemById(Long itemId);

    List<Item> getUserItems(Long userId);

    List<Item> searchItems(String search);
}
