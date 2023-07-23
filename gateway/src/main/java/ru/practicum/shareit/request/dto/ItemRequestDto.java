package ru.practicum.shareit.request.dto;

import lombok.AccessLevel;
import lombok.Value;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;
import java.util.List;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Value
public class ItemRequestDto {
    Long id;
    String description;
    UserDto requestor;
    LocalDateTime created;
    List<ItemDto> items;
}
