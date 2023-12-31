package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

/**
 * TODO Sprint add-controllers.
 */

@Slf4j
@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService service;

    @PostMapping
    public UserDto create(@Valid @RequestBody UserDto userDto) {
        log.info("Получен запрос на создание пользователя");
        User user = service.create(UserMapper.toUser(userDto));
        return UserMapper.toUserDto(user);
    }

    @PatchMapping("/{userId}")
    public UserDto update(@PathVariable Long userId, @RequestBody UserDto userDto) {
        log.info("Получен запрос на обновление пользователя с id={}", userId);
        userDto.setId(userId);
        User user = service.update(userId, UserMapper.toUser(userId, userDto));
        return UserMapper.toUserDto(user);
    }

    @DeleteMapping("/{userId}")
    public void remove(@PathVariable Long userId) {
        log.info("Получен запрос на удаление пользователя");
        service.remove(userId);
    }

    @GetMapping("/{userId}")
    public UserDto getUserById(@PathVariable Long userId) {
        log.info("Получен запрос на получение пользователя с id={}", userId);
        User user = service.getUserById(userId);
        return UserMapper.toUserDto(user);
    }

    @GetMapping
    public List<UserDto> getAllUsers() {
        log.info("Получен запрос на получение списка всех пользователей");
        return service.getAllUser().stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }
}
