package ru.practicum.shareit.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.ItemNotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.model.Item;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component("InMemoryItemStorage")
@Slf4j
public class InMemoryItemStorage implements ItemStorage {
    private final Map<Long, Item> items = new HashMap<>();
    private long id = 1;

    private long getNextId() {
        return id++;
    }

    @Override
    public Item create(Item item) {
        item.setId(getNextId());
        items.put(item.getId(), item);
        return item;
    }

    @Override
    public Item update(Item item) {
        if (items.containsKey(item.getId())) {
            Item updateItem = items.get(item.getId());
            if (updateItem.getId() == (item.getId())) {
                if (item.getOwner() == updateItem.getOwner()) {
                    if (item.getAvailable() != null) {
                        updateItem.setAvailable(item.getAvailable());
                    }
                    if (item.getDescription() != null) {
                        updateItem.setDescription(item.getDescription());
                    }
                    if (item.getOwner() != null) {
                        updateItem.setOwner(item.getOwner());
                    }
                    if (item.getRequest() != null) {
                        updateItem.setRequest(item.getRequest());
                    }
                    if (item.getName() != null) {
                        updateItem.setName(item.getName());
                    }
                    return updateItem;
                }
                throw new ValidationException("Пользователь с id " + item.getOwner().getId() +
                        " не является владельцем вещи с id " + item.getId());
            }
        }
        throw new ItemNotFoundException("Вещь с id " + item.getId() + " не найдена");
    }

    @Override
    public Item getItemById(long itemId) {
        if (items.containsKey(itemId)) {
            return items.get(itemId);
        }
        throw new ItemNotFoundException("Вещь с id " + itemId + " не найдена");
    }

    @Override
    public List<Item> getUserItems(long userId) {
        return items.values().stream()
                .filter(item -> item.getOwner().getId() == userId)
                .collect(Collectors.toList());
    }

    @Override
    public List<Item> searchItems(String text) {
        if (text.isEmpty()) {
            return new ArrayList<>();
        }
        return items.values().stream()
                .filter(item -> (item.getName().toLowerCase().contains(text.toLowerCase()) ||
                        item.getDescription().toLowerCase().contains(text.toLowerCase())))
                .filter(item -> item.getAvailable().equals(true))
                .collect(Collectors.toList());
    }
}
