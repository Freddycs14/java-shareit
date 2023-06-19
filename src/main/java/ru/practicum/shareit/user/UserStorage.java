package ru.practicum.shareit.user;

import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserStorage {
    User create(User user);

    User update(User user);

    void remove(Long userId);

    User getUserById(Long userId);

    List<User> getAllUsers();
}
