package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.NumberFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;

import javax.validation.constraints.NotNull;

@Controller
@RequestMapping(path = "/users")
@Slf4j
@RequiredArgsConstructor
@Validated
public class UserController {
    private final UserClient userClient;

    @PostMapping
    public ResponseEntity<Object> create(@Validated @RequestBody UserDto userDto) {
        log.info("Получен запрос на создание пользователя");
        return userClient.create(userDto);
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<Object> update(@PathVariable Long userId, @RequestBody UserDto userDto) {
        log.info("Получен запрос на обновление пользователя с id={}", userId);
        return userClient.update(userDto, userId);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Object> remove(@NumberFormat @PathVariable Long userId) {
        log.info("Получен запрос на удаление пользователя");
        return userClient.remove(userId);
    }


    @GetMapping
    public ResponseEntity<Object> getAllUsers() {
        log.info("Получен запрос на получение списка всех пользователей");
        return userClient.getAllUser();
    }

    @GetMapping("/{userId}")
    public ResponseEntity<Object> getUserById(@NotNull @PathVariable Long userId) {
        log.info("Получен запрос на получение пользователя с id={}", userId);
        return userClient.getUserById(userId);
    }
}
