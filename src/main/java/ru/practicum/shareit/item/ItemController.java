package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import javax.validation.Valid;
import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@Slf4j
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemService service;

    @PostMapping
    public ItemDto create(@RequestHeader("X-Sharer-User-Id") Long userId, @Valid @RequestBody ItemDto itemDto) {
        log.info("Получен запрос на создание вещи");
        return service.create(userId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public ItemDto update(@RequestHeader("X-Sharer-User-Id") Long userId, @RequestBody ItemDto itemDto, @PathVariable Long itemId) {
        itemDto.setId(itemId);
        log.info("Получен запрос на обновление вещи");
        return service.update(itemId, userId, itemDto);
    }

    @GetMapping("/{itemId}")
    public ItemDto getItemById(@RequestHeader("X-Sharer-User-Id") Long userId, @PathVariable Long itemId) {
        log.info("Получен запрос на получение вещи с id={}", itemId);
        return service.getItemById(userId, itemId);
    }

    @GetMapping
    public List<ItemDto> getItemsByUser(@RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Получен запрос на получение списка всех вещей пользователя с id={}", userId);
        return service.getUserItems(userId);
    }

    @GetMapping("/search")
    public List<ItemDto> searchItems(@RequestParam String text) {
        log.info("Получен запрос на поиск вещи по тексту");
        return service.searchItems(text);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto create(@Valid @RequestBody CommentDto commentDto,
                             @RequestHeader("X-Sharer-User-Id") Long userId, @PathVariable Long itemId) {
        log.info("Получен запрос на добавление комментария");
        return service.create(commentDto, itemId, userId);
    }
}
