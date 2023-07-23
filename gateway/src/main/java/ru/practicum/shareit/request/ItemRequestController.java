package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Slf4j
public class ItemRequestController {
    private final ItemRequestClient itemRequestClient;

    @PostMapping
    public ResponseEntity<Object> create(@Validated @RequestBody ItemRequestDto itemRequestDto,
                                         @RequestHeader(value = "X-Sharer-User-Id") Long requesterId) {
        log.info("Добавление нового запроса вещи");
        return itemRequestClient.create(requesterId, itemRequestDto);
    }

    @GetMapping
    public ResponseEntity<Object> getUserRequests(@NotNull @RequestHeader("X-Sharer-User-Id") Long requestorId) {
        log.info("Получение списка запросов пользователя");
        return itemRequestClient.getUserRequests(requestorId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getOtherUserRequests(@NotNull @RequestHeader("X-Sharer-User-Id") Long requesterId,
                                                       @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") int from,
                                                       @Positive @RequestParam(name = "size", defaultValue = "20") int size) {
        log.info("Получение списка запросов, созданных другими пользователями");
        return itemRequestClient.getOtherUserRequests(requesterId, from, size);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getRequest(@NotNull @RequestHeader("X-Sharer-User-Id") Long userId,
                                             @NotNull @PathVariable Long requestId) {
        log.info("Получени данных об одном конкретном запросе");
        return itemRequestClient.getRequest(userId, requestId);
    }
}
