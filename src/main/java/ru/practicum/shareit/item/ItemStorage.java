package ru.practicum.shareit.item;

import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemStorage {
    Item create(Item item);

    Item update(Item item);

    Item getItemById(long itemId);

    List<Item> getUserItems(long userId);

    List<Item> searchItems(String search);
}
