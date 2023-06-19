package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
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
@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService service;

    @PostMapping
    public UserDto create(@Valid @RequestBody UserDto userDto) {
        User user = service.create(UserMapper.toUser(userDto));
        return UserMapper.toUserDto(user);
    }

    @PatchMapping("/{userId}")
    public UserDto update(@PathVariable long userId, @RequestBody UserDto userDto) {
        userDto.setId(userId);
        User user = service.update(UserMapper.toUser(userId, userDto));
        return UserMapper.toUserDto(user);
    }

    @DeleteMapping("/{userId}")
    public void remove(@PathVariable long userId) {
        service.remove(userId);
    }

    @GetMapping("/{userId}")
    public UserDto getUserById(@PathVariable long userId) {
        User user = service.getUserById(userId);
        return UserMapper.toUserDto(user);
    }

    @GetMapping
    public List<UserDto> getAllUsers() {
        return service.getAllUser().stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }
}
