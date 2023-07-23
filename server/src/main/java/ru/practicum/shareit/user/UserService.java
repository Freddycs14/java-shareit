package ru.practicum.shareit.user;

import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserService {
    User create(User user);

    User update(Long userId, User user);

    void remove(Long userId);

    User getUserById(Long userId);

    List<User> getAllUser();
}
