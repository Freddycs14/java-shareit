package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserStorage;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ItemServiceImpl implements ItemService {
    private final ItemStorage itemStorage;
    private final ItemMapper itemMapper;
    private final UserStorage userStorage;

    @Override
    public ItemDto create(long userId, ItemDto itemDto) {
        checkUser(userId);
        User user = userStorage.getUserById(userId);
        Item item = itemMapper.toItem(itemDto, user);
        itemStorage.create(item);
        return itemMapper.toItemDto(item);
    }

    @Override
    public ItemDto update(long id, long userId, ItemDto itemDto) {
        checkUser(userId);
        if (userId != itemStorage.getItemById(id).getOwner().getId()) {
            throw new UserNotFoundException("Только владелец может вносить измениния");
        }
        User user = userStorage.getUserById(userId);
        Item updateItem = itemMapper.toItem(id, itemDto, user);
        updateItem = itemStorage.update(updateItem);
        return itemMapper.toItemDto(updateItem);
    }

    @Override
    public ItemDto getItemById(long itemId) {
        return itemMapper.toItemDto(itemStorage.getItemById(itemId));
    }

    @Override
    public List<ItemDto> getUserItems(long userId) {
        return itemStorage.getUserItems(userId)
                .stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> searchItems(String text) {
        return itemStorage.searchItems(text)
                .stream()
                .filter(Item::getAvailable)
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    private void checkUser(long userId) {
        if (userStorage.getUserById(userId) == null) {
            throw new UserNotFoundException("Пользователь не найден");
        }
    }
}
