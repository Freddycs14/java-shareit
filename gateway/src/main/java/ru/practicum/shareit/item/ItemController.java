package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.PositiveOrZero;

@RestController
@RequestMapping(path = "/items")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemController {
    private final ItemClient itemClient;

    @PostMapping
    public ResponseEntity<Object> create(@RequestHeader(value = "X-Sharer-User-Id", required = false) Long ownerId,
                                         @Valid @RequestBody ItemDto itemDto) {
        log.info("Получен запрос на создание вещи");
        return itemClient.create(ownerId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> update(@RequestHeader(value = "X-Sharer-User-Id") Long userId,
                                         @RequestBody ItemDto itemDto,
                                         @PathVariable Long itemId) {
        if (itemDto.getName() == null && itemDto.getDescription() == null && itemDto.getAvailable() == null) {
            throw new ValidationException("Некорректный запрос");
        }
        log.info("Получен запрос на обновление вещи");
        return itemClient.update(userId, itemDto, itemId);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItemById(@PathVariable Long itemId,
                                              @RequestHeader(value = "X-Sharer-User-Id") Long ownerId) {
        log.info("Получен запрос на получение вещи с id={}", itemId);
        return itemClient.getItemById(itemId, ownerId);
    }

    @GetMapping
    public ResponseEntity<Object> getItemsByUser(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                 @Min(0) @RequestParam(name = "from", defaultValue = "0") Integer from,
                                                 @Min(1) @RequestParam(name = "size", defaultValue = "10") Integer size) {
        log.info("Получен запрос на получение списка всех вещей пользователя с id={}", userId);
        return itemClient.getItemsByUser(userId, from, size);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchItems(@RequestHeader("X-Sharer-User-Id") Long userId,
                                              @RequestParam(value = "text", required = false) String text,
                                              @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                              @Min(1) @RequestParam(name = "size", defaultValue = "10") Integer size) {
        log.info("Получен запрос на поиск вещи по тексту");
        return itemClient.searchItems(userId, text, from, size);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> create(@Valid @RequestBody CommentDto commentDto,
                                         @RequestHeader("X-Sharer-User-Id") Long userId,
                                         @PathVariable Long itemId) {
        log.info("Получен запрос на добавление комментария");
        return itemClient.create(userId, itemId, commentDto);
    }
}
