package ru.practicum.shareit.request;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

/**
 * TODO Sprint add-item-requests.
 */
@Slf4j
@RestController
//@RequiredArgsConstructor
@RequestMapping(path = "/requests")
public class ItemRequestController {
    private ItemRequestService itemRequestService;
    @Autowired
    public ItemRequestController(ItemRequestService itemRequestService) {
        this.itemRequestService = itemRequestService;
    }

    @PostMapping
    public ItemRequestDto created(@Valid @RequestBody ItemRequestDto itemRequestDto,
                                  @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Добавление нового запроса вещи");
        return itemRequestService.create(itemRequestDto, userId);
    }

    @GetMapping
    public List<ItemRequestDto> getUserRequests (@RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Получение списка запросов пользователя");
        return itemRequestService.getUserRequest(userId);
    }

    @GetMapping("/all")
    public List<ItemRequestDto> getOtherUserRequests(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                     @RequestParam(defaultValue = "0") @PositiveOrZero int from,
                                                     @RequestParam(defaultValue = "1") @Positive int size) {
        log.info("Получение списка запросов, созданных другими пользователями");
        return itemRequestService.getOtherUserRequests(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public ItemRequestDto getRequest(@RequestHeader("X-Sharer-User-Id") Long userId,
                                     @PathVariable Long requestId) {
        log.info("Получени данных об одном конкретном запросе");
        return itemRequestService.getRequest(userId, requestId);
    }

}
